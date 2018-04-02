package com.xqlh.heartsmart.api.service;

import com.xqlh.heartsmart.api.bean.EntityBindPhone;
import com.xqlh.heartsmart.api.bean.EntityCheckMessage;
import com.xqlh.heartsmart.api.bean.EntityCheckPhone;
import com.xqlh.heartsmart.api.bean.EntityGetMessage;
import com.xqlh.heartsmart.api.bean.EntityLogin;
import com.xqlh.heartsmart.api.bean.EntityWelcome;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/3/28.
 */

public interface ApiService {
//    http://open.bnuxq.com//api/Account/token?username=xiaomeinv&password=123456

    //登录
    @GET("api/Account/token")
    Observable<EntityLogin> Login(@Query("username") String username, @Query("password") String password);

    //检验手机号码是否已注册
    @GET("api/account/existstel")
    Observable<EntityCheckPhone> CheckPhone(@Query("tel") String tel);

    //获取短信
    @Headers("User-Agent:Mozilla/5.0 (Windows NT 10.0; WOW64) WindowsClient/537.36 (KHTML, like Gecko) Bnuxq.com/56.0.2924.87 Safari/537.36")
    @GET("api/account/sendsms")
    Observable<EntityGetMessage> GetMessage(@Query("tel") String tel);

    //验证短信
    @GET("api/account/checksmscode")
    Observable<EntityCheckMessage> CheckMessage(@Query("token") String token, @Query("telcode") String telcode);
    //绑定手机号
    @POST("api/account/telphone/{Telphone}/{token}/{telcode}")
    Observable<EntityBindPhone> bindPhone(@Path("Telphone") String Telphone, @Path("token") String token, @Path("telcode") String telcode);

    @GET("api/clientapp/apphomepic")
    Observable<EntityWelcome> getWelcome(@Query("appType") int appType);
}
