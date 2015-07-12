package com.example.chrisfraser.moonrocksample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by chrisfraser on 7/07/15.
 */
public class PostViewHolder extends RecyclerView.ViewHolder {
    private TextView mTextView1;
    private TextView mTextView2;

    public static PostViewHolder Create(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post, viewGroup, false);
        return new PostViewHolder(view);
    }

    public PostViewHolder(View itemView) {
        super(itemView);
        mTextView1 = (TextView) itemView.findViewById(R.id.textView);
        mTextView2 = (TextView) itemView.findViewById(R.id.textView2);
    }

    public void bind(String title, String body) {
        mTextView1.setText(title);
        mTextView2.setText(body);
    }
}
