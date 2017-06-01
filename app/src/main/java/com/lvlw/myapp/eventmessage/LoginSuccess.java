package com.lvlw.myapp.eventmessage;

/**
 * Created by Wantrer on 2017/5/9 0009.
 */

public class LoginSuccess {
    public LoginSuccess(String username) {
        this.username = username;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
