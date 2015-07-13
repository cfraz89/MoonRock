package com.example.chrisfraser.moonrocksample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chrisfraser.moonrocksample.moonrock.Annotations.Portal;
import com.example.chrisfraser.moonrocksample.moonrock.Annotations.ReversePortal;
import com.example.chrisfraser.moonrocksample.moonrock.MoonRockModule;
import com.example.chrisfraser.moonrocksample.moonrock.MoonRock;
import com.example.chrisfraser.moonrocksample.models.Add;
import com.example.chrisfraser.moonrocksample.models.PostList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.view.ViewObservable;
import rx.subjects.PublishSubject;


public class MainActivity extends AppCompatActivity {
    MoonRock mMoonRock;
    Observable<MoonRockModule> mModuleObservable;

    @Portal PublishSubject<Add> addPressed;
    @ReversePortal Observable<Integer> addResponse;
    @ReversePortal Observable<PostList> postsResponse;

    Subscription mTextSubscription;
    Subscription mPostResponseSubscription;

    @Bind(R.id.addButton) Button addButton;
    @Bind(R.id.returnText) TextView mTextView;
    @Bind(R.id.recycler) RecyclerView mRecycler;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.add1) EditText mAdd1;
    @Bind(R.id.add2) EditText mAdd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupView();

        addPressed = PublishSubject.create();

        mMoonRock = new MoonRock(this);
        mModuleObservable = mMoonRock.loadModule("app/appmodule", this);
    }

    private void setupView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mModuleObservable.subscribe(this::setupBehaviour);
    }

    void setupBehaviour(MoonRockModule module) {
        mTextSubscription = AppObservable.bindActivity(this, addResponse).subscribe(data -> mTextView.setText(data.toString()));
        mPostResponseSubscription = AppObservable.bindActivity(this, postsResponse).subscribe(data -> {
            mProgressBar.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
            mRecycler.setAdapter(new PostsAdapter(data));
        });
    }

    @OnClick(R.id.addButton)
    public void button1Clicked() {
        addPressed.onNext(new Add(mAdd1.getText().toString(), mAdd2.getText().toString()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTextSubscription.unsubscribe();
        mPostResponseSubscription.unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMoonRock = null;
    }
}
