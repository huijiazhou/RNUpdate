package com.update.jsbundle.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by zhj on 2018/7/20.
 */

public interface DownloadService {


    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);


}
