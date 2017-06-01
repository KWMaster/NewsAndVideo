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
import android.widget.EditText;
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

public class UserDetilsTwo extends SkinBaseFragment {
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.old_pwd)
    EditText oldPwd;
    @BindView(R.id.first_pwd)
    EditText firstPwd;
    @BindView(R.id.second_pwd)
    EditText secondPwd;
    @BindView(R.id.user_re_pwd)
    TextView userRePwd;
    @BindView(R.id.user_clear_info)
    TextView userClearInfo;
    @BindView(R.id.user_detils_back)
    TextView userDetilsBack;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CommonUtils dbUtils;
    private ListDataSave listDataSave;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.userdetils_two, null);
        ButterKnife.bind(this, rootView);
        toolbar= (Toolbar) getActivity().findViewById(R.id.toolbar);
        collapsingToolbarLayout= (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        userName.setText(collapsingToolbarLayout.getTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllEdt();
                ((UserDetilsActivity)getActivity()).setIndexSelected(0);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
            }
        });
        return rootView;
    }
    public void clearAllEdt(){
        oldPwd.setText(null);
        firstPwd.setText(null);
        secondPwd.setText(null);
    }
    @OnClick({R.id.user_re_pwd, R.id.user_clear_info, R.id.user_detils_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_re_pwd:
                checkModify(collapsingToolbarLayout.getTitle().toString());
                break;
            case R.id.user_clear_info:
                clearAllEdt();
                break;
            case R.id.user_detils_back:
                clearAllEdt();
                ((UserDetilsActivity)getActivity()).setIndexSelected(0);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                break;
        }
    }
    private void checkModify(final String username){
        final String oldPassWord=oldPwd.getText().toString();
        final String firstPassWord=firstPwd.getText().toString();
        dbUtils=new CommonUtils(getActivity(),"videoinfo.db");
        if (oldPassWord.isEmpty()||firstPassWord.isEmpty()||secondPwd.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"数据不能为空！",Toast.LENGTH_SHORT).show();
        }else if (!firstPassWord.equals(secondPwd.getText().toString())){
            Toast.makeText(getActivity(),"两次密码输入不相同！",Toast.LENGTH_SHORT).show();
        }else if (userName.getText().toString().equals("登陆")){
            Toast.makeText(getActivity(),"此用户名不可用！",Toast.LENGTH_SHORT).show();
        } else{
            new AsyncTask<String,String,UserInfo>(){
                private ProgressDialog dialog;
                private boolean isexist=false;
                private boolean is_the_same=false;
                private boolean isaccess=false;
                @Override
                protected void onPreExecute() {
                    listDataSave=new ListDataSave(getActivity());
                    dialog=ProgressDialog.show(getActivity(),"","正在验证，请稍后......");
                    super.onPreExecute();
                }

                @Override
                protected UserInfo doInBackground(String... params) {
                    if (dbUtils.queryBuilderUserInfo(username)){
                        isexist=true;
                    }
                    if (isexist){
                        UserInfo user=dbUtils.queryBuilderOneUserInfo(username);
                        if (oldPassWord.equals(user.getUser_PassWord())){
                            is_the_same=true;
                        }
                        if (is_the_same){
                            UserInfo userInfo=new UserInfo();
                            userInfo.setId(user.getId());
                            userInfo.setUser_Name(username);
                            userInfo.setUser_PassWord(firstPassWord);
                            if (dbUtils.updateUserInfo(userInfo)){
                                isaccess=true;

                                List<User> autoList=listDataSave.getDataList("autologin");
                                List<User> userList=listDataSave.getDataList("users");
                                boolean exists=false;
                                if (autoList!=null&&autoList.size()>0) {
                                    for (User user1 : autoList) {
                                        if (user1.getUser_Name().equals(username)) {
                                            exists = true;
                                            autoList.get(0).setUser_PassWord(firstPassWord);
                                            break;
                                        }
                                    }
                                }
                                boolean exist=false;
                                if (userList!=null&&userList.size()>0) {
                                    for (User user1 : userList) {
                                        if (user1.getUser_Name().equals(username)) {
                                            exist = true;
                                            userList.remove(user1);
                                            user1.setUser_PassWord(firstPassWord);
                                            user1.getUser_Name();
                                            userList.add(user1);
                                            break;
                                        }
                                    }
                                }
                                if (exist){
                                    if (exists){
                                        if (userList.size()>0){
                                            listDataSave.setDataList("users",userList,"autologin",autoList);
                                        }else {
                                            listDataSave.clear(getActivity(),"users");
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
                        if (is_the_same) {
                            if (!isaccess) {
                                Toast.makeText(getActivity(), "密码修改失败！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "密码修改成功！", Toast.LENGTH_SHORT).show();
                                ((UserDetilsActivity)getActivity()).setIndexSelected(0);
                                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getActivity().onBackPressed();
                                    }
                                });
                                clearAllEdt();
                            }
                        }else {
                            Toast.makeText(getActivity(), "旧密码不正确！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    super.onPostExecute(userInfo);
                }
            }.execute();
        }

    }
}
