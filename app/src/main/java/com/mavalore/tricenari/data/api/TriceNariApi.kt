package com.mavalore.tricenari.data.api

import com.mavalore.tricenari.domain.models.AllArticleResponse
import com.mavalore.tricenari.domain.models.SingleArticleResponse
import com.mavalore.tricenari.domain.models.SingleSuperWomenResponse
import com.mavalore.tricenari.domain.models.SuperWomenResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TriceNariApi {

    @GET("/api/list_of_articles/999/5")
    suspend fun getArticleList(): Response<AllArticleResponse>
    @GET("/api/get_article_details/{articleId}/1")
    suspend fun getArticleById(
        @Path("articleId") articleId:Int
    ): Response<SingleArticleResponse>

    @GET("/api/superwomen_list/1/1")
    suspend fun getAllSuperWomen():Response<SuperWomenResponse>

    @GET("/api/get_sw_details/{womenId}/2")
    suspend fun getSuperWomenById(
        @Path("womenId") womenId:Int
    ): Response<SingleSuperWomenResponse>

}