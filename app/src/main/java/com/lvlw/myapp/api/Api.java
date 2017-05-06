package com.lvlw.myapp.api;

import com.lvlw.myapp.entity.DataInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Wantrer on 2017/3/22 0022.
 */

public interface Api {
    //http://gank.io/api/data/福利/5/1
    @GET("api/data/福利/{pageCount}/{pageIndex}")
    Call<DataInfo> getData(@Path("pageCount") int pageCount,@Path("pageIndex") int pageIndex);
}
