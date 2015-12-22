package com.agitation.sportman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.agitation.sportman.R;

import java.util.List;
import java.util.Map;

/**
 * Created by fanwl on 2015/12/12.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> commentList;
    private boolean isComment;

    public CommentAdapter(List<Map<String, Object>> commentList, Context context){
        this.commentList=commentList;
        this.context=context;
    }
    public void  setCommentList(List<Map<String, Object>> commentList, boolean isComment){
        this.commentList=commentList;
        this.isComment = isComment;
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

        return view;
    }
}
