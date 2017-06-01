package com.lvlw.myapp.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoInfo.entity.UserInfo;
import com.lvlw.myapp.R;
import com.lvlw.myapp.adapter.RemenberUserAdapter;
import com.lvlw.myapp.entity.User;
import com.lvlw.myapp.eventmessage.LoginSuccess;
import com.lvlw.myapp.greendao.CommonUtils;
import com.lvlw.myapp.shareprefrence.ListDataSave;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Wantrer on 2017/5/8 0008.
 */

public class LoginFragment extends Fragment {

    @BindView(R.id.input_layout_name)
    LinearLayout inputLayoutName;
    @BindView(R.id.input_layout_psw)
    LinearLayout inputLayoutPsw;
    @BindView(R.id.main_btn_login)
    TextView mainBtnLogin;
    @BindView(R.id.login_name)
    EditText loginName;
    @BindView(R.id.login_pwd)
    EditText loginPwd;
    @BindView(R.id.remember_user)
    CheckBox rememberUser;
    @BindView(R.id.auto_login)
    CheckBox autoLogin;
    private ImageView loginBack;
    private View inputLayout;
    private View progressBar2;
    private CommonUtils dbUtils;

    private ListPopupWindow lpw;
    private List<User> users;
    private ListDataSave listDataSave;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_login, null);
        ButterKnife.bind(this, rootView);
        inputLayout = rootView.findViewById(R.id.input_layout);
        progressBar2 = rootView.findViewById(R.id.layout_progress);
        loginBack = (ImageView) getActivity().findViewById(R.id.login_back);
        mainBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLoginFlag();
            }
        });
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (autoLogin.isChecked()){
                    if (!rememberUser.isChecked()){
                        rememberUser.setChecked(true);
                    }
                    autoLogin.setChecked(true);
                }else {
                    autoLogin.setChecked(false);
                }
            }
        });
        rememberUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!rememberUser.isChecked()) {
                    if (autoLogin.isChecked()) {
                        autoLogin.setChecked(false);
                    }
                }
            }
        });
