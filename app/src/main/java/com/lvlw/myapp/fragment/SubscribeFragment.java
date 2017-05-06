package com.lvlw.myapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.lvlw.myapp.R;
import com.lvlw.myapp.activity.NewsActivity;
import com.lvlw.myapp.adapter.NewsAdapter;
import com.lvlw.myapp.api.AppConfigs;
import com.lvlw.myapp.api.GetData;
import com.lvlw.myapp.api.GetDataService;
import com.lvlw.myapp.entity.NewsData;
import com.lvlw.myapp.utils.FrescoImageLoader;
import com.lvlw.myapp.utils.GetRandomListInt;
import com.lvlw.myapp.utils.ItemClickSupport;
import com.lvlw.myapp.utils.UIUtils;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.rawn_hwang.library.widgit.DefaultLoadingLayout;
import me.rawn_hwang.library.widgit.SmartLoadingLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import solid.ren.skinlibrary.base.SkinBaseFragment;

/**
 * Created by w9859 on 2017/3/11.
 */

public class SubscribeFragment extends SkinBaseFragment {
    @BindView(R.id.subscribe_recycler_view)
    RecyclerView subscribeRecyclerView;
    @BindView(R.id.subscribe_recycler_view_frame)
    PtrClassicFrameLayout subscribeRecyclerViewFrame;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.sub_retitle)
    TextView subRetitle;
    @BindView(R.id.fab2)
    FloatingActionButton fab2;

    private float curTranslationY;
    private AnimatorSet animSet;
    private GetData getData;
    private FrescoImageLoader frescoImageLoader;
    private NewsAdapter newsAdapter;
    private List<String> imageViewstr;
    private ArrayList<NewsData.ShowapiResBodyBean.PagebeanBean.ContentlistBean> list;
    private List<View> mViews;
    private List<String> carousePageStrs;
    private Context context;
    private RecyclerAdapterWithHF recyclerAdapterWithHF;
    private DefaultLoadingLayout loadingLayout;
    private Handler handler;
    private int page = 1;
    private String picture="http://mat1.gtimg.com/news/2013pic/picLogo.png";
    private boolean canrefresh=true;


    public void initData(final int pages, final boolean refreshheader) {
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
        map.put(AppConfigs.CHANNELID_NAME, AppConfigs.CHANNELID_GAME);
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
            public void onResponse(Call<NewsData> call, Response<NewsData> response) {
                NewsData newsData = response.body();
                if (newsData != null) {
                    if (refreshheader) {
                        newsAdapter.getList().clear();
                        list.addAll(newsData.getShowapi_res_body().getPagebean().getContentlist());
                        imageViewstr = new ArrayList<String>();
                        carousePageStrs = new ArrayList<String>();
                        List<Integer> carouseTitles=new ArrayList<Integer>();
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
                        initBanner(carouseTitles);

                        recyclerAdapterWithHF.notifyDataSetChanged();
                        subscribeRecyclerViewFrame.refreshComplete();
                        loadingLayout.onDone();
                        subRetitle.setVisibility(View.VISIBLE);
                        refreshIn();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshOut();
//                                subRetitle.setVisibility(View.GONE);
                            }
                        }, 2000);
                    } else {
                        list.addAll(newsData.getShowapi_res_body().getPagebean().getContentlist());
                        recyclerAdapterWithHF.notifyDataSetChanged();
                        subscribeRecyclerViewFrame.loadMoreComplete(true);//下拉加载完毕
                        Toast.makeText(getActivity(), "更新了20条新闻", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsData> call, Throwable t) {
                if (refreshheader) {
                    subscribeRecyclerViewFrame.loadMoreComplete(true);//下拉加载完毕
                    loadingLayout.onError();
                    loadingLayout.setErrorButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadingLayout.onLoading();
                            initData(pages, true);
                        }
                    });
                } else {
                    subscribeRecyclerViewFrame.loadMoreComplete(true);
                    page--;
                    Toast.makeText(getActivity(), "加载更多新闻失败", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_subscribe, null);
        ButterKnife.bind(this, rootView);
        handler = new Handler();
        subscribeRecyclerView.setHasFixedSize(true);
        subscribeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadingLayout = SmartLoadingLayout.createDefaultLayout(getActivity(), subscribeRecyclerViewFrame);

        //        initBanner();

        list = new ArrayList<>();
        //        getData=new GetData(newsAdapter,imageViewstr,list,carousePageStrs,getActivity(),recyclerAdapterWithHF,subscribeRecyclerViewFrame,loadingLayout,page);
        //        getData.initData(1,true);

        loadingLayout.onLoading();
        initData(1, true);
        newsAdapter = new NewsAdapter(getActivity(), list);
        recyclerAdapterWithHF = new RecyclerAdapterWithHF(newsAdapter);
        recyclerAdapterWithHF.addHeader(banner);
        subscribeRecyclerView.setAdapter(recyclerAdapterWithHF);
        initView();
        initListener();

        //        init();
        //        mPtrClassicFrameLayout.refreshComplete();
        subscribeRecyclerViewFrame.setPtrHandler(ptrDefultHandler);//设置下拉监听
        subscribeRecyclerViewFrame.setOnLoadMoreListener(onLoadMoreListener);//设置上拉监听
        subscribeRecyclerViewFrame.setLoadMoreEnable(true);//设置可以加载更多
        ItemClickSupport.addTo(subscribeRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
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
        curTranslationY = subRetitle.getTranslationY();
        Animator moveOut1 = ObjectAnimator.ofFloat(subRetitle, "translationY", curTranslationY, -70f);
        moveOut1.setDuration(1);
        moveOut1.start();
        animSet = new AnimatorSet();
        return rootView;
    }

    public void refreshIn() {
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(subRetitle, "translationY", -70f, curTranslationY);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(subRetitle, "alpha", 0f, 1f);
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
        ObjectAnimator moveOut = ObjectAnimator.ofFloat(subRetitle, "translationY", curTranslationY, -70f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(subRetitle, "alpha", 1f, 0f);
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

    private void initBanner(final List<Integer> sucarouseTitles) {
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        frescoImageLoader = new FrescoImageLoader();
        banner.setImageLoader(frescoImageLoader);
        //        banner.setImages(imageViewstr);
        //        banner.setBannerTitles(carousePageStrs);
        banner.update(imageViewstr, carousePageStrs);
        banner.setBannerAnimation(Transformer.DepthPage);
        banner.isAutoPlay(true);
        banner.setDelayTime(3000);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.start();
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                //采用Intent普通传值的方式
                ArrayList<String> bundlestr = new ArrayList<String>();
                switch (position) {
                    case 0:
                        bundlestr.add(list.get(sucarouseTitles.get(0)).getTitle());
                        bundlestr.add(list.get(sucarouseTitles.get(0)).getLink());
                        break;
                    case 1:
                        bundlestr.add(list.get(sucarouseTitles.get(1)).getTitle());
                        bundlestr.add(list.get(sucarouseTitles.get(1)).getLink());
                        break;
                    case 2:
                        bundlestr.add(list.get(sucarouseTitles.get(2)).getTitle());
                        bundlestr.add(list.get(sucarouseTitles.get(2)).getLink());
                        break;
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("link", bundlestr);
                intent.putExtras(bundle);
                //跳转Activity
                startActivity(intent);
            }
        });
        banner.setVisibility(View.VISIBLE);

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
                        subscribeRecyclerViewFrame.loadMoreComplete(true);//下拉加载完毕
                        subscribeRecyclerViewFrame.setNoMoreData();
                        //                        mPtrClassicFrameLayout.setLoadMoreEnable(false);
                    }
                }
            }, 2000);
        }
    };

    public void setPullRefresh(boolean pullRefresh) {
        if (pullRefresh&&canrefresh) {
            subscribeRecyclerViewFrame.setPtrHandler(ptrDefultHandler);
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
            subscribeRecyclerViewFrame.setPtrHandler(ptrHandler);
        }


    }
    private void initView() {
        dynamicAddView(fab2,"fabColorNormal",R.color.main_color);
        dynamicAddView(fab2,"fabColorPressed",R.color.launcher_item_select);
        fab2.hide(false);
        //设置为小图
        //fab.setType(FloatingActionButton.TYPE_MINI);或布局中：fab:fab_type="mini"
        fab2.attachToRecyclerView(subscribeRecyclerView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fab2.hide();
            }

            @Override
            public void onScrollUp() {
                fab2.show();
            }
        }, new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = subscribeRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition > 5) {
                        fab2.show();
                    } else {
                        fab2.hide();
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
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                UIUtils.MoveToPosition(manager, 0);
                UIUtils.MoveToPosition(new LinearLayoutManager(getActivity()), subscribeRecyclerView, 0);

                fab2.hide();

            }
        });
    }
}
