package com.example.chrisfraser.rxbridgetest;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.chrisfraser.rxbridgetest.models.Post;
import com.example.chrisfraser.rxbridgetest.models.PostList;

/**
 * Created by chrisfraser on 7/07/15.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder>{
    private PostList mPostList;

    public PostsAdapter(PostList posts) {
        mPostList = posts;
    }

    @Override
    public int getItemCount() {
        return mPostList.getData().size();
    }

    @Override
    public void onBindViewHolder(PostViewHolder postViewHolder, int i) {
        Post post = mPostList.getData().get(i);
        postViewHolder.bind(post.getTitle(), post.getBody());
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return PostViewHolder.Create(viewGroup);
    }
}
