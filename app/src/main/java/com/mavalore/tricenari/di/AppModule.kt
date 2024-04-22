package com.mavalore.tricenari.di

import android.app.Application
import android.content.Context
import com.google.gson.GsonBuilder
import com.mavalore.tricenari.data.api.TriceNariApi
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.helper.SpinManager
import com.mavalore.tricenari.utils.Const.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance():TriceNariApi{
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return  Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TriceNariApi::class.java)
    }

    @Provides
    @Singleton
    fun networkInstance(): CheckInternetConnection {
        return CheckInternetConnection()
    }

    @Provides
    @Singleton
    fun alertDialogInstance(): AlertDialogBox {
        return AlertDialogBox()
    }

    @Provides
    @Singleton
    fun spinManagerInstance(context:Context):SpinManager{
        return SpinManager(context)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }


}