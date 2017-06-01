package com.lvlw.myapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.lvlw.myapp.MainActivity;
import com.lvlw.myapp.R;
import com.lvlw.myapp.fragment.LoginFragment;
import com.lvlw.myapp.fragment.RegisterFragment;
import com.lvlw.myapp.fragment.UserDetilsOne;
import com.lvlw.myapp.fragment.UserDetilsTwo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import solid.ren.skinlibrary.base.SkinBaseActivity;

/**
 * Created by Wantrer on 2017/5/8 0008.
 */

public class UserDetilsActivity extends SkinBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    private List<Fragment> mFragments;
    private int mIndex;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detils);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        collapsingToolbarLayout.setTitle(getIntent().getExtras().getString("username"));
        initView();
    }

    private void initView() {
        mFragments = new ArrayList<>();
        UserDetilsOne userDetilsOne = new UserDetilsOne();
        UserDetilsTwo userDetilsTwo = new UserDetilsTwo();
        mFragments.add(userDetilsOne);
        mFragments.add(userDetilsTwo);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.userdetils_content, userDetilsOne).commit();
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
            ft.add(R.id.userdetils_content, mFragments.get(index));
        } else {
            ft.show(mFragments.get(index));
        }
        ft.commit();

        mIndex = index;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            if (mFragments.get(mIndex) instanceof UserDetilsTwo) {
                ((UserDetilsTwo)mFragments.get(mIndex)).clearAllEdt();
                setIndexSelected(0);
                return true;
            }else {
                finish();
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
