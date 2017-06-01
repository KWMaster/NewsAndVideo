package com.lvlw.myapp.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoInfo.entity.UserInfo;
import com.lvlw.myapp.R;
import com.lvlw.myapp.activity.UserDetilsActivity;
import com.lvlw.myapp.entity.User;
import com.lvlw.myapp.eventmessage.LoginSuccess;
import com.lvlw.myapp.greendao.CommonUtils;
import com.lvlw.myapp.shareprefrence.ListDataSave;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import solid.ren.skinlibrary.base.SkinBaseFragment;

/**
 * Created by Wantrer on 2017/5/9 0009.
 */

public class UserDetilsOne extends SkinBaseFragment {
    @BindView(R.id.user_exit)
    TextView userExit;
    @BindView(R.id.user_destroy)
    TextView userDestroy;
    @BindView(R.id.user_mod_pwd)
    TextView userModPwd;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CommonUtils dbUtils;
    private ListDataSave listDataSave;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.userdetils_one, null);
        ButterKnife.bind(this, rootView);
        toolbar= (Toolbar) getActivity().findViewById(R.id.toolbar);
        collapsingToolbarLayout= (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        return rootView;
    }

    @OnClick({R.id.user_exit, R.id.user_destroy, R.id.user_mod_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_exit:
                LoginSuccess loginSuccess=new LoginSuccess("登陆");
                getActivity().onBackPressed();
                EventBus.getDefault().post(loginSuccess);
                break;
            case R.id.user_destroy:
                deletedUserInfo(collapsingToolbarLayout.getTitle().toString());
                break;
            case R.id.user_mod_pwd:
                ((UserDetilsActivity)getActivity()).setIndexSelected(1);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((UserDetilsActivity)getActivity()).setIndexSelected(0);
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().onBackPressed();
                            }
                        });
                    }
                });
                break;
        }
    }
    private void deletedUserInfo(final String username){
        dbUtils=new CommonUtils(getActivity(),"videoinfo.db");
        listDataSave=new ListDataSave(getActivity());
        new AsyncTask<String,String,UserInfo>(){
            private ProgressDialog dialog;
            private boolean isexist=false;
            private boolean isaccess=false;
            @Override
            protected void onPreExecute() {
                dialog=ProgressDialog.show(getActivity(),"","正在删除，请稍后......");
                super.onPreExecute();
            }

            @Override
            protected UserInfo doInBackground(String... params) {
                if (dbUtils.queryBuilderUserInfo(username)){
                    isexist=true;
                }
                if (isexist){
                    UserInfo user=dbUtils.queryBuilderOneUserInfo(username);
                    if (dbUtils.deleteUserInfo(user)){
                        isaccess=true;
                        List<User> autoList=listDataSave.getDataList("autologin");
                        List<User> userList=listDataSave.getDataList("users");
                        boolean exists=false;
                        for (User user1 : autoList) {
                            if (user1.getUser_Name().equals(username)){
                                exists=true;
                                listDataSave.clear(getActivity(),"autologin");
                                break;
                            }
                        }
                        boolean exist=false;
                        for (User user1 : userList) {
                            if (user1.getUser_Name().equals(username)){
                                exist=true;
                                userList.remove(user1);
                                break;
                            }
                        }
                        if (exist){
                            if (exists){
                                if (userList.size()>0){
                                    listDataSave.setDataList("users",userList,"autologin",autoList);
                                    listDataSave.clear(getActivity(),"autologin");
                                }else {
                                    listDataSave.clear(getActivity(),"users");
                                    listDataSave.clear(getActivity(),"autologin");
                                }
                            }else {
                                if (userList.size()>0){
                                    listDataSave.setDataList("users",userList,"autologin",autoList);
                                }else {
                                    listDataSave.setDataList("users",null,"autologin",autoList);
                                    listDataSave.clear(getActivity(),"users");
                                }
                            }
                        }
                    }
                }
                dbUtils.getDaoManager().closeDaoSession();
                return null;
            }

            @Override
            protected void onPostExecute(UserInfo userInfo) {
                dialog.dismiss();
                if (!isexist){
                    Toast.makeText(getActivity(),"用户名不存在！",Toast.LENGTH_SHORT).show();
                }else {
                    if (!isaccess){
                        Toast.makeText(getActivity(),"注销用户失败！",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(),"注销用户成功！",Toast.LENGTH_SHORT).show();
                        LoginSuccess loginSuccess=new LoginSuccess("登陆");
                        EventBus.getDefault().post(loginSuccess);
                        getActivity().onBackPressed();
                    }
                }
                super.onPostExecute(userInfo);
            }
        }.execute();
    }
}
