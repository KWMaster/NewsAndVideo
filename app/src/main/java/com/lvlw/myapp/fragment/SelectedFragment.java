package com.lvlw.myapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lvlw.myapp.R;
import com.lvlw.myapp.activity.NewsActivity;
import com.lvlw.myapp.adapter.NewsAdapter;
import com.lvlw.myapp.adapter.SelectedPagerAdapter;
import com.lvlw.myapp.api.AppConfigs;
import com.lvlw.myapp.api.GetDataService;
import com.lvlw.myapp.entity.DataBean;
import com.lvlw.myapp.entity.DataInfo;
import com.lvlw.myapp.entity.NewsData;
import com.lvlw.myapp.iview.ICarousePagerSelectView;
import com.lvlw.myapp.utils.GetRandomListInt;
import com.lvlw.myapp.utils.ItemClickSupport;
import com.lvlw.myapp.utils.UIUtils;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.rawn_hwang.library.widgit.DefaultLoadingLayout;
import me.rawn_hwang.library.widgit.SmartLoadingLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import solid.ren.skinlibrary.base.SkinBaseFragment;

import static android.content.ContentValues.TAG;


/**
 * Created by w9859 on 2017/3/11.
 */

public class SelectedFragment extends SkinBaseFragment {
    @BindView(R.id.fab1)
    FloatingActionButton fab1;
    private View headView;
    private ViewPager mViewPager;
    private SelectedPagerAdapter mSelectedPagerAdapter;

    private ImageView[] tips;//底部

    private Timer mTimer;
    private final int CAROUSEL_TIMR = 3000;//滚动间隔

    private int currentIndex = 0;//当前选中

    private float curTranslationY;
    private AnimatorSet animSet;
    private TextView tvContent;
    private TextView refreshIndicator;
    private PtrClassicFrameLayout mPtrClassicFrameLayout;
    private RecyclerView mRecyclerView;
    //    private SelectedRecyclerAdapter mSelectedRecyclerAdapter;
    private RecyclerAdapterWithHF mWithHF;
    private DefaultLoadingLayout mLoadingLayout;
    private int page = 1;
    private String picture="http://mat1.gtimg.com/news/2013pic/picLogo.png";
    private boolean canrefresh=true;
    //===========================================================================================================================================


    private List<DataBean> mDatas = new ArrayList<>();
    private List<DataBean> dataBeans = new ArrayList<>();
    private static final String url = "http://www.imooc.com/api/teacher?type=4&num=30";
    //Volley begin
    private RequestQueue mQueue;
    private RequestQueue queue;

    //    //Volley end
    //
    private RequestQueue initRequestQueue(Context context) {
        queue = Volley.newRequestQueue(context);
        return queue;
    }

