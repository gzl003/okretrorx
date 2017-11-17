package com.lzg.okretrorx.http;

import com.lzg.okretrorx.bean.UserInfo;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 *  * Created by 智光 on 2017/11/17 12:06
 *  
 */

public interface IApi {


    @GET("users")//不带参数get请求
    Call<List<UserInfo>> getUsers();

    @GET("users/{groupId}")//动态路径get请求
    Call<List<UserInfo>> getUsers(@Path("userId") String userId);

    @GET("users/{groupId}")//get请求 拼接参数 @Query使用
    Call<List<UserInfo>> getUsers(@Path("userId") String userId, @Query("age")int age);

    @GET("users/{groupId}")//get请求 拼接参数 @QueryMap使用
    Call<List<UserInfo>> getUsers(@Path("userId") String userId, @QueryMap HashMap<String, String> paramsMap);




    @POST("add")//直接把对象通过ConverterFactory转化成对应的参数
    Call<List<UserInfo>> addUser(@Body UserInfo user);//post请求 @body使用

    @POST("login")
    @FormUrlEncoded
        //读参数进行urlEncoded
    Call<UserInfo> login(@Field("userId") String username, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded//读参数进行urlEncoded
    Call<UserInfo> login(@FieldMap HashMap<String, String> paramsMap);//post请求 @FormUrlEncoded,@FieldMap使用

    @Multipart
    @POST("login")
    Call<UserInfo> login2(@Part("userId") String userId, @Part("password") String password);//post请求 @Multipart,@Part使用




    @Headers("Cache-Control: max-age=640000")
    @GET("users")//不带参数get请求
    Call<List<UserInfo>> getUsersC();//Cache-Control缓存控制









    @POST("system/login")
    Observable<UserInfo> systemLogin(@Body String userId, @Body String password);
}
