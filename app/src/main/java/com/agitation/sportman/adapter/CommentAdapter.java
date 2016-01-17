package com.agitation.sportman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.agitation.sportman.R;
import com.agitation.sportman.utils.MyViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/12/12.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> commentList;
    private boolean isComment;

    public CommentAdapter(List<Map<String, Object>> commentList, Context context, boolean isComment){
        this.commentList=commentList;
        this.context=context;
        this.isComment=isComment;
    }
    public void  setCommentList(List<Map<String, Object>> commentList){
        this.commentList=commentList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (isComment){
                int commentCount = commentList.size();
                return commentCount>5?5:commentCount;
        }else {
            return commentList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null){
            view = LayoutInflater.from(context).inflate(R.layout.comment_item,null);
        }
        Map<String, Object> item = commentList.get(position);
        TextView comment_name = MyViewHolder.get(view, R.id.comment_name);
        comment_name.setText(item.get("name")+"");
        TextView comment_time = MyViewHolder.get(view, R.id.comment_time);
        comment_time.setText(item.get("createDate")+"");
        TextView comment_content = MyViewHolder.get(view, R.id.comment_content);
        comment_content.setText("     "+item.get("content"));
        TextView comment_num = MyViewHolder.get(view, R.id.comment_num);
        comment_num.setText(item.get("score")+"分");
        RatingBar ratingbar = MyViewHolder.get(view, R.id.ratingbar);
        Double score = Double.parseDouble(item.get("score")+"");
        ratingbar.setRating((float)(score/2));
        return view;
    }
}
