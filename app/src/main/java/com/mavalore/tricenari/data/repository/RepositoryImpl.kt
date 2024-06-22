package com.mavalore.tricenari.data.repository

import com.mavalore.tricenari.data.api.TriceNariApi
import com.mavalore.tricenari.domain.interfaces.Repository
import com.mavalore.tricenari.domain.models.article.AllArticleResponse
import com.mavalore.tricenari.domain.models.article.NextArticleInfo
import com.mavalore.tricenari.domain.models.article.SingleArticleResponse
import com.mavalore.tricenari.domain.models.contactUs.ContactUsResponse
import com.mavalore.tricenari.domain.models.dynamicValues.DynamicValuesResponse
import com.mavalore.tricenari.domain.models.event.EventDetailsResponse
import com.mavalore.tricenari.domain.models.event.EventInfoResponse
import com.mavalore.tricenari.domain.models.productRecomendation.ProductRecommendationResponse
import com.mavalore.tricenari.domain.models.superwomen.SingleSuperWomenResponse
import com.mavalore.tricenari.domain.models.superwomen.SuperWomenInfo
import com.mavalore.tricenari.domain.models.superwomen.SuperWomenResponse
import com.mavalore.tricenari.domain.models.user.AddUserResponse
import com.mavalore.tricenari.domain.models.user.AuthUserResponse
import com.mavalore.tricenari.domain.models.user.UpdateUserResponse
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val triceNariApi: TriceNariApi
):Repository {
    override suspend fun getAllArticles(): Response<AllArticleResponse> = triceNariApi.getArticleList()
    override suspend fun getArticleById(articleId: Int): Response<SingleArticleResponse> =
        triceNariApi.getArticleById(articleId)

    override suspend fun getNextArticleInfo(articleId: Int): Response<NextArticleInfo> =
        triceNariApi.getNextArticleInfoById(articleId)

    override suspend fun getSuperWomen(): Response<SuperWomenResponse> = triceNariApi.getAllSuperWomen()

    override suspend fun getSuperWomenById(womenId: Int): Response<SingleSuperWomenResponse> =
        triceNariApi.getSuperWomenById(womenId)

    override suspend fun getSWInfo(womenId: Int): Response<SuperWomenInfo> =
        triceNariApi.getSuperWomenInfoById(womenId)

    override suspend fun addNewUser(params: String): Response<AddUserResponse> =
        triceNariApi.addUser(params)

    override suspend fun updateUser(params: String, userId: Int): Response<UpdateUserResponse> =
        triceNariApi.updateUserData(params,userId)

    override suspend fun authUser(loginId: String, password: String): Response<AuthUserResponse> =
        triceNariApi.authoriseUser(loginId,password)

    override suspend fun requestOtp(emailId: String): Response<AddUserResponse> =
        triceNariApi.requestOtp(emailId)

    override suspend fun getDynamicValues(): Response<DynamicValuesResponse> =
        triceNariApi.getDynamicValues()

    override suspend fun sendContactUsData(params: String): Response<ContactUsResponse> =
        triceNariApi.contactUs(params)

    override suspend fun getProductRecommendationData(): Response<ProductRecommendationResponse> =
        triceNariApi.getProductRecommendationItems()

    override suspend fun getEventInfo(eventId: Int): Response<EventInfoResponse> = triceNariApi.getEventInfo(eventId)

    override suspend fun getEventDetails(eventId: Int): Response<EventDetailsResponse> = triceNariApi.getEventDetails(eventId)
}