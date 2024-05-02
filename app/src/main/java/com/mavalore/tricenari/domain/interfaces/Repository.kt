package com.mavalore.tricenari.domain.interfaces

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

interface Repository {

    suspend fun getAllArticles():Response<AllArticleResponse>

    suspend fun getArticleById(articleId:Int):Response<SingleArticleResponse>

    suspend fun getNextArticleInfo(articleId: Int):Response<NextArticleInfo>

    suspend fun getSuperWomen():Response<SuperWomenResponse>

    suspend fun getSuperWomenById(womenId:Int):Response<SingleSuperWomenResponse>

    suspend fun getSWInfo(womenId: Int):Response<SuperWomenInfo>

    suspend fun addNewUser(params:String):Response<AddUserResponse>

    suspend fun updateUser(params:String,userId:Int):Response<UpdateUserResponse>

    suspend fun authUser(loginId:String,password:String):Response<AuthUserResponse>

    suspend fun requestOtp(emailId:String):Response<AddUserResponse>

}