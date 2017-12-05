package com.example.administrator.xinglv.tools;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by lx on 2017/5/22.
 */
public interface GitHubApi {
    @FormUrlEncoded
    @POST("addPulse.do")
    Call<ResponseBody> postStringFlyRoute(@FieldMap Map<String,String> params);//传入的参数为RequestBody
}
