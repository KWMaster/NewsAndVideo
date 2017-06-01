package com.lvlw.myapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lvlw.myapp.R;
import com.lvlw.myapp.fragment.LoginFragment;
import com.lvlw.myapp.fragment.RegisterFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import solid.ren.skinlibrary.base.SkinBaseActivity;

/**
 * Created by Wantrer on 2017/5/8 0008.
 */

public class LoginActivity extends SkinBaseActivity {
    @BindView(R.id.login_content)
    FrameLayout loginContent;
    @BindView(R.id.login_back)
    ImageView loginBack;
    @BindView(R.id.sign_up)
    TextView signUp;
    private List<Fragment> mFragments;
    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mFragments = new ArrayList<>();
        LoginFragment loginFragment = new LoginFragment();
        RegisterFragment registerFragment = new RegisterFragment();
        mFragments.add(loginFragment);
        mFragments.add(registerFragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.login_content, loginFragment).commit();
        setIndexSelected(0);
    }

    public void setIndexSelected(int index) {
        if (mIndex == index) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.hide(mFragments.get(mIndex));

        if (!mFragments.get(index).isAdded()) {
            ft.add(R.id.login_content, mFragments.get(index));
        } else {
            ft.show(mFragments.get(index));
        }
        ft.commit();

        mIndex = index;
    }

    @OnClick({R.id.login_back, R.id.sign_up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_back:
                break;
            case R.id.sign_up:
                signUp.setVisibility(View.GONE);
                setIndexSelected(1);
                break;
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
            if (mFragments.get(mIndex) instanceof RegisterFragment) {
                ((RegisterFragment) mFragments.get(mIndex)).onKeyDown(keyCode, event);
                return true;
            } else if (mFragments.get(mIndex) instanceof LoginFragment) {
                ((LoginFragment) mFragments.get(mIndex)).onKeyDown(keyCode, event);
                return true;
            }
        }
        return false;
    }
}
