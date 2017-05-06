package com.lvlw.myapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lvlw.myapp.R;
import com.lvlw.myapp.adapter.FragmentAdapter;

import solid.ren.skinlibrary.base.SkinBaseFragment;


/**
 * Created by w9859 on 2017/3/10.
 */

public class HomeFragment extends SkinBaseFragment {

    //重命名参数设置中的参数，选择匹配的参数名
    //初始化fragment的参数设置，比如参数个数
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //重命名和选择参数的类型
    private String mParam1;
    private String mParam2;


    private ViewPager mViewPager;
    private AppBarLayout mAppBarLayout;
//    private SelectedFragment selectedFragment;
    private SelectedFragment selecteFragment;
    private SubscribeFragment subscribeFragment;
    private FindFragment findFragment;
    private DrawerLayout mDrawerLayout;
//    private ;
    public HomeFragment() {

    }

    /**
     * 根据已经提供的参数去用机械化的方法创建新的fragment实例
     *
     * @param param1
     * @param param2
     * @return HomeFragment的一个新的fragment实例
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_home,null);
        mViewPager= (ViewPager) rootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);

        FragmentAdapter pagerAdapter=new FragmentAdapter(getActivity().getSupportFragmentManager());

//        selectedFragment=new SelectedFragment();
        selecteFragment=new SelectedFragment();
        subscribeFragment=new SubscribeFragment();
        findFragment=new FindFragment();

        pagerAdapter.addFragment(selecteFragment,"科技");
        pagerAdapter.addFragment(subscribeFragment,"游戏");
        pagerAdapter.addFragment(findFragment,"电影");

        mViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout= (TabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        Toolbar toolbar= (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_menu_white);
        toolbar.setTitle("新闻频道");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white_normal));
        mDrawerLayout= (DrawerLayout) getActivity().findViewById(R.id.activity_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout!=null){
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }
        });
        dynamicAddView(toolbar,"background",R.color.main_color);



        mAppBarLayout= (AppBarLayout) rootView.findViewById(R.id.appBarLayout);
//        mAppBarLayout= (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
//        mAppBarLayout.addView(tabLayout);
        mAppBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);
//        initToolbar();
        return rootView;
    }

//    private void initToolbar(){
//        Toolbar toolbar= (Toolbar) getActivity().findViewById(R.id.main_toolbar);
//        toolbar.inflateMenu(R.menu.toolbar_menu);
//    }

    AppBarLayout.OnOffsetChangedListener onOffsetChangedListener=new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            //i>=0 Toolbar全部显示
            subscribeFragment.setPullRefresh(verticalOffset>=0);
            selecteFragment.setPullRefresh(verticalOffset>=0);
            findFragment.setPullRefresh(verticalOffset>=0);
//            selectdFragment.setPullRefresh(verticalOffset<0);
            System.out.println("i值:"+verticalOffset);
        }
    };
}
