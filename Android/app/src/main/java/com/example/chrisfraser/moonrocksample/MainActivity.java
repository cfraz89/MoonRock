package com.example.chrisfraser.moonrocksample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chrisfraser.moonrocksample.moonrock.Annotations.Portal;
import com.example.chrisfraser.moonrocksample.moonrock.Annotations.ReversePortal;
import com.example.chrisfraser.moonrocksample.moonrock.MRModule;
import com.example.chrisfraser.moonrocksample.moonrock.MoonRock;
import com.example.chrisfraser.moonrocksample.models.Add;
import com.example.chrisfraser.moonrocksample.models.PostList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.subjects.PublishSubject;


public class MainActivity extends AppCompatActivity {

    private final String Module = "app/testviewmodel";

    @Portal("addPressed") PublishSubject<Add> mAddPressed;
    @ReversePortal("addResponse") Observable<Integer> mAddResponse;
    @ReversePortal("postsResponse") Observable<PostList> mPostsResponse;

    @Bind(R.id.returnText) TextView mTextView;
    @Bind(R.id.recycler) RecyclerView mRecycler;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.add1) EditText mAdd1;
    @Bind(R.id.add2) EditText mAdd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MoonRock.createWithModule(this, null, Module, this).subscribe(this::setupBehaviour);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    void setupBehaviour(MRModule module) {
        mAddResponse.subscribe(data->mTextView.setText(data.toString()));

        mPostsResponse.subscribe(data -> {
            mProgressBar.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
            mRecycler.setAdapter(new PostsAdapter(data));
        });
    }

    @OnClick(R.id.addButton)
    public void button1Clicked() {
        mAddPressed.onNext(new Add(mAdd1.getText().toString(), mAdd2.getText().toString()));
    }
}
