package com.lvlw.myapp.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoInfo.entity.UserInfo;
import com.lvlw.myapp.R;
import com.lvlw.myapp.activity.LoginActivity;
import com.lvlw.myapp.greendao.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Wantrer on 2017/5/8 0008.
 */

public class RegisterFragment extends Fragment {
    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.first_pwd)
    EditText firstPwd;
    @BindView(R.id.second_pwd)
    EditText secondPwd;
    @BindView(R.id.user_register)
    TextView userRegister;
    @BindView(R.id.clear_info)
    TextView clearInfo;
    private ImageView loginBack;
    private TextView signUp;;
    private CommonUtils dbUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register, null);
        ButterKnife.bind(this, rootView);
        loginBack= (ImageView) getActivity().findViewById(R.id.login_back);
        signUp= (TextView) getActivity().findViewById(R.id.sign_up);
        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                onKeyDown(KeyEvent.KEYCODE_BACK,((LoginActivity)getActivity()).getEvent());

            }
        });
        return rootView;
    }

    @OnClick({R.id.user_register, R.id.clear_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_register:
                checkUserFlag();
                break;
            case R.id.clear_info:
                clearAllEdt();
                break;
        }
    }

    private void clearAllEdt(){
        userName.setText(null);
        firstPwd.setText(null);
        secondPwd.setText(null);
    }
    private void checkUserFlag(){
        if (userName.getText().toString().isEmpty()||firstPwd.getText().toString().isEmpty()||secondPwd.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"数据不能为空！",Toast.LENGTH_SHORT).show();
        }else if (!firstPwd.getText().toString().equals(secondPwd.getText().toString())){
            Toast.makeText(getActivity(),"两次密码输入不相同！",Toast.LENGTH_SHORT).show();
        }else if (userName.getText().toString().equals("登陆")){
            Toast.makeText(getActivity(),"此用户名不可用！",Toast.LENGTH_SHORT).show();
        } else{
            dbUtils=new CommonUtils(getActivity(),"videoinfo.db");
            new AsyncTask<String,String,UserInfo>(){
                private ProgressDialog dialog;
                private boolean isexist=false;
                private String user_name=userName.getText().toString();
                @Override
                protected void onPreExecute() {
                    dialog=ProgressDialog.show(getActivity(),"","正在验证用户......");
                    super.onPreExecute();
                }

                @Override
                protected UserInfo doInBackground(String... params) {
                    if (dbUtils.queryBuilderUserInfo(user_name)){
                        isexist=true;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(UserInfo userInfo) {
                    dialog.dismiss();
                    if (isexist){
                        Toast.makeText(getActivity(),"用户名已存在！",Toast.LENGTH_SHORT).show();
                    }else {
                        UserInfo user=new UserInfo();
                        user.setUser_Name(userName.getText().toString());
                        user.setUser_PassWord(firstPwd.getText().toString());
                        if (dbUtils.insertUserInfo(user)){
                            dbUtils.getDaoManager().closeDaoSession();
                            Toast.makeText(getActivity(),"注册成功！",Toast.LENGTH_SHORT).show();
                            onKeyDown(KeyEvent.KEYCODE_BACK,((LoginActivity)getActivity()).getEvent());
                        }else {
                            Toast.makeText(getActivity(),"注册失败！",Toast.LENGTH_SHORT).show();
                        }
                        clearAllEdt();
                    }
                    super.onPostExecute(userInfo);
                }
            }.execute();

        }
    }
    public void onKeyDown(int keyCode, KeyEvent event) {
        clearAllEdt();
        signUp.setVisibility(View.VISIBLE);
        ((LoginActivity)getActivity()).setIndexSelected(0);
    }
}
