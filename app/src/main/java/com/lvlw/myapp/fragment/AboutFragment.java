package com.lvlw.myapp.fragment;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lvlw.myapp.MainActivity;
import com.lvlw.myapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import solid.ren.skinlibrary.base.SkinBaseFragment;


public class AboutFragment extends SkinBaseFragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    private RelativeLayout mRlHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_share, null);
        //		initToolbar();
        ButterKnife.bind(this, rootView);
        mRlHome= (RelativeLayout) getActivity().findViewById(R.id.rl_home);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlHome.performClick();

            }
        });
        collapsingToolbarLayout.setTitle("关于");
        return rootView;
    }
}
