package com.lvlw.myapp.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.VideoInfo.entity.UserInfo;
import com.lvlw.myapp.R;
import com.lvlw.myapp.entity.User;
import com.lvlw.myapp.shareprefrence.ListDataSave;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wantrer on 2017/5/11 0011.
 */

public class RemenberUserAdapter extends BaseAdapter {
    private Context context;
    private EditText user_name;
    private EditText user_pwd;
    private ListPopupWindow lpw;
    private CheckBox rememberUser;
    private CheckBox autoLogin;
    private LayoutInflater mInflater;
    private List<User> users;
    private ListDataSave listDataSave;

    public RemenberUserAdapter(Context context, List<User> users, EditText user_name, EditText user_pwd, ListPopupWindow lpw, CheckBox rememberUser, CheckBox autoLogin) {
        this.context = context;
        this.user_name=user_name;
        this.user_pwd=user_pwd;
        this.lpw=lpw;
        this.rememberUser=rememberUser;
        this.autoLogin=autoLogin;
        this.mInflater = LayoutInflater.from(this.context);
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView==null){
            convertView = mInflater.inflate(R.layout.remenberuseradapter_item, null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        User user=users.get(position);
        if (user!=null){
            holder.userNameLogin.setText(user.getUser_Name());
        }
        holder.userNameLogin.setOnClickListener(new onSetUserNameClickListener(user,user_name,user_pwd,rememberUser,autoLogin));
        holder.userNameDelete.setOnClickListener(new onDeleteUserNameClickListener(user,user_name,rememberUser,autoLogin));
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.user_name_login)
        TextView userNameLogin;
        @BindView(R.id.user_name_delete)
        ImageView userNameDelete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    private class onSetUserNameClickListener implements View.OnClickListener{
        private EditText user_name;
        private EditText user_pwd;
        private CheckBox rememberUser;
        private CheckBox autoLogin;
        private User user;
        public onSetUserNameClickListener(User user, EditText user_name, EditText user_pwd, CheckBox rememberUser, CheckBox autoLogin) {
            this.user=user;
            this.user_name = user_name;
            this.user_pwd=user_pwd;
            this.rememberUser=rememberUser;
            this.autoLogin=autoLogin;
        }
        @Override
        public void onClick(View v) {
            user_name.setText(user.getUser_Name());
            user_pwd.setText(user.getUser_PassWord());
            rememberUser.setChecked(true);
            autoLogin.setChecked(user.isAuto_login());
            if (lpw.isShowing()){
                lpw.dismiss();
            }
        }
    }
    private class onDeleteUserNameClickListener implements View.OnClickListener{
        private User user;
        private EditText user_name;
        private CheckBox rememberUser;
        private CheckBox autoLogin;
        public onDeleteUserNameClickListener(User user, EditText user_name, CheckBox rememberUser, CheckBox autoLogin) {
            this.user=user;
            this.user_name = user_name;
            this.rememberUser=rememberUser;
            this.autoLogin=autoLogin;
        }

        @Override
        public void onClick(View v) {
            users.remove(user);
            if (user_name.getText().toString().equals(user.getUser_Name())){
                user_name.setText(null);
                user_pwd.setText(null);
                if (rememberUser.isChecked()){
                    rememberUser.performClick();
                }
                if (autoLogin.isChecked()){
                    autoLogin.performClick();
                }
            }
            notifyDataSetChanged();
            if (lpw.isShowing()){
                lpw.dismiss();
            }
            new AsyncTask<String,String,User>(){
                private ProgressDialog dialog;
                private boolean auto_login=autoLogin.isChecked();
                @Override
                protected void onPreExecute() {
                    dialog = ProgressDialog.show(context, "", "正在登陆，请稍后......");
                    super.onPreExecute();
                }

                @Override
                protected User doInBackground(String... params) {
                    listDataSave=new ListDataSave(context);
                    List<User> userAutoLogin=listDataSave.getDataList("autologin");
                    if (listDataSave.getDataList("users")!=null&&listDataSave.getDataList("users").size()>0){
                        List<User> userList=listDataSave.getDataList("users");
                        boolean exist=false;
                        for (User useritem : userList) {
                            if (useritem.getUser_Name().equals(user.getUser_Name())) {
                                exist = true;
                                userList.remove(useritem);
                                break;
                            }
                        }
                        if (exist){
                            if (userList.size()<=0){
                                listDataSave.clear(context,"users");
                            }else {
                                listDataSave.setDataList("users", userList,"autologin",userAutoLogin);
                            }
                            if (user.isAuto_login()){
                                if (listDataSave.getDataList("autologin")!=null&&listDataSave.getDataList("autologin").size()>0){
                                    listDataSave.clear(context,"autologin");
                                }
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(User user) {
                    dialog.dismiss();
                    super.onPostExecute(user);
                }
            }.execute();
        }
    }
}