//        autoLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean a=autoLogin.isChecked();
//
//            }
//        });
        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        listDataSave = new ListDataSave(getActivity());
        //        list = new String[] { "item1", "item2", "item3", "item4" };
        //        for (int i=0;i<5;i++){
        //            User user=new User();
        //            user.setUser_Name("user"+i);
        //            user.setUser_PassWord("123");
        //            users.add(user);
        //        }
        users = listDataSave.getDataList("users");
        users.size();
        lpw = new ListPopupWindow(getActivity());
        RemenberUserAdapter remenberUserAdapter = new RemenberUserAdapter(getActivity(), users, loginName, loginPwd, lpw,rememberUser,autoLogin);
        lpw.setAdapter(remenberUserAdapter);
        lpw.setAnchorView(loginName);
        lpw.setModal(true);
        //        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //            @Override
        //            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //                loginName.setText(list[position]);
        //                lpw.dismiss();
        //            }
        //        });

        loginName.setOnTouchListener(new openListPopupWindowOnTouchListener());
        return rootView;
    }

    private class openListPopupWindowOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getX() >= (v.getWidth() - ((EditText) v)
                        .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    lpw.show();
                    return true;
                }
            }
            return false;
        }
    }

    private void checkLoginFlag() {
        if (loginName.getText().toString().isEmpty() || loginPwd.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "用户名密码不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            dbUtils = new CommonUtils(getActivity(), "videoinfo.db");
            new AsyncTask<String, String, UserInfo>() {
                private ProgressDialog dialog;
                private boolean isexist = false;
                private boolean isaccess = false;
                private String user_name = loginName.getText().toString();
                private String user_pwd = loginPwd.getText().toString();

                @Override
                protected void onPreExecute() {
                    dialog = ProgressDialog.show(getActivity(), "", "正在登陆，请稍后......");
                    super.onPreExecute();
                }

                @Override
                protected UserInfo doInBackground(String... params) {
                    if (dbUtils.queryBuilderUserInfo(user_name)) {
                        isexist = true;
                    }
                    if (isexist) {
                        UserInfo user = dbUtils.queryBuilderOneUserInfo(user_name);
                        if (user_pwd.equals(user.getUser_PassWord())) {
                            isaccess = true;
                        }
                    }
                    dbUtils.getDaoManager().closeDaoSession();
                    return null;
                }

                @Override
                protected void onPostExecute(UserInfo userInfo) {
                    dialog.dismiss();
                    if (!isexist) {
                        Toast.makeText(getActivity(), "用户名不存在！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!isaccess) {
                            Toast.makeText(getActivity(), "密码错误！", Toast.LENGTH_SHORT).show();
                        } else {
                            new AsyncTask<String, String, User>() {
                                private boolean re_pwd_check = rememberUser.isChecked();
                                private boolean auto_login = autoLogin.isChecked();

                                @Override
                                protected void onPreExecute() {
                                    dialog = ProgressDialog.show(getActivity(), "", "正在登陆，请稍后......");
                                    super.onPreExecute();
                                }

                                @Override
                                protected User doInBackground(String... params) {
                                    List<User> userAutoLogin=listDataSave.getDataList("autologin");
                                    if (re_pwd_check){
                                        if (listDataSave.getDataList("users")!=null){
                                            List<User> userList=listDataSave.getDataList("users");
                                            if (userList.size()>0){
                                                for (User user : userList) {
                                                    if (user_name.equals(user.getUser_Name())){
                                                        userList.remove(user);
                                                        break;
                                                    }
                                                }
                                                    User user=new User();
                                                    user.setUser_Name(user_name);
                                                    user.setUser_PassWord(user_pwd);
                                                    user.setRe_pwd_check(true);
                                                    userList.add(user);
                                                    listDataSave.setDataList("users",userList,"autologin",userAutoLogin);
                                            }else {
                                                addUserList("autologin",userAutoLogin);
                                            }
                                        }else {
                                            addUserList("autologin",userAutoLogin);
                                        }
                                        if (auto_login){
                                            List<User> users=new ArrayList<User>();
                                            User user=new User();
                                            user.setUser_Name(user_name);
                                            user.setUser_PassWord(user_pwd);
                                            user.setRe_pwd_check(true);
                                            user.setAuto_login(true);
                                            users.add(user);
                                            List<User> userList=listDataSave.getDataList("users");
                                            for (User useritem : userList) {
                                                if (useritem.isAuto_login()) {
                                                    useritem.setAuto_login(false);
                                                    break;
                                                }
                                            }
                                            for (User useritem : userList) {
                                                if (useritem.getUser_Name().equals(user.getUser_Name())) {
                                                    userList.remove(useritem);
                                                    userList.add(user);
                                                    break;
                                                }
                                            }
                                            listDataSave.setDataList("users",userList,"autologin",users);
                                        }else {
                                            if (listDataSave.getDataList("autologin")!=null&&listDataSave.getDataList("autologin").size()>=0){
                                                List<User> userList=listDataSave.getDataList("users");
                                                for (User useritem : userList) {
                                                    if (useritem.isAuto_login()) {
                                                        useritem.setAuto_login(false);
                                                        break;
                                                    }
                                                }
                                                    listDataSave.setDataList("users",userList,"autologin",null);
                                                    listDataSave.clear(getActivity(),"autologin");
                                            }
                                        }
                                    }else {
                                        if (listDataSave.getDataList("users")!=null&&listDataSave.getDataList("users").size()>0){
                                            List<User> userList=listDataSave.getDataList("users");
                                            boolean exist=false;
                                            for (User user : userList) {
                                                if (user_name.equals(user.getUser_Name())) {
                                                    exist = true;
                                                    userList.remove(user);
                                                    break;
                                                }
                                            }
                                            if (exist){
                                                if (userList.size()<=0){
                                                    listDataSave.clear(getActivity(),"users");
                                                }else {
                                                    listDataSave.setDataList("users", userList,"autologin",userAutoLogin);
                                                }
                                            }
                                        }
                                        if (!auto_login){
                                            if (listDataSave.getDataList("autologin")!=null&&listDataSave.getDataList("autologin").size()>0){
                                                listDataSave.clear(getActivity(),"autologin");
                                            }
                                        }
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(User user) {
                                    dialog.dismiss();
                                    LoginSuccess access = new LoginSuccess(user_name);
                                    EventBus.getDefault().post(access);
                                    clearAllEdt();
                                    getActivity().onBackPressed();
                                    super.onPostExecute(user);
                                }
                            }.execute();
                        }
                    }
                    super.onPostExecute(userInfo);
                }
            }.execute();

        }
    }

    private void addUserList(String autologin,List<User> autoList) {
        List<User> userList=new ArrayList<User>();
        User user=new User();
        user.setUser_Name(loginName.getText().toString());
        user.setUser_PassWord(loginPwd.getText().toString());
        user.setRe_pwd_check(true);
        userList.add(user);
        listDataSave.setDataList("users",userList,autologin,autoList);
    }
    private void clearAllEdt() {
        loginName.setText(null);
        loginPwd.setText(null);
    }


    public void onKeyDown(int keyCode, KeyEvent event) {
        getActivity().onBackPressed();
    }
}
