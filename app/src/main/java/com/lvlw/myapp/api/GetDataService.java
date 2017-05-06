package com.lvlw.myapp.api;

import com.lvlw.myapp.entity.NewsData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Wantrer on 2017/3/22 0022.
 */

public interface GetDataService {
    @FormUrlEncoded
    @POST(AppConfigs.URL_DATA)
    Call<NewsData> getBeansData(@FieldMap Map<String, String> map);
}
