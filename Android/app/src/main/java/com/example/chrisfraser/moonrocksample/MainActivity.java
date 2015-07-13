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

import com.example.chrisfraser.moonrocksample.moonrock.notannotations.Portal;
import com.example.chrisfraser.moonrocksample.moonrock.notannotations.ReversePortal;
import com.example.chrisfraser.moonrocksample.moonrock.MoonRockModule;
import com.example.chrisfraser.moonrocksample.moonrock.MoonRock;
import com.example.chrisfraser.moonrocksample.models.PostList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.view.ViewObservable;
import rx.android.widget.WidgetObservable;


public class MainActivity extends AppCompatActivity {
    MoonRock mMoonRock;
    Observable<MoonRockModule> mModuleObservable;

    @Portal Observable<Object> addPressed;
    @Portal Observable<String> add1Text;
    @Portal Observable<String> add2Text;
    @ReversePortal Observable<String> sum;
    @ReversePortal Observable<PostList> posts;

    Subscription mTextSubscription;
    Subscription mPostResponseSubscription;

    @Bind(R.id.addButton) Button mAddButton;
    @Bind(R.id.returnText) TextView mTextView;
    @Bind(R.id.recycler) RecyclerView mRecycler;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.add1) EditText mAdd1;
    @Bind(R.id.add2) EditText mAdd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();

        mMoonRock = new MoonRock(this);
        mModuleObservable = mMoonRock.loadModule("app/appmodule", this);
        createPortals();
        mModuleObservable.subscribe(moonRockModule -> moonRockModule.getPortalGenerator().generatePortals());
    }

    private void setupView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createPortals() {
        addPressed = ViewObservable.clicks(mAddButton).map(ev -> null);
        add1Text = WidgetObservable.text(mAdd1).map(ev -> ev.text().toString());
        add2Text = WidgetObservable.text(mAdd2).map(ev -> ev.text().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mModuleObservable.subscribe(this::setupBehaviour);
    }

    void setupBehaviour(MoonRockModule module) {
        mTextSubscription = AppObservable.bindActivity(this, this.sum).subscribe(mTextView::setText);
        mPostResponseSubscription = AppObservable.bindActivity(this, this.posts).subscribe(data -> {
            mProgressBar.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
            mRecycler.setAdapter(new PostsAdapter(data));
        });
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