    private List<DataBean> getDataByVolley() {
        //json 四个参数分别是:url, JSONObject对象这里为null，一个请求成功的Listener，和一个请求失败的Listener
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (null != jsonObject) {
                    //传入的"data" 是根据json返回字符串得来的
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        //上一句代码可能报错，确认不报错再创建下面的对象
                        //使用GSON加载 begin
                        String dataString = jsonArray.toString();
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        List<DataBean> beans = gson.fromJson(dataString, new TypeToken<List<DataBean>>() {
                        }.getType());
                        //                        for (DataBean d:beans
                        //                             ) {
                        //                            DataBean bean=new DataBean();
                        //                            bean.setName(d.getName());
                        //                            bean.setPicSmall(d.getPicSmall());
                        //                            mDatas.add(bean);
                        ////                            Log.d(TAG,d.getName()+" "+d.getPicSmall());
                        //                        }
                        //使用GSON加载 end
                        //数据加载完后 再加载适配器
                        //                        init(mDatas);
                        for (DataBean bean : beans) {
                            dataBeans.add(bean);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, volleyError.getMessage(), volleyError);
            }
        });
        queue.add(jsonObjectRequest);

        return dataBeans;
    }

    //    public DataAdapter(Context context,List<DataBean> dataBeens,RequestQueue queue) {
    //        initRequestQueue(context);
    //        getDataByVolley();
    //    }

    //===========================================================================================================================================
    private ArrayList<DataInfo.Info> arrayList;
    private ArrayList<DataInfo.Info> aList = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private List<String> imageViewstr;
    private ArrayList<NewsData.ShowapiResBodyBean.PagebeanBean.ContentlistBean> list;
    private List<View> mViews;
    private List<Integer> carouseTitles;
    private List<String> carousePageStrs;

    private void initData(final int pages, final boolean refreshheader) {
        //使用retrofit配置api
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfigs.URL_DATA)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //        Api api=retrofit.create(Api.class);
        GetDataService getDataService = retrofit.create(GetDataService.class);
        Map<String, String> map = new HashMap<>();
        map.put(AppConfigs.APPID_NAME, AppConfigs.APPID);
        map.put(AppConfigs.SECRECT_NAME, AppConfigs.SECRECT);
        map.put(AppConfigs.CHANNELID_NAME, AppConfigs.CHANNELID_TECHOLOGY);
        map.put(AppConfigs.MAXRESULT_NAME, AppConfigs.MAXRESULT);
        map.put(AppConfigs.PAGE_NAME, "" + pages);
        //        Call<DataInfo> call=api.getData(5,pages);
        //        call.enqueue(new Callback<DataInfo>() {
        //            @Override
        //            public void onResponse(Call<DataInfo> call, retrofit2.Response<DataInfo> response) {
        //                aList.addAll(response.body().results);
        //            }
        //
        //            @Override
        //            public void onFailure(Call<DataInfo> call, Throwable t) {
        //
        //            }
        //        });
        Call<NewsData> call = getDataService.getBeansData(map);
        call.enqueue(new Callback<NewsData>() {
            @Override
            public void onResponse(Call<NewsData> call, retrofit2.Response<NewsData> response) {
                NewsData newsData = response.body();
                if (newsData != null) {
                    if (refreshheader) {
                        newsAdapter.getList().clear();
                        carousePageStrs.clear();
                        list.addAll(newsData.getShowapi_res_body().getPagebean().getContentlist());
                        imageViewstr = new ArrayList<String>();
                        carouseTitles=new ArrayList<Integer>();
                        for (int i = 5; i < list.size(); i++) {
                            if (list.get(i).isHavePic()) {
                                imageViewstr.add(list.get(i).getImageurls().get(0).getUrl());
                                carousePageStrs.add(list.get(i).getTitle());
                                carouseTitles.add(i);
                                i += 2;
                            }
                            if (imageViewstr.size() == 3) {
                                break;
                            }
                        }
                        GetRandomListInt listInt=new GetRandomListInt(list);
                        switch (imageViewstr.size()){
                            case 0:
                                List<Integer> newint1=listInt.getRandom(carouseTitles);
                                carousePageStrs.add(list.get(newint1.get(0)).getTitle());
                                carousePageStrs.add(list.get(newint1.get(1)).getTitle());
                                carousePageStrs.add(list.get(newint1.get(2)).getTitle());
                                imageViewstr.add(picture);
                                imageViewstr.add(picture);
                                imageViewstr.add(picture);
                                carouseTitles=newint1;
                                break;
                            case 1:
                                List<Integer> newint2=listInt.getRandom(carouseTitles);
                                carousePageStrs.add(list.get(newint2.get(0)).getTitle());
                                carousePageStrs.add(list.get(newint2.get(1)).getTitle());
                                imageViewstr.add(picture);
                                imageViewstr.add(picture);
                                carouseTitles.add(newint2.get(0));
                                carouseTitles.add(newint2.get(1));
                                break;
                            case 2:
                                List<Integer> newint3=listInt.getRandom(carouseTitles);
                                carousePageStrs.add(list.get(newint3.get(0)).getTitle());
                                imageViewstr.add(picture);
                                carouseTitles.add(newint3.get(0));
                                break;
                        }
                        //        for (int i=0;i<images.length;i++){//初始化每页显示的View
                        for (int j = 0; j < imageViewstr.size(); j++) {//初始化每页显示的View
                            //            if (header.get(i).isHavePic()&&ishavepic<3) {
                            View item = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_selected_pager_item, null);
                            //            ImageView imageView= (ImageView) item.findViewById(R.id.imageview);
                            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) item.findViewById(R.id.news_imageview);
                            //            imageView.setImageResource(images[i]);
                            //            imageView.setTag(i);
                            //            imageView.setOnClickListener(this);
                            simpleDraweeView.setImageURI(imageViewstr.get(j));
                            //                            simpleDraweeView.setTag(j);
                            //                            simpleDraweeView.setOnClickListener((View.OnClickListener) mICarousePagerSelectView);
                            //            mViews.add(imageView);
                            mViews.add(simpleDraweeView);
                            //            }else break;
                        }
                        //                        if (mSelectedPagerAdapter.getmViews()!=null&&mSelectedPagerAdapter.getmViews().size()>0){
                        ////                            for (int i = 0; i < mSelectedPagerAdapter.getmViews().size(); i++) {
                        //                                mSelectedPagerAdapter.getmViews().clear();
                        ////                            }
                        //                        }
//                        tvContent.setText(carousePageStrs.get(0));
                        mSelectedPagerAdapter.notifyDataSetChanged();
                        mWithHF.notifyDataSetChanged();
                        mPtrClassicFrameLayout.refreshComplete();
                        mLoadingLayout.onDone();
                        refreshIndicator.setVisibility(View.VISIBLE);
                        refreshIn();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                refreshIndicator.setVisibility(View.GONE);
                                refreshOut();
                            }
                        }, 2000);
                    } else {
                        list.addAll(newsData.getShowapi_res_body().getPagebean().getContentlist());
                        mWithHF.notifyDataSetChanged();
                        mPtrClassicFrameLayout.loadMoreComplete(true);//下拉加载完毕
                        Toast.makeText(getActivity(), "更新了20条新闻", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsData> call, Throwable t) {
                if (refreshheader) {
                    mPtrClassicFrameLayout.loadMoreComplete(true);//下拉加载完毕
                    mLoadingLayout.onError();
                    mLoadingLayout.setErrorButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mLoadingLayout.onLoading();
                            initData(pages, true);
                        }
                    });
                } else {
                    mPtrClassicFrameLayout.loadMoreComplete(true);
                    page--;
                    Toast.makeText(getActivity(), "加载更多新闻失败", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    //===========================================================================================================================================


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_selected, null);
        ButterKnife.bind(this, rootView);
        refreshIndicator = (TextView) rootView.findViewById(R.id.refresh_indicator);
        mPtrClassicFrameLayout = (PtrClassicFrameLayout) rootView.findViewById(R.id.recycler_view_frame);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLoadingLayout = SmartLoadingLayout.createDefaultLayout(getActivity(), mPtrClassicFrameLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //        mQueue=initRequestQueue(getActivity());
        //        mDatas=getDataByVolley();
        list = new ArrayList<>();
        mViews = new ArrayList<>();
        carousePageStrs = new ArrayList<>();
        mLoadingLayout.onLoading();
        initData(1, true);
        //        new Handler().postDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                mLoadingLayout.onDone();
        //            }
        //        }, 2000);
        //        mDataAdapter=new DataAdapter(getActivity(),initData(1));
        newsAdapter = new NewsAdapter(getActivity(), list);
        mWithHF = new RecyclerAdapterWithHF(newsAdapter);
        mSelectedPagerAdapter = new SelectedPagerAdapter();
        View header = initCarouselHead(mViews);
        mWithHF.addHeader(header);
        mRecyclerView.setAdapter(mWithHF);
        initView();
        initListener();
        //        init();
        //        mPtrClassicFrameLayout.refreshComplete();
        mPtrClassicFrameLayout.setPtrHandler(ptrDefultHandler);//设置下拉监听
        mPtrClassicFrameLayout.setOnLoadMoreListener(onLoadMoreListener);//设置上拉监听
        mPtrClassicFrameLayout.setLoadMoreEnable(true);//设置可以加载更多
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Toast.makeText(getActivity(), "第" + position + "条数据:" + list.get(position - 1).getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                //采用Intent普通传值的方式
                ArrayList<String> bundlestr = new ArrayList<String>();
                bundlestr.add(list.get(position - 1).getTitle());
                bundlestr.add(list.get(position - 1).getLink());
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("link", bundlestr);
                intent.putExtras(bundle);
                //跳转Activity
                startActivity(intent);
            }
        });

        curTranslationY = refreshIndicator.getTranslationY();
        Animator moveOut1 = ObjectAnimator.ofFloat(refreshIndicator, "translationY", curTranslationY, -70f);
        moveOut1.setDuration(1);
        moveOut1.start();
        animSet = new AnimatorSet();

        return rootView;
    }

    public void refreshIn() {
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(refreshIndicator, "translationY", -70f, curTranslationY);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(refreshIndicator, "alpha", 0f, 1f);
        AnimatorSet animIn = new AnimatorSet();
        animIn.play(moveIn).with(fadeIn);
        animIn.setDuration(800);
        animIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                canrefresh=false;
                super.onAnimationStart(animation);
            }
        });
        if (animSet.isRunning()) {
            return;
        } else {
            animIn.start();
            animSet = animIn;
        }

    }

    public void refreshOut() {
        ObjectAnimator moveOut = ObjectAnimator.ofFloat(refreshIndicator, "translationY", curTranslationY, -70f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(refreshIndicator, "alpha", 1f, 0f);
        AnimatorSet animOut = new AnimatorSet();
        animOut.play(moveOut).with(fadeOut);
        animOut.setDuration(1000);
        animOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canrefresh=true;
                super.onAnimationEnd(animation);
            }
        });
        if (animSet.isRunning()) {
            return;
        } else {
            animOut.start();
            animSet = animOut;
        }

    }

    private PtrDefaultHandler ptrDefultHandler = new PtrDefaultHandler() {
        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    page = 1;
                    //                    mPtrClassicFrameLayout.setLoadMoreEnable(true);
                    initData(page, true);

                }
            }, 2000);
        }
    };
    private OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void loadMore() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    page++;
                    if (page <= 20) {
                        initData(page, false);
                    } else {
                        Toast.makeText(getActivity(), "数据已全部加载完毕，没有更多数据了", Toast.LENGTH_LONG).show();
                        mPtrClassicFrameLayout.loadMoreComplete(true);//下拉加载完毕
                        mPtrClassicFrameLayout.setNoMoreData();
                        //                        mPtrClassicFrameLayout.setLoadMoreEnable(false);
                    }
                }
            }, 2000);
        }
    };
    private ICarousePagerSelectView carousePagerSelectView = new ICarousePagerSelectView() {
        @Override
        public void carouseSelect(int index) {
            Toast.makeText(getActivity(), carousePageStrs.get(index), Toast.LENGTH_SHORT).show();
            openDetils(index,carouseTitles);
        }
    };
    private void openDetils(int index,List<Integer> secarouseTitles){
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        //采用Intent普通传值的方式
        ArrayList<String> bundlestr = new ArrayList<String>();
        switch (index) {
            case 0:
                bundlestr.add(list.get(secarouseTitles.get(0)).getTitle());
                bundlestr.add(list.get(secarouseTitles.get(0)).getLink());
                break;
            case 1:
                bundlestr.add(list.get(secarouseTitles.get(1)).getTitle());
                bundlestr.add(list.get(secarouseTitles.get(1)).getLink());
                break;
            case 2:
                bundlestr.add(list.get(secarouseTitles.get(2)).getTitle());
                bundlestr.add(list.get(secarouseTitles.get(2)).getLink());
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("link", bundlestr);
        intent.putExtras(bundle);
        //跳转Activity
        startActivity(intent);
    }
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.sendEmptyMessage(CAROUSEL_TIMR);
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CAROUSEL_TIMR:
                    if (currentIndex >= tips.length - 1) {//已经滚动到最后,从第一页开始
                        mViewPager.setCurrentItem(0);
                    } else {//开始下一页
                        mViewPager.setCurrentItem(currentIndex + 1);
                    }
                    break;
            }
        }
    };

    //初始化
    private View initCarouselHead(List<View> mmViews) {

        headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_selected_header, mRecyclerView, false);
        tvContent = (TextView) headView.findViewById(R.id.tv_content);


        mViewPager = (ViewPager) headView.findViewById(R.id.viewpager);
        mSelectedPagerAdapter = new SelectedPagerAdapter(getActivity(), carousePagerSelectView, mmViews);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        mViewPager.setAdapter(mSelectedPagerAdapter);

        ViewGroup group = (ViewGroup) headView.findViewById(R.id.viewGroup);// 初始化底部显示控件
        tips = new ImageView[3];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            if (i == 0) {
                imageView.setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                imageView.setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }

            tips[i] = imageView;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;// 设置点点点view的左边距
            layoutParams.rightMargin = 10;// 设置点点点view的右边距
            group.addView(imageView, layoutParams);
        }

        mTimer = new Timer(true);//初始化计时器
        mTimer.schedule(task, 0, CAROUSEL_TIMR);//延时0ms后执行，3000ms执行一次

        return headView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //            tvContent.setText(carousePageStr[position]);
            tvContent.setText(carousePageStrs.get(position));
            setImageBackground(position);
            currentIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 改变点点点的切换效果
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (selectItems == i) {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
        }

    }

    public void setPullRefresh(boolean pullRefresh) {
        if (pullRefresh&&canrefresh) {
            mPtrClassicFrameLayout.setPtrHandler(ptrDefultHandler);
        } else {
            PtrHandler ptrHandler = new PtrHandler() {
                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {
                    return false;
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {

                }
            };
            mPtrClassicFrameLayout.setPtrHandler(ptrHandler);
        }


    }

    private void initView() {
        dynamicAddView(fab1,"fabColorNormal",R.color.main_color);
        dynamicAddView(fab1,"fabColorPressed",R.color.launcher_item_select);
        fab1.hide(false);
        //设置为小图
        //fab.setType(FloatingActionButton.TYPE_MINI);或布局中：fab:fab_type="mini"
        fab1.attachToRecyclerView(mRecyclerView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fab1.hide();
            }

            @Override
            public void onScrollUp() {
                fab1.show();
            }
        }, new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition > 5) {
                        fab1.show();
                    } else {
                        fab1.hide();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void initListener() {
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                UIUtils.MoveToPosition(manager, 0);
                UIUtils.MoveToPosition(new LinearLayoutManager(getActivity()), mRecyclerView, 0);

                fab1.hide();

            }
        });
    }

}
