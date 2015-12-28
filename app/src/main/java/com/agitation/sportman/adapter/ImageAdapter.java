package com.agitation.sportman.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.agitation.sportman.R;
import com.agitation.sportman.activity.WebActivity;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MyViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;
import java.util.Map;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private List<Map<String, Object>> images;
	protected ImageLoader imageLoader = null;
//	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private DisplayImageOptions options = null;
	private String imageProfix;

	public ImageAdapter(Context context, List<Map<String, Object>> images) {
		this.mContext = context;
		this.images=images;
		this.imageProfix = DataHolder.getInstance().getImageProfix();
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.pictures_no) //设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.pictures_no) //设置图片Uri为空或是错误的时候显示的图片  
		.showImageOnFail(R.drawable.pictures_no) //设置图片加载/解码过程中错误时候显示的图片
		.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
		.cacheOnDisk(true) //设置下载的图片是否缓存在SD卡中
		.bitmapConfig(Bitmap.Config.ARGB_8888)
		.displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
		.build();
	}
	
	public void setData(List<Map<String, Object>> images){
		this.images=images;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.home_image_item, null);
		}
		ImageView home_image = MyViewHolder.get(view, R.id.home_image_item);
		if(images.size()==0)return view;
		final Map<String, Object> itemData = images.get(position % images.size());
		String img_url = imageProfix + itemData.get("url");
		imageLoader.displayImage(img_url, home_image, options , null);
//		imageLoader.displayImage(itemData.get("ImgSrc")+"", home_image);
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = itemData.get("website")+"";
				if(TextUtils.isEmpty(url) || "null".equals(url))return;
				Intent intent = new Intent(mContext, WebActivity.class);
				intent.putExtra(WebActivity.URL_NAME, url);
				mContext.startActivity(intent);
			}
		});
		
		
		return view;
	}
	
	
//	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
//
//		static final List<String> displayedImages = Collections
//				.synchronizedList(new LinkedList<String>());
//
//		@Override
//		public void onLoadingComplete(String imageUri, View view,
//				Bitmap loadedImage) {
//			if (loadedImage != null) {
//				ImageView imageView = (ImageView) view;
//				boolean firstDisplay = !displayedImages.contains(imageUri);
//				if (firstDisplay) {
//					FadeInBitmapDisplayer.animate(imageView, 500);
//					displayedImages.add(imageUri);
//				}
//			}
//		}
//	}

}
