package com.agitation.sportman.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.entity.FormFile;
import com.agitation.sportman.fragment.MyCenter;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.MultiUploadThread;
import com.agitation.sportman.utils.ToastUtils;
import com.agitation.sportman.widget.CircleImageView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by fanwl on 2015/11/7.
 */
public class UserInfoEdit extends BaseActivity implements View.OnClickListener {

    private LinearLayout mycenter_edit_head,mycenter_edit_name,mycenter_edit_sex,mycenter_edit_age,mycenter_edit_phone,mycenter_edit_address;
    private AQuery aq;
    private DataHolder dataHolder;
    private TextView centerUserName,centerUserSex,centerUserAge,centerUserAddress,centerUserPhone;

    //设置我的中心的头像
    private final int CHOOSE_FROM_CAMERA = 2;
    private final int CHOOSE_FROM_ALBUM = 1;
    private final int CHOOSE_FROM_CROP = 3;
    public static final int CHOOSE_FROM_ADDRESS = 145;
    private File file = null;
    private File takePhotoFile = null;
    private CircleImageView head_portrait;
    private static final int DIALOG_DATE_ID = 0;
    private int Year,Month,Day;
    private ImageLoader imageLoader;

    //上传文件
    private FormFile formFile;
    private boolean editHead = false;
    private String head;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MultiUploadThread.UPLOAD_SUCCESS:
                    head = msg.obj+"";
                    dataHolder.getUserData().put("head", head);
                    ToastUtils.showToast(UserInfoEdit.this, "修改成功:"+head);
                    editHead = true;
                    if (file!=null)file.delete();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.center_data_edit);
            initToorbar();
            initVarble();
            initView();
            setUserData();
    }

    private void initView() {
        centerUserSex = (TextView) findViewById(R.id.edit_sex);
        centerUserName = (TextView) findViewById(R.id.user_data_name_edit);
        centerUserAge = (TextView) findViewById(R.id.user_data__age_edit);
        centerUserAddress = (TextView) findViewById(R.id.user_data_address_edit);
        centerUserPhone = (TextView) findViewById(R.id.user_data_phone_edit);
        mycenter_edit_head = (LinearLayout) findViewById(R.id.mycenter_edit_head);
        mycenter_edit_name = (LinearLayout) findViewById(R.id.mycenter_edit_name);
        mycenter_edit_sex = (LinearLayout) findViewById(R.id.mycenter_edit_sex);
        mycenter_edit_age = (LinearLayout) findViewById(R.id.mycenter_edit_age);
        mycenter_edit_phone = (LinearLayout) findViewById(R.id.mycenter_edit_phone);
        mycenter_edit_address = (LinearLayout) findViewById(R.id.mycenter_edit_address);
        head_portrait = (CircleImageView)findViewById(R.id.head_portrait);
        findViewById(R.id.exit_landing).setOnClickListener(this);
        mycenter_edit_head.setOnClickListener(this);
        mycenter_edit_name.setOnClickListener(this);
        mycenter_edit_sex.setOnClickListener(this);
        mycenter_edit_age.setOnClickListener(this);
        mycenter_edit_phone.setOnClickListener(this);
        mycenter_edit_address.setOnClickListener(this);
        File path = new File(Mark.getFilePath());
        if (!path.exists())path.mkdir();
        file = new File(Mark.getFilePath() + "/head.jpg");
    }

    private void initVarble() {
        imageLoader = ImageLoader.getInstance();
        aq = new AQuery(this);
        dataHolder = DataHolder.getInstance();
        Calendar calendar = Calendar.getInstance();
        Year = calendar.get(calendar.YEAR);
        Month = calendar.get(calendar.MONTH);
        Day = calendar.get(calendar.DAY_OF_MONTH);
    }

    private void initToorbar() {
        if (toolbar!=null){
            title.setText("资料编辑");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editHead){
                    UserInfoEdit.this.setResult(MyCenter.EDIT_PHOTO);
                    finish();
                }else{
                    onBackPressed();
                }
            }
        });
    }
    public void setUserData(){
        if (dataHolder.getUserData()!=null){
            centerUserName.setText(dataHolder.getUserData().get("name")+"");
            centerUserAge.setText(dataHolder.getUserData().get("age")+"");
            centerUserAddress.setText(dataHolder.getUserData().get("address") + "");
            centerUserPhone.setText(dataHolder.getUserData().get("phoneNumber") + "");
            String headImg = dataHolder.getImageProfix() + dataHolder.getUserData().get("head")+"";
            imageLoader.displayImage(headImg,head_portrait);
            if (Boolean.parseBoolean(dataHolder.getUserData().get("sex")+"")){
                centerUserSex.setText("男");
            }else {
                centerUserSex.setText("女");
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mycenter_edit_head:
                ShowPickDialog();
                break;
            case R.id.mycenter_edit_name:
                modiftName();
                break;
            case R.id.mycenter_edit_sex:
                modiftSex();
                break;
            case R.id.mycenter_edit_age:
                showDialog(DIALOG_DATE_ID);
                break;
            case R.id.mycenter_edit_phone:
                modiftPhone();
                break;
            case R.id.mycenter_edit_address:
                startActivityForResult(new Intent(UserInfoEdit.this,UserAddress.class),130);
                break;
            case R.id.exit_landing:
                dataHolder.setIsLogin(false);
                startActivity(new Intent(UserInfoEdit.this, Login.class));
                finish();
                break;
        }
    }

    /*
    修改头像
     */

    public void uploadHead(){
        String url = Mark.getServerIp() + "/baseApi/updateUserHead";

        Map<String, String> param = new HashMap<>();
        param.put("userName", dataHolder.getUserName());
        param.put("passWord", dataHolder.getPassWord());

        MultiUploadThread uploadThread = new MultiUploadThread(url, formFile, handler, param);
        new Thread(uploadThread).start();
    }

    /*
    修改昵称
     */
    public void modiftName(){
        new MaterialDialog.Builder(this).title(R.string.dialog_tip)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                .input("请输入你要修改的昵称", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        if (charSequence.toString().equals("")){
                            ToastUtils.showToast(UserInfoEdit.this,"昵称不能为空");
                            return;
                        }
                        Map<String, Object> param = new HashMap<String, Object>();
                        param.put("action", "updateName");
                        param.put("name", charSequence.toString());
                        modiftUserInfo(param);
                    }
                })
                .show();
    }

    /**
     * 选择性别
     */
    public void modiftSex(){
        final String[] sex = new String[]{"男","女"};
        AlertDialog.Builder sexDialog = new AlertDialog.Builder(this);
        sexDialog.setTitle("请选择性别");
        sexDialog.setItems(sex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String,Object> param = new HashMap<String, Object>();
                param.put("action", "updateSex");
                param.put("sex", i==0?true:false);
                modiftUserInfo(param);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        sexDialog.show();
    }

    /*
    选择年龄
     */
    private void updataDiaplay(){
        StringBuffer sb= new StringBuffer();
        sb.append(Year).append("-").append(Month+1).append("-").append(Day);
        int age = getCurrentAge(Year,Month,Day);
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("action", "updateAge");
        param.put("age", age);
        param.put("brithday",sb.toString());
        modiftUserInfo(param);
        centerUserAge.setText(age+"");
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DIALOG_DATE_ID:
                return new DatePickerDialog(this,setDateCallBack,Year,Month,Day);
        }
        return super.onCreateDialog(id);
    }
    private DatePickerDialog.OnDateSetListener setDateCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Year=year;
            Month=monthOfYear;
            Day=dayOfMonth;
            updataDiaplay();
        }
    };

    /*
    修改手机号
     */
    public void modiftPhone(){
        new MaterialDialog.Builder(this).title(R.string.dialog_tip)
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("请输入你要修改的手机号", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        if (charSequence.toString().equals("")){
                            ToastUtils.showToast(UserInfoEdit.this,"手机号不能为空");
                            return;
                        }
                        Map<String, Object> param = new HashMap<String, Object>();
                        param.put("action", "updatePhoneNumber");
                        param.put("phoneNumber", charSequence.toString());
                        modiftUserInfo(param);
                    }
                })
                .show();
    }
    /*
    想后台提交修改的内容
     */

    public void modiftUserInfo(final Map<String,Object> param){
        String url = Mark.getServerIp()+"/baseApi/updateUser";
        aq.transformer(new MapTransformer()).auth(dataHolder.getBasicHandle())
                .ajax(url, param, Map.class, new AjaxCallback<Map>() {
                    @Override
                    public void callback(String url, Map result, AjaxStatus status) {
                        if (result != null) {
                            boolean isResult = Boolean.parseBoolean(result.get("result") + "");
                            if (isResult) {
                                ToastUtils.showToast(UserInfoEdit.this, "修改成功");
                                String action = param.get("action") + "";
                                if (action.equals("updateName")) {
                                    centerUserName.setText(param.get("name") + "");
                                    dataHolder.getUserData().put("name",param.get("name"));
                                } else if (action.equals("updateSex")) {
                                    if (Boolean.parseBoolean(param.get("sex") + "")) {
                                        centerUserSex.setText("男");
                                        dataHolder.getUserData().put("sex", "true");
                                    } else {
                                        centerUserSex.setText("女");
                                        dataHolder.getUserData().put("sex", "false");
                                    }
                                } else if (action.equals("updateAge")) {
                                    centerUserAge.setText(param.get("age") + "");
                                    dataHolder.getUserData().put("age", param.get("age"));
                                } else if (action.equals("updatePhoneNumber")) {
                                    centerUserPhone.setText(param.get("phoneNumber") + "");
                                    dataHolder.getUserData().put("phoneNumber", param.get("phoneNumber"));
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            // 如果是直接从相册获取
            case CHOOSE_FROM_ALBUM:
                if(data!=null)startPhotoZoom(data.getData());
                break;
            // 如果是调用相机拍照时
            case CHOOSE_FROM_CAMERA:
                if (takePhotoFile!=null)startPhotoZoom(Uri.fromFile(takePhotoFile));
                break;
            // 取得裁剪后的图片
            case CHOOSE_FROM_CROP:
                if(data != null){
                    if (takePhotoFile!=null)takePhotoFile.delete();
                    saveNewHeadImage(data);
                }
                break;
            default:
                break;
        }
        if (resultCode==CHOOSE_FROM_ADDRESS)centerUserAddress.setText(dataHolder.getUserData().get("address") + "");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ShowPickDialog(){
        new android.support.v7.app.AlertDialog.Builder(this)
            .setTitle("设置头像...")
            .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, CHOOSE_FROM_ALBUM);
                }
            })
            .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePhotoFile = new File(Mark.getFilePath() + "/take_photo.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takePhotoFile));
                    startActivityForResult(intent, CHOOSE_FROM_CAMERA);
                }
            }).show();
    }

    private Bitmap decodeUriAsBitmap(Uri uri){

//        Log.e("uri.toString", uri.toString());
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void saveNewHeadImage(Intent picdata) {

        if(picdata.getData()==null)return;

        Bitmap bitmap = decodeUriAsBitmap(picdata.getData());//decode bitmap
        head_portrait.setImageBitmap(bitmap);
        dataHolder.setCenterHeadBit(bitmap);

        if (file==null){
            ToastUtils.showToast(this, "上传错误，请重试！");
            return;
        }

        formFile = new FormFile(file.getName(), file, "file", "application/octet-stream");

        //调用上传方法进行文件上传
        uploadHead();
    }
    /**
     * 裁剪图片方法实现
     */
    public void startPhotoZoom(Uri uri){

        Uri saveUri = Uri.fromFile(file);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CHOOSE_FROM_CROP);
    }

    public int getCurrentAge(int year,int month,int day){
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int cYear = Integer.parseInt(c.get(Calendar.YEAR)+"");
        int cMonth = Integer.parseInt(c.get(Calendar.MONTH)+1+"");
        int cDay = Integer.parseInt(c.get(Calendar.DAY_OF_MONTH)+"");
        int age = cYear -year;
        if (cMonth > month){
            age = age -1;
        }else if (cMonth == month){
            if (cDay > day){
                age = age -1;
            }else {
            }
        }else {
        }
        if (age<0){
            ToastUtils.showToast(UserInfoEdit.this,"你选择的日期不正确");
            return 0;
        }
        return age;
    }

}
