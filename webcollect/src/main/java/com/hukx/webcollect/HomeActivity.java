package com.hukx.webcollect;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hukx.webcollect.presenter.WebRecord;
import com.hukx.webcollect.presenter.WebsPresenter;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements ViewShower{

    WebsPresenter mPresenter;

    ContentLoadingProgressBar mLoadingView;
    TextView mEmptyTv;
    RecyclerView mRecyclerView;
    FragmentManager fm;
    FloatingActionButton fab;

    RecyclerViewAdapter mAdapter;
    HandleBackFragment currFrament;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.home);

        fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                HandleBackFragment newFragment = new NewFragment();
                currFrament = newFragment;
                transaction.replace(R.id.content_home, newFragment);
                transaction.addToBackStack("");
                transaction.commit();
                hideFloatingButton();
            }
        });
        mLoadingView = (ContentLoadingProgressBar) findViewById(R.id.loading);
        mEmptyTv = (TextView) findViewById(R.id.empty_tv);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        linearLayoutManager = new WrapContentLinearLayoutManager(this, LinearLayout.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPresenter = new WebsPresenter(this,this);
        mPresenter.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void showLoading(){
        mEmptyTv.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.show();
    }

    @Override
    public void hideLoading() {
        mLoadingView.hide();
    }

    @Override
    public void loadContent(List<WebRecord> result) {
        mAdapter = new RecyclerViewAdapter(result, this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(WebRecord item) {
                showNewFragment(item);
            }
        });
        mAdapter.setOnItemLongClickListener(new RecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, WebRecord item) {
                showDeleteDialog(item);
                return true;
            }
        });

        if(result.size() == 0){
            mRecyclerView.setVisibility(View.GONE);
            mEmptyTv.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTv.setVisibility(View.GONE);
        }

        if(((WbApplication)getApplication()).isFirstTimeLaunch()){
            mPresenter.add("https://www.baidu.com", getResources().getString(R.string.example_note), System.currentTimeMillis());
        }
    }

    @Override
    public void notifyItemRangeChangedFromTo(int dataIndex, int count) {
        mAdapter.notifyItemRangeChanged(dataIndex, count);
    }

    @Override
    public void onInsertItem(int dataIndex) {

        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyTv.setVisibility(View.GONE);

        int dataCount = mAdapter.getItemCount();

        mAdapter.notifyItemInserted(dataIndex);
        linearLayoutManager.scrollToPosition(dataIndex);
        mAdapter.notifyItemRangeChanged(dataIndex, dataCount - dataIndex);
    }

    @Override
    public void onDeleteItem(int dataIndex) {

        int dataCount = mAdapter.getItemCount();

        mAdapter.notifyItemRemoved(dataIndex);
        mAdapter.notifyItemRangeChanged(dataIndex, dataCount - dataIndex + 1);

        if(dataCount == 0){
            mRecyclerView.setVisibility(View.GONE);
            mEmptyTv.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTv.setVisibility(View.GONE);
        }
    }

    public void showFloatingButton() {
        fab.setVisibility(View.VISIBLE);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(fab, "scaleX", 0f, 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(fab, "scaleY", 0f, 1f);
        Interpolator interpolator = new OvershootInterpolator();
        animatorX.setInterpolator(interpolator);
        animatorY.setInterpolator(interpolator);
        animatorX.setDuration(500);
        animatorY.setDuration(500);
        animatorX.start();
        animatorY.start();
        fab.setClickable(true);
    }

    public void hideFloatingButton() {
        fab.setClickable(false);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(fab, "scaleX", 1f, 0f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(fab, "scaleY", 1f, 0f);
        Interpolator interpolator = new AnticipateInterpolator();
        animatorX.setInterpolator(interpolator);
        animatorY.setInterpolator(interpolator);
        animatorX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorX.setDuration(500);
        animatorY.setDuration(500);
        animatorX.start();
        animatorY.start();
    }

    private boolean showDeleteDialog(final WebRecord item){

        final AlertDialog deleteDialog = new AlertDialog.Builder(this, R.style.dialogStyle).create();

        deleteDialog.show();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.option_dialog, null);

        deleteDialog.setContentView(dialogView);

        Window window = deleteDialog.getWindow();
        window.setWindowAnimations(R.style.dialogStyle);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);

        TextView forwardTv = (TextView) dialogView.findViewById(R.id.access);
        forwardTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                intent.putExtra("url", item.getURL());
                HomeActivity.this.startActivity(intent);
            }
        });
        dialogView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                showNewFragment(item);
            }
        });
        dialogView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                mPresenter.delete(item);
            }
        });
        dialogView .findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
        return true;
    }

    private void showNewFragment(WebRecord item) {
        Bundle data = new Bundle();
        data.putSerializable("record", item);
        fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        HandleBackFragment detailFragment = new DetailFragment();
        currFrament = detailFragment;
        detailFragment.setArguments(data);
        transaction.replace(R.id.content_home, detailFragment);
        transaction.addToBackStack("");
        transaction.commit();
        hideFloatingButton();
    }

    @Override
    public void onBackPressed() {
        if(fm.getBackStackEntryCount() == 0){
            currFrament = null;
            super.onBackPressed();
        }
        if(currFrament == null || !currFrament.onBackPressed()){
            super.onBackPressed();
        }
    }
}
