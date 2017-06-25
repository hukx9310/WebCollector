package com.hukx.webcollect;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hukx.webcollect.utils.LogUtil;

/**
 * Created by hkx on 17-6-21.
 */

public class WebViewActivity extends AppCompatActivity {

    WebView mWebView;
    ContentLoadingProgressBar mLoadingView;
    EditText mUrlEt;

    ImageButton mBack;
    ImageButton mForward;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        mWebView = (WebView) findViewById(R.id.wb);
        mLoadingView = (ContentLoadingProgressBar) findViewById(R.id.loading);
        mUrlEt = (EditText)findViewById(R.id.urlEt);
        mBack = (ImageButton)findViewById(R.id.back);
        mForward = (ImageButton)findViewById(R.id.forward);

        mLoadingView.show();


        WebSettings webSettings =   mWebView .getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.toLowerCase().startsWith("http")){
                    view.loadUrl(url);
                    return true;
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(url);
                        intent.setData(uri);
                        startActivity(intent);
                        return false;
                    } catch (ActivityNotFoundException e){
                        return true;
                    }
                }
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mUrlEt.setText(url);
                LogUtil.i("onPageStarted " + url);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mLoadingView.hide();
                mUrlEt.setText(url);
                if(mWebView.canGoBack()){

                    mBack.setVisibility(View.VISIBLE);
                } else {

                    mBack.setVisibility(View.INVISIBLE);
                }

                if(mWebView.canGoForward()){

                    mForward.setVisibility(View.VISIBLE);
                } else {

                    mForward.setVisibility(View.INVISIBLE);
                }
                LogUtil.i("onPageFinished " + url);
            }
        });

        mUrlEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    mWebView.loadUrl(mUrlEt.getText().toString());
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goBack();
            }
        });

        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goForward();
            }
        });


        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }



}
