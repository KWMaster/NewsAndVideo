package com.lvlw.myapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by w9859 on 2017/3/11.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter{
    private  final List<Fragment> mFragments=new ArrayList<>();
    private  final List<String> titles=new ArrayList<>();
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        mFragments.add(fragment);
        titles.add(title);
    }


}
