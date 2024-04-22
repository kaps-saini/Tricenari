package com.mavalore.tricenari.presentation.vm

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavalore.tricenari.domain.interfaces.Repository
import com.mavalore.tricenari.domain.models.AllArticleResponse
import com.mavalore.tricenari.domain.models.SingleArticleResponse
import com.mavalore.tricenari.domain.models.SingleSuperWomenResponse
import com.mavalore.tricenari.domain.models.SuperWomenResponse
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TriceNariViewModel @Inject constructor(
    private val network:CheckInternetConnection,
    private val repository: Repository,
    val app:Application
): ViewModel () {

    private lateinit var sharedPreferences: SharedPreferences
    private val spinCountKey = "spinCount"

    private val _spinLeft:MutableLiveData<Int> = MutableLiveData()
    val spinLeft:LiveData<Int> get() = _spinLeft

    private var _allArticleResponse:MutableLiveData<Resources<AllArticleResponse>> = MutableLiveData()
    val allArticleResponse:LiveData<Resources<AllArticleResponse>> get() = _allArticleResponse

    private var _singleArticleResponse:MutableLiveData<Resources<SingleArticleResponse>> = MutableLiveData()
    val singleArticleResponse:LiveData<Resources<SingleArticleResponse>> get() = _singleArticleResponse

    private var _superWomenResponse:MutableLiveData<Resources<SuperWomenResponse>> = MutableLiveData()
    val superWomenResponse:LiveData<Resources<SuperWomenResponse>> get() = _superWomenResponse

    private var _singleSuperWomenResponse:MutableLiveData<Resources<SingleSuperWomenResponse>> = MutableLiveData()
    val singleSuperWomenResponse:LiveData<Resources<SingleSuperWomenResponse>>get() = _singleSuperWomenResponse

    private var _nextSingleSuperWomenResponse:MutableLiveData<Resources<SingleSuperWomenResponse>> = MutableLiveData()
    val nextSingleSuperWomenResponse:LiveData<Resources<SingleSuperWomenResponse>>get() = _nextSingleSuperWomenResponse


    // Initialize SharedPreferences in your ViewModel
    fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("SpinPreferences", Context.MODE_PRIVATE)
    }

    fun spin(context: Context) {
        // Decrease spin count by 1
        val spinCount = sharedPreferences.getInt(spinCountKey, 0)
        if (spinCount > 0) {
            sharedPreferences.edit().putInt(spinCountKey, spinCount - 1).apply()
        }else{
            Toast.makeText(context,"No spin left", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to retrieve user login status
    fun getSpinLeft() {
        _spinLeft.value = sharedPreferences.getInt(spinCountKey, 1)
    }

    // Method to clear SharedPreferences
    fun clearSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun removeTags(title: String): String {
        return title
            .replace("<br>", " ")
            .replace("<li>", " ")
            .replace("<b>", " ")
            .replace("<ul>", " ")
            .replace("</li>", " ")
            .replace("</b>", " ")
            .replace("</ul>", " ")
    }

    fun getAllArticle() = viewModelScope.launch {
        handleNetworkSafeAllArticleResponse()
    }

    fun getArticleById(articleId: Int) = viewModelScope.launch {
        handleNetworkSafeSingleArticleResponse(articleId)
    }

    fun getSuperWomen() = viewModelScope.launch {
        handleNetworkSafeSuperWomenResponse()
    }

    fun getWomenDetailsById(womenId: Int) = viewModelScope.launch {
        handleNetworkSafeSingleSuperWomenResponse(womenId)
    }

    fun getNextWomenDetailsById(womenId: Int) = viewModelScope.launch {
        handleNetworkSafeNextSingleSuperWomenResponse(womenId)
    }

    private suspend fun handleNetworkSafeAllArticleResponse(){
        _allArticleResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getAllArticles()
                _allArticleResponse.value = handleAllArticleResponse(response)
            }else{
                _allArticleResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _allArticleResponse.value = Resources.Error("Network failure")
                else -> _allArticleResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleAllArticleResponse(response: Response<AllArticleResponse>): Resources<AllArticleResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeSingleArticleResponse(articleId:Int){
        _singleArticleResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getArticleById(articleId)
                _singleArticleResponse.value = handleSingleArticleResponse(response)
            }else{
                _singleArticleResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _singleArticleResponse.value = Resources.Error("Network failure")
                else -> _singleArticleResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleSingleArticleResponse(response: Response<SingleArticleResponse>): Resources<SingleArticleResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeSuperWomenResponse(){
        _superWomenResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getSuperWomen()
                _superWomenResponse.value = handleSuperWomenResponse(response)
            }else{
                _superWomenResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _superWomenResponse.value = Resources.Error("Network failure")
                else -> _superWomenResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleSuperWomenResponse(response: Response<SuperWomenResponse>): Resources<SuperWomenResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeSingleSuperWomenResponse(womenId:Int){
        _singleSuperWomenResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getSuperWomenById(womenId)
                _singleSuperWomenResponse.value = handleSingleSuperWomenResponse(response)
            }else{
                _singleSuperWomenResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _singleSuperWomenResponse.value = Resources.Error("Network failure")
                else -> _singleSuperWomenResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleSingleSuperWomenResponse(response: Response<SingleSuperWomenResponse>): Resources<SingleSuperWomenResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeNextSingleSuperWomenResponse(womenId:Int){
        _nextSingleSuperWomenResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getSuperWomenById(womenId)
                _nextSingleSuperWomenResponse.value = handleNextSingleSuperWomenResponse(response)
            }else{
                _nextSingleSuperWomenResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _nextSingleSuperWomenResponse.value = Resources.Error("Network failure")
                else -> _nextSingleSuperWomenResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleNextSingleSuperWomenResponse(response: Response<SingleSuperWomenResponse>): Resources<SingleSuperWomenResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }


}