package com.nightfarmer.coder.service

import com.nightfarmer.coder.bean.ProjectInfo
import com.nightfarmer.coder.bean.ProjectDepot
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url
import rx.Observable

/**
 * Created by zhangfan on 16-8-26.
 */
interface AppInfoService {

    @GET("users/{hehe}/repos")
    fun xxx(@Path("hehe") hehe: String): Observable<List<ProjectInfo>>
//    Call<List<Repo>> listRepos(@Path("user") String user);
//    Call<List<Appinfo>> getUsers(@)

    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String): Call<List<ProjectInfo>>

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): Observable<ResponseBody>

    companion object {
        val HOST = "https://nightfarmer.github.io/apkDepot/"
    }

    @GET("project.json")
    fun getAllProject(): Observable<ProjectDepot>
}