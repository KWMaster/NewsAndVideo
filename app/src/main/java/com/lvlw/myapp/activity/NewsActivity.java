package com.lvlw.myapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.lvlw.myapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import solid.ren.skinlibrary.base.SkinBaseActivity;

/**
 * Created by Wantrer on 2017/3/26 0026.
 */

public class NewsActivity extends SkinBaseActivity {

    @BindView(R.id.news_item)
    LinearLayout newsItem;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Toolbar toolbar;
    private ScrollView scrollView;
    private WebView newsWebview;
    private WebSettings webSettings;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar = new Toolbar(this);
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_color));
        toolbar.setNavigationIcon(R.mipmap.ic_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        onBackPressed();
            }
        });

        toolbar.setTitleTextColor(getResources().getColor(R.color.white_normal));
        dynamicAddView(toolbar,"background",R.color.main_color);
        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(params);
        newsWebview = new WebView(getApplicationContext());
        newsWebview.setLayoutParams(params);
        scrollView.addView(newsWebview);
        newsItem.addView(toolbar);
        newsItem.addView(scrollView);
        newsWebview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        webSettings = newsWebview.getSettings();
        webSettings.setSupportZoom(true);  // 支持缩放
        webSettings.setBuiltInZoomControls(true); // 支持缩放
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.setDisplayZoomControls(false); //设置是否显示缩放按钮
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        final Intent data = getIntent();
        Bundle bundle=data.getExtras();
        ArrayList<String> str=new ArrayList<>();
        str=bundle.getStringArrayList("link");
        toolbar.setTitle(str.get(0));
        newsWebview.loadUrl(str.get(1));

        newsWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        newsWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                System.out.println("标题在这里");
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //                progressDialog.setMessage("加载" + newProgress);
                if (scrollView!=null&&toolbar!=null){
                    scrollView.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        newsWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (scrollView!=null&&toolbar!=null){
                    scrollView.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //                progressDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                if (scrollView!=null&&toolbar!=null) {
                    toolbar.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                }

            }
        });
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (newsWebview != null) {
            newsWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            newsWebview.clearHistory();
            newsWebview.freeMemory();

            ((ViewGroup) newsWebview.getParent()).removeView(newsWebview);
            ((ViewGroup) toolbar.getParent()).removeView(toolbar);
            toolbar.destroyDrawingCache();
            scrollView.destroyDrawingCache();
            newsWebview = null;
            toolbar = null;
            scrollView = null;
        }
        super.onDestroy();
    }

}
