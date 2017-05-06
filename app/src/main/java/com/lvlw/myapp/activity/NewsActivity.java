package com.lvlw.myapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private ScrollView scrollView;
    private WebView newsWebview;
    private WebSettings webSettings;
    private ProgressDialog progressDialog;

    private RelativeLayout relativeLayout;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        relativeLayout=new RelativeLayout(this);
//        relativeLayout.setLayoutParams(params);
//        relativeLayout.setGravity(RelativeLayout.CENTER_HORIZONTAL);
//        textView=new TextView(this);
//        textView.setText("コンパイラ原理");
//        textView.setTextColor(getResources().getColor(R.color.white_normal));
//        relativeLayout.addView(textView);

        toolbar = new Toolbar(this);
//        toolbar.addView(relativeLayout);
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_color));
//        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setNavigationIcon(R.mipmap.ic_menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        onBackPressed();
//                    }
//                });
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
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        //        progressDialog = new ProgressDialog(this);
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
                //                if (progressDialog != null) {
                //                    progressDialog.setMessage("加载中。。。。。。");
                //                }
                //                progressDialog.show();
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

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_BACK){
//            this.finish();
//            Intent intent = new Intent(this, MainActivity.class);
////            intent.setClass(this, MainActivity.class);
////            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
////            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
//            startActivity(intent);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


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
//            ((ViewGroup) textView.getParent()).removeView(textView);
//            ((ViewGroup) relativeLayout.getParent()).removeView(relativeLayout);
            ((ViewGroup) toolbar.getParent()).removeView(toolbar);
            ((ViewGroup) scrollView.getParent()).removeView(scrollView);
//            newsWebview.destroy();
            toolbar.destroyDrawingCache();
            scrollView.destroyDrawingCache();
            newsWebview = null;
//            textView=null;
//            relativeLayout=null;
            toolbar = null;
            scrollView = null;
        }
        super.onDestroy();
    }

}
