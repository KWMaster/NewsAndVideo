package com.lvlw.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lvlw.myapp.activity.LoginActivity;
import com.lvlw.myapp.activity.UserDetilsActivity;
import com.lvlw.myapp.eventmessage.LoginSuccess;
import com.lvlw.myapp.fragment.AboutFragment;
import com.lvlw.myapp.fragment.MyVideoFragment;
import com.lvlw.myapp.fragment.HomeFragment;
import com.lvlw.myapp.fragment.ThemFragment;
import com.lvlw.myapp.permission.PermissionsActivity;
import com.lvlw.myapp.permission.PermissionsChecker;
import com.lvlw.myapp.shareprefrence.ListDataSave;
import com.lvlw.myapp.views.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import solid.ren.skinlibrary.base.SkinBaseActivity;


public class MainActivity extends SkinBaseActivity {

    private static final String TAG = "wiwi";
    @BindView(R.id.content)
    FrameLayout mContent;
    @BindView(R.id.rl_home)
    RelativeLayout mRlHome;
    @BindView(R.id.rl_gift)
    RelativeLayout mRlGift;
    @BindView(R.id.rl_share)
    RelativeLayout mRlShare;
    @BindView(R.id.activity_main)
    DrawerLayout mActivityMain;
    @BindView(R.id.rl_create_subject)
    RelativeLayout rlCreateSubject;
    @BindView(R.id.header)
    LinearLayout header;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.iv_gift)
    ImageView ivGift;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.left_drawer)
    FrameLayout leftDrawer;
    @BindView(R.id.user_header)
    CircleImageView userHeader;
    @BindView(R.id.user_name)
    TextView userName;
    private List<Fragment> mFragments;
    private int mIndex;
    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private boolean firstIn = true;
    private boolean loginflag = false;

    private ListDataSave listDataSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mPermissionsChecker = new PermissionsChecker(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        listDataSave=new ListDataSave(this);
        EventBus.getDefault().register(this);
        initLoginListener();
    }

    private void initLoginListener() {
        if (listDataSave.getDataList("autologin")!=null&&listDataSave.getDataList("autologin").size()>0){
            loginflag=true;
            userHeader.setImageResource(R.mipmap.header);
            userName.setText(listDataSave.getDataList("autologin").get(0).getUser_Name());
        }
        if (loginflag){
            userHeader.setOnClickListener(new UserDetilsOnClickListener());
            userName.setOnClickListener(new UserDetilsOnClickListener());
        }else {
            userHeader.setOnClickListener(new LoginOnClickListener());
            userName.setOnClickListener(new LoginOnClickListener());
        }
    }

    private class UserDetilsOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mFragments.get(mIndex) instanceof MyVideoFragment){
                mRlHome.performClick();
            }
            Bundle bundle=new Bundle();
            bundle.putString("username",userName.getText().toString());
            Intent intent=new Intent(MainActivity.this, UserDetilsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class LoginOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void initFragment() {
        HomeFragment homeFragment = new HomeFragment();

        //左滑菜单
        MyVideoFragment myVideoFragment = new MyVideoFragment();
        AboutFragment aboutFragment = new AboutFragment();
        ThemFragment themFragment = new ThemFragment();


        mFragments = new ArrayList<>();
        mFragments.add(homeFragment);
        mFragments.add(myVideoFragment);
        mFragments.add(aboutFragment);
        mFragments.add(themFragment);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, homeFragment).commit();
        setIndexSelected(0);
        mRlHome.setSelected(true);
    }

    @OnClick({R.id.rl_home, R.id.rl_gift, R.id.rl_share, R.id.rl_create_subject})
    public void onClick(View view) {
        switch (view.getId()) {
            //左滑菜单
            case R.id.rl_home:
                //                mainAppBarLayout.setVisibility(View.GONE);
                onLeftMenuItemSelected();
                //                mRbHome.performClick();
                setIndexSelected(0);
                mRlHome.setSelected(true);
                //                mBottomMenu.setVisibility(View.VISIBLE);
                mActivityMain.closeDrawer(Gravity.LEFT);
                break;
            case R.id.rl_gift:
                //                initToolbar("礼物");
                //                mainAppBarLayout.setVisibility(View.VISIBLE);
                if (loginflag) {
                    onLeftMenuItemSelected();
                    setIndexSelected(1);
                    mRlGift.setSelected(true);
                }else {
                    Toast.makeText(this,"此功能仅限登陆后使用，请先登录！",Toast.LENGTH_SHORT).show();
                }
                //                mBottomMenu.setVisibility(View.GONE);
                mActivityMain.closeDrawer(Gravity.LEFT);
                break;
            case R.id.rl_share:
                //                initToolbar("分享");
                //                mainAppBarLayout.setVisibility(View.VISIBLE);
                onLeftMenuItemSelected();
                setIndexSelected(2);
                mRlShare.setSelected(true);
                //                mBottomMenu.setVisibility(View.GONE);
                mActivityMain.closeDrawer(Gravity.LEFT);
                break;
            case R.id.rl_create_subject:
                //                initToolbar("更换皮肤");
                //                mainAppBarLayout.setVisibility(View.VISIBLE);
                onLeftMenuItemSelected();
                setIndexSelected(3);
                rlCreateSubject.setSelected(true);
                //                mBottomMenu.setVisibility(View.GONE);
                mActivityMain.closeDrawer(Gravity.LEFT);
                break;
        }
    }


    public void onLeftMenuItemSelected() {
        mRlHome.setSelected(false);
        mRlGift.setSelected(false);
        mRlShare.setSelected(false);
        rlCreateSubject.setSelected(false);
    }

    public void setIndexSelected(int index) {
        if (mIndex == index) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.hide(mFragments.get(mIndex));

        if (!mFragments.get(index).isAdded()) {
            ft.add(R.id.content, mFragments.get(index));
        } else {
            ft.show(mFragments.get(index));
        }
        ft.commit();

        mIndex = index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (firstIn) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                    startPermissionsActivity();
                } else {
                    initFragment();
                    firstIn = false;
                }
            } else {
                initFragment();
                firstIn = false;
            }
        }

    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    public KeyEvent event;

    public KeyEvent getEvent() {
        return event;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            this.event = event;
            if (mFragments.get(mIndex) instanceof MyVideoFragment) {
                ((MyVideoFragment) mFragments.get(mIndex)).onKeyDown(keyCode, event);
                return true;
            } else {
                finish();
            }
        }
        return false;
    }

    @Subscribe
    public void onEventMainThread(LoginSuccess loginSuccess) {
        userName.setText(loginSuccess.getUsername());
        if (!userName.getText().toString().equals("登陆")){
            loginflag=true;
            userHeader.setImageResource(R.mipmap.header);
            userHeader.setOnClickListener(new UserDetilsOnClickListener());
            userName.setOnClickListener(new UserDetilsOnClickListener());
        }else {
            loginflag=false;
            userHeader.setImageResource(R.mipmap.unloginheader);
            userHeader.setOnClickListener(new LoginOnClickListener());
            userName.setOnClickListener(new LoginOnClickListener());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



}
