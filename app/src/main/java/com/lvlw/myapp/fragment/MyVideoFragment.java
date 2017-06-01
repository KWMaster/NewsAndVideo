package com.lvlw.myapp.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lvlw.myapp.MainActivity;
import com.lvlw.myapp.R;
import com.lvlw.myapp.adapter.MyAdapter;
import com.lvlw.myapp.entity.QFileInfo;
import com.lvlw.myapp.eventmessage.GoToFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import solid.ren.skinlibrary.base.SkinBaseFragment;


/**
 * 我的视频界面
 *
 * @author ansen
 * @create time 2016-04-19
 */
public class MyVideoFragment extends SkinBaseFragment {

    @BindView(R.id.seltall)
    TextView seltall;
    @BindView(R.id.reseltall)
    TextView reseltall;
    @BindView(R.id.edit)
    TextView edit;
    @BindView(R.id.video_back)
    ImageView videoBack;
    @BindView(R.id.myvideo)
    TextView myvideo;

    public Toolbar getToolbar() {
        return toolbar;
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.myvideo_content)
    FrameLayout myvideoContent;

    private DrawerLayout mActivityMain;

    private FileDirFragment filedirfragment;
    private FileFragment filefragment;
    private FileScanSetFragment fileScanSetFragment;
    private List<Fragment> mFragments;
    private int mIndex;
    private MyAdapter mAdapter;
    private List<QFileInfo> mDatas;
    private boolean selectedall = false;
    private int groupId_1 = 1;
    private GoToFragment gotofrg = new GoToFragment(1);
    //    private boolean optionMenuOn = true;  //标示是否要显示optionmenu
    //    private Menu aMenu;         //获取optionmenu

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gift, null);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        initView();
        return rootView;
    }

    private void initView() {
        mActivityMain = (DrawerLayout) getActivity().findViewById(R.id.activity_main);
        DrawerlayoutOut();
        filedirfragment = new FileDirFragment();
        filefragment = new FileFragment();
        mFragments = new ArrayList<>();
        mFragments.add(filedirfragment);
        mFragments.add(filefragment);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.myvideo_content, filedirfragment).commit();
        setIndexSelected(1);
    }

    private void setIndexSelected(int index) {
        if (mIndex == index) {
            return;
        }
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //        ft.replace(R.id.content,mFragments[index]);
        ft.hide(mFragments.get(mIndex));

        if (!mFragments.get(index).isAdded()) {
            ft.add(R.id.myvideo_content, mFragments.get(index));
        } else {
            ft.show(mFragments.get(index));
        }
        ft.commit();
        mIndex = index;
    }

    private void DrawerlayoutOut() {
        videoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityMain.openDrawer(Gravity.START);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.video_menu, menu);
        //添加第一组菜单
        //        aMenu = menu;
    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if (mFragments.get(mIndex) instanceof FileDirFragment) {
            if (edit.getText().equals("取消")) {
                edit.performClick();
            }
            filedirfragment.getFileparent().setText(Environment.getExternalStorageDirectory().getPath());
            EventBus.getDefault().post(gotofrg);
        } else if (mFragments.get(mIndex) instanceof FileFragment) {
            if (edit.getText().equals("取消")) {
                edit.performClick();
            }
            getActivity().finish();
        } else if (mFragments.get(mIndex) instanceof FileScanSetFragment) {
            EventBus.getDefault().post(gotofrg);
        }
        mActivityMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.video_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.custom_scan:
                goToVideoBack("自定义扫描");
                if (mFragments.get(mIndex) instanceof FileFragment) {
                    ((FileFragment) mFragments.get(mIndex)).getIbtnRightMenu2().setVisibility(View.GONE);
                    mAdapter = (MyAdapter) filefragment.getFile().getAdapter();
                    mDatas = mAdapter.getmDatas();
                    for (int i = 0; i < mDatas.size(); i++) {
                        mDatas.get(i).setCheck(false);
                    }
                    if (!mAdapter.flage) {
                        mAdapter.flage = !mAdapter.flage;
                    }
                    seltall.setVisibility(View.GONE);
                    reseltall.setVisibility(View.GONE);
                    edit.setText("编辑");
                }

                filedirfragment.initData();
                filedirfragment.getFileparent().setText(Environment.getExternalStorageDirectory().getPath());
                setIndexSelected(0);
                //                mMenu.findItem(0).setVisible(false);
                //                mMenu.findItem(1).setVisible(false);
                seltall.setVisibility(View.VISIBLE);
                reseltall.setVisibility(View.VISIBLE);
                break;
            case R.id.all_scan:
                if (mFragments.get(mIndex) instanceof FileFragment) {
                    mAdapter = (MyAdapter) filefragment.getFile().getAdapter();
                    filefragment.scanSDCard();
                    if (mAdapter.flage) {
                        mAdapter.flage = !mAdapter.flage;
                    }
                }
                break;
            case R.id.set_scan:
                goToVideoBack("扫描设置");
                if (fileScanSetFragment == null) {
                    fileScanSetFragment = new FileScanSetFragment();
                    mFragments.add(fileScanSetFragment);
                }
                setIndexSelected(2);
                break;
        }
        return true;
    }

    private void goToVideoBack(String title) {
        toolbar.getMenu().clear();
        myvideo.setText(title);
        videoBack.setImageResource(R.mipmap.ic_menu_back);
        videoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                getActivity().onBackPressed();
                getActivity().onKeyDown(KeyEvent.KEYCODE_BACK, ((MainActivity) getActivity()).getEvent());
            }
        });
        edit.setVisibility(View.GONE);
        mActivityMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void backToMyVideo() {
        toolbar.inflateMenu(R.menu.video_menu);
        DrawerlayoutOut();
        mActivityMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        myvideo.setText("我的视频");
    }

    @Subscribe
    public void onEventMainThread(GoToFragment goToFragment) {
        if (goToFragment.getIndex() == 1) {
            backToMyVideo();
            videoBack.setImageResource(R.mipmap.ic_menu_white);
            mAdapter = (MyAdapter) filefragment.getFile().getAdapter();
            if (mAdapter.flage) {
                mAdapter.flage = !mAdapter.flage;
            }
            setIndexSelected(goToFragment.getIndex());
            edit.setVisibility(View.VISIBLE);
            seltall.setVisibility(View.GONE);
            reseltall.setVisibility(View.GONE);
        } else if (goToFragment.getIndex() == 2) {
            videoBack.setVisibility(View.VISIBLE);
            backToMyVideo();
        }
    }

    @OnClick({R.id.seltall, R.id.reseltall, R.id.edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.seltall:
                if (mFragments.get(mIndex) == filedirfragment) {
                    mAdapter = (MyAdapter) filedirfragment.getFiledir().getAdapter();
                    //                    setEditVisiable(filedirfragment);
                    //                    if (mAdapter.flage) {
                    selectedall = !selectedall;
                    mDatas = ((MyAdapter) filedirfragment.getFiledir().getAdapter()).getmDatas();
                    if (selectedall) {
                        for (int i = 0; i < mDatas.size(); i++) {
                            mDatas.get(i).setCheck(true);
                        }
                    } else {
                        for (int i = 0; i < mDatas.size(); i++) {
                            mDatas.get(i).setCheck(false);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    //                    }
                } else if (mFragments.get(mIndex) == filefragment) {
                    mAdapter = (MyAdapter) filefragment.getFile().getAdapter();
                    //                    setEditVisiable(filefragment);
                    if (mAdapter.flage) {
                        selectedall = !selectedall;
                        mDatas = ((MyAdapter) filefragment.getFile().getAdapter()).getmDatas();
                        if (selectedall) {
                            for (int i = 0; i < mDatas.size(); i++) {
                                mDatas.get(i).setCheck(true);
                            }
                        } else {
                            for (int i = 0; i < mDatas.size(); i++) {
                                mDatas.get(i).setCheck(false);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.reseltall:
                if (mFragments.get(mIndex) == filedirfragment) {
                    mAdapter = (MyAdapter) filedirfragment.getFiledir().getAdapter();
                    //                    setEditVisiable(filedirfragment);
                    //                    if (mAdapter.flage) {
                    mDatas = ((MyAdapter) filedirfragment.getFiledir().getAdapter()).getmDatas();
                    for (int i = 0; i < mDatas.size(); i++) {
                        if (mDatas.get(i).isCheck()) {
                            mDatas.get(i).setCheck(false);
                        } else {
                            mDatas.get(i).setCheck(true);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                    //                    }
                } else if (mFragments.get(mIndex) == filefragment) {
                    mAdapter = (MyAdapter) filefragment.getFile().getAdapter();
                    //                    setEditVisiable(filefragment);
                    if (mAdapter.flage) {
                        mDatas = ((MyAdapter) filefragment.getFile().getAdapter()).getmDatas();
                        for (int i = 0; i < mDatas.size(); i++) {
                            if (mDatas.get(i).isCheck()) {
                                mDatas.get(i).setCheck(false);
                            } else {
                                mDatas.get(i).setCheck(true);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.edit:
                if (mFragments.get(mIndex) == filefragment) {
                    mAdapter = (MyAdapter) filefragment.getFile().getAdapter();
                    if (mAdapter.getCount() > 0) {
                        mAdapter.flage = !mAdapter.flage;

                        if (mAdapter.flage) {
                            mActivityMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            mDatas = ((MyAdapter) filefragment.getFile().getAdapter()).getmDatas();
                            for (int i = 0; i < mDatas.size(); i++) {
                                mDatas.get(i).setCheck(false);
                            }
                            videoBack.setVisibility(View.GONE);
                            seltall.setVisibility(View.VISIBLE);
                            reseltall.setVisibility(View.VISIBLE);
                            filefragment.getIbtnRightMenu2().setVisibility(View.VISIBLE);
                            mAdapter.isAdd = true;
                            edit.setText("取消");
                            toolbar.getMenu().clear();
                        } else {
                            mActivityMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                            videoBack.setVisibility(View.VISIBLE);
                            seltall.setVisibility(View.GONE);
                            reseltall.setVisibility(View.GONE);
                            filefragment.getIbtnRightMenu2().setVisibility(View.GONE);
                            mAdapter.isAdd = false;
                            toolbar.inflateMenu(R.menu.video_menu);
                            edit.setText("编辑");
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
