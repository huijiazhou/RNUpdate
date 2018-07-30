package com.update.jsbundle.service;

import com.update.jsbundle.bean.CheckBack;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by zhj on 2018/7/20.
 */

public interface CheckService {
//    https://s111h.com/adp/check
    //        服务的地址：https://mx.vincent78.com:8083/adp/

    @POST("adp/check")
    @FormUrlEncoded
    Call<CheckBack> checkVersion(@FieldMap Map<String, Object> map);

}
