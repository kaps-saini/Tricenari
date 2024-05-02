package com.mavalore.tricenari.data.api

import com.mavalore.tricenari.domain.models.article.AllArticleResponse
import com.mavalore.tricenari.domain.models.article.NextArticleInfo
import com.mavalore.tricenari.domain.models.article.SingleArticleResponse
import com.mavalore.tricenari.domain.models.superwomen.SingleSuperWomenResponse
import com.mavalore.tricenari.domain.models.superwomen.SuperWomenInfo
import com.mavalore.tricenari.domain.models.superwomen.SuperWomenResponse
import com.mavalore.tricenari.domain.models.user.AddUserResponse
import com.mavalore.tricenari.domain.models.user.AuthUserResponse
import com.mavalore.tricenari.domain.models.user.UpdateUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TriceNariApi {

    @GET("/api/list_of_articles/999/5")
    suspend fun getArticleList(): Response<AllArticleResponse>
    @GET("/api/get_article_details/{articleId}/1")
    suspend fun getArticleById(
        @Path("articleId") articleId:Int
    ): Response<SingleArticleResponse>

    @GET("/api/get_article_info_by_id/{articleId}/1")
    suspend fun getNextArticleInfoById(
        @Path("articleId") articleId:Int
    ): Response<NextArticleInfo>

    @GET("/api/superwomen_list/1/1")
    suspend fun getAllSuperWomen():Response<SuperWomenResponse>

    @GET("/api/get_sw_details/{womenId}/2")
    suspend fun getSuperWomenById(
        @Path("womenId") womenId:Int
    ): Response<SingleSuperWomenResponse>


     @GET("/api/get_sw_info/{womenId}/2")
    suspend fun getSuperWomenInfoById(
        @Path("womenId") womenId:Int
    ): Response<SuperWomenInfo>


    @POST("/api/nari_signup/{params}/5")
    suspend fun addUser(
        @Path("params") params:String
    ):Response<AddUserResponse>

    @GET("/api/nari_signin/{loginId}/{password}")
    suspend fun authoriseUser(
        @Path("loginId") loginId: String,
        @Path("password") password: String,
    ):Response<AuthUserResponse>

    @GET("/api/nari_update/{params}/{userId}")
    suspend fun updateUserData(
        @Path("params") params: String,
        @Path("userId") userId: Int
    ):Response<UpdateUserResponse>

    @GET("/api/get_otp_by_email/{emailId}/5")
    suspend fun requestOtp(
        @Path("emailId") email:String
    ):Response<AddUserResponse>




}