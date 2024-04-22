package com.mavalore.tricenari.domain.interfaces

import com.mavalore.tricenari.domain.models.AllArticleResponse
import com.mavalore.tricenari.domain.models.SingleArticleResponse
import com.mavalore.tricenari.domain.models.SingleSuperWomenData
import com.mavalore.tricenari.domain.models.SingleSuperWomenResponse
import com.mavalore.tricenari.domain.models.SuperWomenResponse
import retrofit2.Response

interface Repository {

    suspend fun getAllArticles():Response<AllArticleResponse>

    suspend fun getArticleById(articleId:Int):Response<SingleArticleResponse>

    suspend fun getSuperWomen():Response<SuperWomenResponse>

    suspend fun getSuperWomenById(womenId:Int):Response<SingleSuperWomenResponse>
}