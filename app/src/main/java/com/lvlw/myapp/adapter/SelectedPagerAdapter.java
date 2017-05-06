package com.lvlw.myapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lvlw.myapp.entity.NewsData;
import com.lvlw.myapp.iview.ICarousePagerSelectView;

import java.util.ArrayList;
import java.util.List;


/**
 * 轮播动画 ViewPager适配器
 * Created by w9859 on 2017/3/12.
 */

public class SelectedPagerAdapter extends PagerAdapter implements View.OnClickListener{


    private List<View> mViews;
    //每页显示的图片
    private final ArrayList<NewsData.ShowapiResBodyBean.PagebeanBean.ContentlistBean> imageViews=new ArrayList<>();
    private final ArrayList<NewsData.ShowapiResBodyBean.PagebeanBean.ContentlistBean> imageViewss=new ArrayList<>();
//    private List<String> imageViewstr;
    private ICarousePagerSelectView mICarousePagerSelectView;

    public SelectedPagerAdapter(Context context, ICarousePagerSelectView ICarousePagerSelectView, List<View> mViews) {
//        imageViewstr=imageViewstring;
        mICarousePagerSelectView = ICarousePagerSelectView;
        this.mViews=mViews;

    }

    public SelectedPagerAdapter() {
    }

    public List<View> getViews() {
        return mViews;
    }

    @Override
    public int getCount() {
        return mViews==null?0:mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        ViewGroup parent = (ViewGroup) mViews.get(position).getParent();
//        parent.removeAllViews();
        if (mViews.get(position).getParent()==null){
            ((ViewPager)container).addView(mViews.get(position),0);
            setListener(position,(SimpleDraweeView) mViews.get(position));
        }else {
            ((ViewGroup)mViews.get(position).getParent()).removeView(mViews.get(position));
            setListener(position,(SimpleDraweeView) mViews.get(position));
            ((ViewPager)container).addView(mViews.get(position),0);
        }
        return mViews.get(position);
    }

    @Override
    public void onClick(View v) {
        int index= (int) v.getTag();
        mICarousePagerSelectView.carouseSelect(index);
    }

    @Override
    public int getItemPosition(Object object) {
        return null!=mViews?POSITION_NONE:getItemPosition(object);
//        return POSITION_NONE;
    }

//    public List<String> getImageViewstr() {
//        return imageViewstr;
//    }

    public List<View> getmViews() {
        return mViews;
    }

    public void setListener(int tag,SimpleDraweeView simpleDraweeView){
        simpleDraweeView.setTag(tag);
        simpleDraweeView.setOnClickListener(this);
    }
}
