package com.lvlw.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lvlw.myapp.eventmessage.EventMessage;
import com.lvlw.myapp.fragment.FileDirFragment;
import com.lvlw.myapp.fragment.FileFragment;
import com.lvlw.myapp.fragment.FileScanSetFragment;
import com.lvlw.myapp.fragment.GiftFragment;
import com.lvlw.myapp.fragment.HomeFragment;
import com.lvlw.myapp.fragment.ShareFragment;
import com.lvlw.myapp.fragment.ThemFragment;
import com.lvlw.myapp.permission.PermissionsActivity;
import com.lvlw.myapp.permission.PermissionsChecker;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

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
    //    @BindView(R.id.rbHome)
    //    RadioButton mRbHome;
    //    @BindView(R.id.rbShop)
    //    RadioButton mRbShop;
    //    @BindView(R.id.rbMessage)
    //    RadioButton mRbMessage;
    //    @BindView(R.id.rbMine)
    //    RadioButton mRbMine;
    //    @BindView(R.id.rgTools)
    //    RadioGroup mRgTools;
    @BindView(R.id.rl_home)
    RelativeLayout mRlHome;
    @BindView(R.id.rl_gift)
    RelativeLayout mRlGift;
    @BindView(R.id.rl_share)
    RelativeLayout mRlShare;
    @BindView(R.id.activity_main)
    DrawerLayout mActivityMain;
    //    @BindView(R.id.bottom_menu)
    //    LinearLayout mBottomMenu;
    @BindView(R.id.rl_create_subject)
    RelativeLayout rlCreateSubject;
    //    @BindView(R.id.appBarLayout)
    //    AppBarLayout appBarLayout;
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
//    @BindView(R.id.main_appBarLayout)
//    AppBarLayout mainAppBarLayout;
//    @BindView(R.id.main_toolbar)
//    Toolbar mainToolbar;

    private List<Fragment> mFragments;
    private int mIndex;
    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private boolean firstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mPermissionsChecker = new PermissionsChecker(this);
        setContentView(R.layout.activity_main);
        //        setWindowStatus();
        ButterKnife.bind(this);
//        EventBus.getDefault().register(this);
        //        init();
//        dynamicAddView(mainToolbar,"background",R.color.main_color);
    }


    private void initFragment() {
        HomeFragment homeFragment = new HomeFragment();

        //左滑菜单
        GiftFragment giftFragment = new GiftFragment();
        ShareFragment shareFragment = new ShareFragment();
        ThemFragment themFragment = new ThemFragment();


        mFragments = new ArrayList<>();
        mFragments.add(homeFragment);
        mFragments.add(giftFragment);
        mFragments.add(shareFragment);
        mFragments.add(themFragment);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, homeFragment).commit();
        setIndexSelected(0);
        mRlHome.setSelected(true);
    }

    @OnClick({/*R.id.rbHome, R.id.rbShop, R.id.rbMessage, R.id.rbMine,*/ R.id.rl_home, R.id.rl_gift, R.id.rl_share, R.id.rl_create_subject})
    public void onClick(View view) {
        switch (view.getId()) {
            /*
            case R.id.rbHome:
                setIndexSelected(0);
                mRlHome.setSelected(true);
                break;
            case R.id.rbShop:
                onLeftMenuItemSelected();
                setIndexSelected(1);
                break;
            case R.id.rbMessage:
                onLeftMenuItemSelected();
                setIndexSelected(2);
                break;
            case R.id.rbMine:
                onLeftMenuItemSelected();
                setIndexSelected(3);
                break;
                */
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
                onLeftMenuItemSelected();
                setIndexSelected(1);
                mRlGift.setSelected(true);
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

    //    public void changeBottommenuHeight(LinearLayout linearLayout, boolean isvisible) {
    //        if (isvisible) {
    //            ViewGroup.LayoutParams lp = linearLayout.getLayoutParams();
    //            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
    //        } else {
    //            ViewGroup.LayoutParams lp = linearLayout.getLayoutParams();
    //            lp.height = 0;
    //        }
    //    }

//    private void initToolbar(String menuitem) {
//        mainToolbar.setNavigationIcon(R.mipmap.ic_menu_white);
//        mainToolbar.setTitle(menuitem);
//        mainToolbar.setTitleTextColor(getResources().getColor(R.color.white_normal));
//        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mActivityMain != null) {
//                    mActivityMain.openDrawer(Gravity.START);
//                }
//            }
//        });
//    }

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
        if (KeyEvent.KEYCODE_BACK==keyCode) {
            this.event=event;
            if (mFragments.get(mIndex) instanceof GiftFragment) {
                ((GiftFragment) mFragments.get(mIndex)).onKeyDown(keyCode, event);
                return true;
            }else {
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void setGoIndex(EventMessage eventNessage) {
//        Log.d(TAG, "setGoIndex" + eventNessage.getTag());
//        if (eventNessage != null) {
//            int tag = eventNessage.getTag();

            //            if (tag == EventMessage.EventMessageAction.TAG_GO_MAIN) {
            //                mRbHome.performClick();
            //                mRlHome.setSelected(true);
            //                setIndexSelected(0);
            //            } else if (tag == EventMessage.EventMessageAction.TAG_GO_SHOPCART) {
            //                mRbShop.performClick();
            //                setIndexSelected(1);
            //            } else if (tag == EventMessage.EventMessageAction.TAG_GO_MESSAGE) {
            //                mRbMessage.performClick();
            //                setIndexSelected(2);
            //            }
//        }
//    }


    // 设置状态栏
    private void setWindowStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            //            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 设置状态栏颜色
            getWindow().setBackgroundDrawableResource(R.color.main_color);
        }
    }

}
