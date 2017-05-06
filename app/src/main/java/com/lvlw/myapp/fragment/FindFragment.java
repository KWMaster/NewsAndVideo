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

public class FindFragment extends SkinBaseFragment {
    @BindView(R.id.find_banner)
    Banner findBanner;
    @BindView(R.id.find_recycler_view)
    RecyclerView findRecyclerView;
    @BindView(R.id.find_recycler_view_frame)
    PtrClassicFrameLayout findRecyclerViewFrame;
    @BindView(R.id.fid_retitle)
    TextView fidRetitle;
    @BindView(R.id.fab3)
    FloatingActionButton fab3;

    private float curTranslationY;
    private AnimatorSet animSet;
    private TextView refreshIndicator;
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
    private boolean canrefresh=true;
    private String picture="http://mat1.gtimg.com/news/2013pic/picLogo.png";


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
        map.put(AppConfigs.CHANNELID_NAME, AppConfigs.CHANNELID_FILM);
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
                        findRecyclerViewFrame.refreshComplete();
                        loadingLayout.onDone();
                        fidRetitle.setVisibility(View.VISIBLE);
                        refreshIn();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshOut();
//                                fidRetitle.setVisibility(View.GONE);
                            }
                        }, 2000);
                    } else {
                        list.addAll(newsData.getShowapi_res_body().getPagebean().getContentlist());
                        recyclerAdapterWithHF.notifyDataSetChanged();
                        findRecyclerViewFrame.loadMoreComplete(true);//下拉加载完毕
                        Toast.makeText(getActivity(), "更新了20条新闻", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsData> call, Throwable t) {
                if (refreshheader) {
                    findRecyclerViewFrame.loadMoreComplete(true);//下拉加载完毕
                    loadingLayout.onError();
                    loadingLayout.setErrorButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadingLayout.onLoading();
                            initData(pages, true);
                        }
                    });
                } else {
                    findRecyclerViewFrame.loadMoreComplete(true);
                    page--;
                    Toast.makeText(getActivity(), "加载更多新闻失败", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_find, null);
        ButterKnife.bind(this, rootView);
        handler = new Handler();
        findRecyclerView.setHasFixedSize(true);
        findRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadingLayout = SmartLoadingLayout.createDefaultLayout(getActivity(), findRecyclerViewFrame);

        //        initBanner();

        list = new ArrayList<>();
        //        getData=new GetData(newsAdapter,imageViewstr,list,carousePageStrs,getActivity(),recyclerAdapterWithHF,subscribeRecyclerViewFrame,loadingLayout,page);
        //        getData.initData(1,true);
        loadingLayout.onLoading();
        initData(1, true);
        newsAdapter = new NewsAdapter(getActivity(), list);
        recyclerAdapterWithHF = new RecyclerAdapterWithHF(newsAdapter);
        recyclerAdapterWithHF.addHeader(findBanner);
        findRecyclerView.setAdapter(recyclerAdapterWithHF);
        initView();
        initListener();

        //        init();
        //        mPtrClassicFrameLayout.refreshComplete();
        findRecyclerViewFrame.setPtrHandler(ptrDefultHandler);//设置下拉监听
        findRecyclerViewFrame.setOnLoadMoreListener(onLoadMoreListener);//设置上拉监听
        findRecyclerViewFrame.setLoadMoreEnable(true);//设置可以加载更多
        ItemClickSupport.addTo(findRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
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
        curTranslationY = fidRetitle.getTranslationY();
        Animator moveOut1 = ObjectAnimator.ofFloat(fidRetitle, "translationY", curTranslationY, -70f);
        moveOut1.setDuration(1);
        moveOut1.start();
        animSet = new AnimatorSet();

        return rootView;
    }

    public void refreshIn() {
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(fidRetitle, "translationY", -70f, curTranslationY);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(fidRetitle, "alpha", 0f, 1f);
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
        ObjectAnimator moveOut = ObjectAnimator.ofFloat(fidRetitle, "translationY", curTranslationY, -70f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fidRetitle, "alpha", 1f, 0f);
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

    private void initBanner(final List<Integer> fcarouseTitles) {
        findBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        frescoImageLoader = new FrescoImageLoader();
        findBanner.setImageLoader(frescoImageLoader);
        //        findBanner.setImages(imageViewstr);
        //        findBanner.setBannerTitles(carousePageStrs);
        findBanner.update(imageViewstr, carousePageStrs);
        findBanner.setBannerAnimation(Transformer.DepthPage);
        findBanner.isAutoPlay(true);
        findBanner.setDelayTime(3000);
        findBanner.setIndicatorGravity(BannerConfig.RIGHT);
        findBanner.start();
        findBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                //采用Intent普通传值的方式
                ArrayList<String> bundlestr = new ArrayList<String>();
                switch (position) {
                    case 0:
                        bundlestr.add(list.get(fcarouseTitles.get(0)).getTitle());
                        bundlestr.add(list.get(fcarouseTitles.get(0)).getLink());
                        break;
                    case 1:
                        bundlestr.add(list.get(fcarouseTitles.get(1)).getTitle());
                        bundlestr.add(list.get(fcarouseTitles.get(1)).getLink());
                        break;
                    case 2:
                        bundlestr.add(list.get(fcarouseTitles.get(2)).getTitle());
                        bundlestr.add(list.get(fcarouseTitles.get(2)).getLink());
                        break;
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("link", bundlestr);
                intent.putExtras(bundle);
                //跳转Activity
                startActivity(intent);
            }
        });
        findBanner.setVisibility(View.VISIBLE);
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
                        findRecyclerViewFrame.loadMoreComplete(true);//下拉加载完毕
                        findRecyclerViewFrame.setNoMoreData();
                        //                        mPtrClassicFrameLayout.setLoadMoreEnable(false);
                    }
                }
            }, 2000);
        }
    };

    public void setPullRefresh(boolean pullRefresh) {
        if (pullRefresh&&canrefresh) {
            findRecyclerViewFrame.setPtrHandler(ptrDefultHandler);
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
            findRecyclerViewFrame.setPtrHandler(ptrHandler);
        }


    }

    private void initView() {
        dynamicAddView(fab3,"fabColorNormal",R.color.main_color);
        dynamicAddView(fab3,"fabColorPressed",R.color.launcher_item_select);
        fab3.hide(false);
        //设置为小图
        //fab.setType(FloatingActionButton.TYPE_MINI);或布局中：fab:fab_type="mini"
        fab3.attachToRecyclerView(findRecyclerView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fab3.hide();
            }

            @Override
            public void onScrollUp() {
                fab3.show();
            }
        }, new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = findRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition > 5) {
                        fab3.show();
                    } else {
                        fab3.hide();
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
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                UIUtils.MoveToPosition(manager, 0);
                UIUtils.MoveToPosition(new LinearLayoutManager(getActivity()), findRecyclerView, 0);

                fab3.hide();

            }
        });
    }
}
