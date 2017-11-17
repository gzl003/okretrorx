package com.lzg.okretrorx.application;

import android.app.Application;

import com.lzg.okretrorx.http.IApi;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 *  * Created by 智光 on 2017/11/17 11:55
 *  
 */
public class IApplication extends Application {


    private static IApplication instance;

    private Retrofit retrofit;
    private OkHttpClient mOkHttpClient;

    public static IApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initOkhttp();
        initRetrofit();
    }

    private void initOkhttp() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS);//设置写入超时时间
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(getInstance().getCacheDir(), cacheSize);
        builder.cache(cache);
//        builder.addInterceptor(new LoggerInterceptor("API_http"));
        mOkHttpClient = builder.build();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl("BASE_URL")
                .addConverterFactory(FastJsonConverterFactory.create())//用来统一解析ResponseBody返回数据的。
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//通过addCallAdapterFactory来添加对RxJava的支持
                .client(mOkHttpClient)
                .build();
    }

    /**
     * 初始化Api
     */
    public IApi initIApi() {

        return retrofit.create(IApi.class);
    }

}
