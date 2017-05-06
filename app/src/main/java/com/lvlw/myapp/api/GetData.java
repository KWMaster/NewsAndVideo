package com.lvlw.myapp.api;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.lvlw.myapp.adapter.NewsAdapter;
import com.lvlw.myapp.entity.NewsData;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.rawn_hwang.library.widgit.DefaultLoadingLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Wantrer on 2017/3/28 0028.
 */

public class GetData {
    private NewsAdapter newsAdapter;
    private List<String> imageViewstr;
    private ArrayList<NewsData.ShowapiResBodyBean.PagebeanBean.ContentlistBean> list;
    private List<View> mViews;
    private List<String> carousePageStrs;
    private Context context;
    private RecyclerAdapterWithHF recyclerAdapterWithHF;
    private PtrClassicFrameLayout ptrClassicFrameLayout;
    private DefaultLoadingLayout loadingLayout;
    private Banner banner;
    private int page=1;

    public GetData(NewsAdapter newsAdapter, List<String> imageViewstr, ArrayList<NewsData.ShowapiResBodyBean.PagebeanBean.ContentlistBean> list, List<String> carousePageStrs, Context context, RecyclerAdapterWithHF recyclerAdapterWithHF, PtrClassicFrameLayout ptrClassicFrameLayout, DefaultLoadingLayout loadingLayout, int page) {
//        this.banner=banner;
        this.newsAdapter = newsAdapter;
        this.imageViewstr = imageViewstr;
        this.list = list;
        this.carousePageStrs = carousePageStrs;
        this.context = context;
        this.recyclerAdapterWithHF = recyclerAdapterWithHF;
        this.ptrClassicFrameLayout = ptrClassicFrameLayout;
        this.loadingLayout = loadingLayout;
        this.page = page;
    }

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
            public void onResponse(Call<NewsData> call, retrofit2.Response<NewsData> response) {
                NewsData newsData = response.body();
                if (newsData != null) {
                    if (refreshheader) {
                        if (newsAdapter!=null&&newsAdapter.getList()!=null){
                            newsAdapter.getList().clear();
                        }
                        list.addAll(newsData.getShowapi_res_body().getPagebean().getContentlist());
                        imageViewstr = new ArrayList<String>();
                        for (int i = 5; i < list.size(); i++) {
                            if (list.get(i).isHavePic()) {
                                imageViewstr.add(list.get(i).getImageurls().get(0).getUrl());
                                carousePageStrs.add(list.get(i).getTitle());
                                i += 2;
                            }
                            if (imageViewstr.size() == 3) {
                                break;
                            }
                        }
//                        banner.setImages(imageViewstr);
//                        banner.setBannerTitles(carousePageStrs);
                        if (recyclerAdapterWithHF!=null){
                            recyclerAdapterWithHF.notifyDataSetChanged();
                        }
                        ptrClassicFrameLayout.refreshComplete();
                        loadingLayout.onDone();
                    } else {
                        list.addAll(newsData.getShowapi_res_body().getPagebean().getContentlist());
                        if (recyclerAdapterWithHF!=null) {
                            recyclerAdapterWithHF.notifyDataSetChanged();
                        }
                        ptrClassicFrameLayout.loadMoreComplete(true);//下拉加载完毕
                        Toast.makeText(context, "更新了20条新闻", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsData> call, Throwable t) {
                if (refreshheader){
                    ptrClassicFrameLayout.loadMoreComplete(true);//下拉加载完毕
                    loadingLayout.onError();
                    loadingLayout.setErrorButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadingLayout.onLoading();
                            initData(pages, true);
                        }
                    });
                }else {
                    ptrClassicFrameLayout.loadMoreComplete(true);
                    page--;
                    Toast.makeText(context, "加载更多新闻失败", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
