package com.example.chrisfraser.moonrocksample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chrisfraser on 7/07/15.
 */
public class PostViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.textView) private TextView mTextView1;
    @Bind(R.id.textView2) private TextView mTextView2;

    public static PostViewHolder Create(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post, viewGroup, false);
        return new PostViewHolder(view);
    }

    public PostViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String title, String body) {
        mTextView1.setText(title);
        mTextView2.setText(body);
    }
}
