package com.mavalore.tricenari.data.repository

import com.mavalore.tricenari.data.api.TriceNariApi
import com.mavalore.tricenari.domain.interfaces.Repository
import com.mavalore.tricenari.domain.models.AllArticleResponse
import com.mavalore.tricenari.domain.models.SingleArticleResponse
import com.mavalore.tricenari.domain.models.SingleSuperWomenResponse
import com.mavalore.tricenari.domain.models.SuperWomenResponse
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val triceNariApi: TriceNariApi
):Repository {
    override suspend fun getAllArticles(): Response<AllArticleResponse> = triceNariApi.getArticleList()
    override suspend fun getArticleById(articleId: Int): Response<SingleArticleResponse> =
        triceNariApi.getArticleById(articleId)

    override suspend fun getSuperWomen(): Response<SuperWomenResponse> = triceNariApi.getAllSuperWomen()

    override suspend fun getSuperWomenById(womenId: Int): Response<SingleSuperWomenResponse> =
        triceNariApi.getSuperWomenById(womenId)
}