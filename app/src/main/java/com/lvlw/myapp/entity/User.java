package com.lvlw.myapp.entity;

/**
 * Created by Wantrer on 2017/5/12 0012.
 */

public class User {
    private String user_Name;
    private String user_PassWord;
    private boolean re_pwd_check=false;
    private boolean auto_login=false;

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public String getUser_PassWord() {
        return user_PassWord;
    }

    public void setUser_PassWord(String user_PassWord) {
        this.user_PassWord = user_PassWord;
    }

    public boolean isRe_pwd_check() {
        return re_pwd_check;
    }

    public void setRe_pwd_check(boolean re_pwd_check) {
        this.re_pwd_check = re_pwd_check;
    }

    public boolean isAuto_login() {
        return auto_login;
    }

    public void setAuto_login(boolean auto_login) {
        this.auto_login = auto_login;
    }
}
