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
import com.mavalore.tricenari.domain.models.user.UserDetails
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

    private lateinit var loginSharedPreferences: SharedPreferences

    private val _spinLeft:MutableLiveData<Int> = MutableLiveData()
    val spinLeft:LiveData<Int> get() = _spinLeft

    private var _allArticleResponse:MutableLiveData<Resources<AllArticleResponse>> = MutableLiveData()
    val allArticleResponse:LiveData<Resources<AllArticleResponse>> get() = _allArticleResponse

    private var _singleArticleResponse:MutableLiveData<Resources<SingleArticleResponse>> = MutableLiveData()
    val singleArticleResponse:LiveData<Resources<SingleArticleResponse>> get() = _singleArticleResponse

    private var _nextSingleArticleResponse:MutableLiveData<Resources<NextArticleInfo>> = MutableLiveData()
    val nextSingleArticleResponse :LiveData<Resources<NextArticleInfo>> get() = _nextSingleArticleResponse

    private var _superWomenResponse:MutableLiveData<Resources<SuperWomenResponse>> = MutableLiveData()
    val superWomenResponse:LiveData<Resources<SuperWomenResponse>> get() = _superWomenResponse

    private var _singleSuperWomenResponse:MutableLiveData<Resources<SingleSuperWomenResponse>> = MutableLiveData()
    val singleSuperWomenResponse:LiveData<Resources<SingleSuperWomenResponse>>get() = _singleSuperWomenResponse

    private var _nextSuperWomenInfo:MutableLiveData<Resources<SuperWomenInfo>> = MutableLiveData()
    val nextSuperWomenInfo:LiveData<Resources<SuperWomenInfo>>get() = _nextSuperWomenInfo

    private var _addUserResponse:MutableLiveData<Resources<AddUserResponse>> = MutableLiveData()
    val addUserResponse:LiveData<Resources<AddUserResponse>> get() = _addUserResponse

    private var _authUserResponse:MutableLiveData<Resources<AuthUserResponse>> = MutableLiveData()
    val authUserResponse:LiveData<Resources<AuthUserResponse>> get() = _authUserResponse

    private var _updateUserResponse:MutableLiveData<Resources<UpdateUserResponse>> = MutableLiveData()
    val updateUserResponse: LiveData<Resources<UpdateUserResponse>> get() = _updateUserResponse

    private var _otpResponse: MutableLiveData<Resources<AddUserResponse>> = MutableLiveData()
    val otpResponse: LiveData<Resources<AddUserResponse>> get() = _otpResponse

    private var _dynamicValuesResponse: MutableLiveData<Resources<DynamicValuesResponse>> = MutableLiveData()
    val dynamicValuesResponse: LiveData<Resources<DynamicValuesResponse>> get() = _dynamicValuesResponse

    private var _contactUsResponse: MutableLiveData<Resources<ContactUsResponse>> = MutableLiveData()
    val contactUsResponse: LiveData<Resources<ContactUsResponse>> get() = _contactUsResponse

    private var _productRecommendationResponse: MutableLiveData<Resources<ProductRecommendationResponse>> = MutableLiveData()
    val productRecommendationResponse: LiveData<Resources<ProductRecommendationResponse>> get() = _productRecommendationResponse

     private var _eventDetailsResponse: MutableLiveData<Resources<EventDetailsResponse>> = MutableLiveData()
    val eventDetailsResponse: LiveData<Resources<EventDetailsResponse>> get() = _eventDetailsResponse

    private var _eventInfoResponse: MutableLiveData<Resources<EventInfoResponse>> = MutableLiveData()
    val eventInfoResponse: LiveData<Resources<EventInfoResponse>> get() = _eventInfoResponse


    init {
        getSuperWomen()
        getDynamicValues()
        getAllArticle()
    }

    // Initialize SharedPreferences in your ViewModel
    fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("SpinPreferences", Context.MODE_PRIVATE)
    }

    // Initialize SharedPreferences in your ViewModel
    fun initAuthSharedPreferences(context: Context) {
        loginSharedPreferences = context.getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE)
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

    // Method to save user login status
    fun saveUserLoginStatus(isLoggedIn: Boolean) {
        val editor = loginSharedPreferences.edit()
        editor.putBoolean("is_logged_in", isLoggedIn)
        editor.apply()
    }

    fun saveUserLoginDataValue(dataValue: Int) {
        val editor = loginSharedPreferences.edit()
        editor.putInt("user_data_value", dataValue)
        editor.apply()
    }

    // Method to retrieve user login status
    fun getUserLoginStatus(): Boolean {
        return loginSharedPreferences.getBoolean("is_logged_in", false)
    }

    fun saveUserData(
        id: Int? = null,
        name: String? = null,
        emailId: String? = null,
        mobile: String? = null,
        provider: String? = null,
        city: String? = null,
        dob: String? = null,
        gender: String? = null,
        interest: String? = null,
        jewels: Int? = null,
        otpVerified: Int? = null,
        idVerified: Int? = null,
        proceed: Int? = null,
        loggedIn: Boolean? = null
    ) {
        val editor = loginSharedPreferences.edit()

        id?.let { editor.putInt("user_id", it) }
        name?.let { editor.putString("user_name", it) }
        emailId?.let { editor.putString("email_id", it) }
        mobile?.let { editor.putString("mobile", it) }
        provider?.let { editor.putString("provider", it) }
        city?.let { editor.putString("city", it) }
        dob?.let { editor.putString("dob", it) }
        gender?.let { editor.putString("gender", it) }
        interest?.let { editor.putString("interest", it) }
        jewels?.let { editor.putInt("jewels", it) }
        otpVerified?.let { editor.putInt("otp_verified", it) }
        idVerified?.let { editor.putInt("id_verified", it) }
        proceed?.let { editor.putInt("proceed", it) }
        loggedIn?.let { editor.putBoolean("loggedIn", it) }

        editor.apply()
    }

    fun getUserData(): UserDetails {
        val userId = loginSharedPreferences.getInt("user_id",0)
        val userName = loginSharedPreferences.getString("user_name","")
        val emailId = loginSharedPreferences.getString("email_id","")
        val mobile = loginSharedPreferences.getString("mobile","")
        val provider = loginSharedPreferences.getString("provider","")
        val city = loginSharedPreferences.getString("city","")
        val dob = loginSharedPreferences.getString("dob","")
        val gender = loginSharedPreferences.getString("gender","")
        val interest = loginSharedPreferences.getString("interest","")
        val jewels = loginSharedPreferences.getInt("jewels",0)
        val otpVerified = loginSharedPreferences.getInt("otp_verified",0)
        val idVerified = loginSharedPreferences.getInt("id_verified",0)
        val proceed = loginSharedPreferences.getInt("proceed",0)
        val loggedIn = loginSharedPreferences.getBoolean("logged_in",false)
        return UserDetails(
                idVerified,city,dob, emailId!!,gender,userId,interest,jewels,loggedIn, mobile,
                userName!!,otpVerified,proceed, provider.toString()
            )
    }

    // Method to clear SharedPreferences
    fun clearAuthSharedPreferences() {
        val editor = loginSharedPreferences.edit()
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

    private fun getAllArticle() = viewModelScope.launch {
        handleNetworkSafeAllArticleResponse()
    }

    fun getArticleById(articleId: Int) = viewModelScope.launch {
        handleNetworkSafeSingleArticleResponse(articleId)
    }

    fun getNextArticleById(articleId: Int) = viewModelScope.launch {
        handleNetworkSafeNextSingleArticleResponse(articleId)
    }

    fun getSuperWomen() = viewModelScope.launch {
        handleNetworkSafeSuperWomenResponse()
    }

    fun getWomenDetailsById(womenId: Int) = viewModelScope.launch {
        handleNetworkSafeSingleSuperWomenResponse(womenId)
    }

    fun getNextWomenDetailsById(womenId: Int) = viewModelScope.launch {
        handleNetworkSafeNextSuperWomenInfoResponse(womenId)
    }

    fun addNewUser(param:String) = viewModelScope.launch {
        handleNetworkSafeAddUserResponse(param)
    }

    fun authUser(userId:String,password:String) = viewModelScope.launch {
        handleNetworkAuthUserResponse(userId,password)
    }

    fun updateUser(params: String,userId:Int) = viewModelScope.launch {
        handleNetworkUpdateUserResponse(params,userId)
    }

    fun requestOtp(emailId: String) = viewModelScope.launch {
        handleNetworkRequestOtpResponse(emailId)
    }

    private fun getDynamicValues() = viewModelScope.launch {
        handleNetworkSafeDynamicValuesResponse()
    }

    fun sendContactUsData(params: String) = viewModelScope.launch {
        handleNetworkSafeContactUsResponse(params)
    }

    fun getProductRecommendationData() = viewModelScope.launch {
        handleNetworkSafeProductRecommendationResponse()
    }

    fun getEventDetails(eventId:Int) = viewModelScope.launch {
        handleNetworkSafeEventDetailsResponse(eventId)
    }

    fun getEventInfo(eventId:Int) = viewModelScope.launch {
        handleNetworkSafeEventInfoResponse(eventId)
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

    private suspend fun handleNetworkSafeNextSingleArticleResponse(articleId:Int){
        _nextSingleArticleResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getNextArticleInfo(articleId)
                _nextSingleArticleResponse.value = handleNextSingleArticleResponse(response)
            }else{
                _nextSingleArticleResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _nextSingleArticleResponse.value = Resources.Error("Network failure")
                else -> _nextSingleArticleResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleNextSingleArticleResponse(response: Response<NextArticleInfo>): Resources<NextArticleInfo> {
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

    private suspend fun handleNetworkSafeNextSuperWomenInfoResponse(womenId:Int){
        _nextSuperWomenInfo.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getSWInfo(womenId)
                _nextSuperWomenInfo.value = handleNextSingleSuperWomenResponse(response)
            }else{
                _nextSuperWomenInfo.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _nextSuperWomenInfo.value = Resources.Error("Network failure")
                else -> _nextSuperWomenInfo.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleNextSingleSuperWomenResponse(response: Response<SuperWomenInfo>): Resources<SuperWomenInfo> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeAddUserResponse(params: String){
        _addUserResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.addNewUser(params)
                _addUserResponse.value = handleAddUserResponse(response)
            }else{
                _addUserResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _addUserResponse.value = Resources.Error("Network failure")
                else -> _addUserResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleAddUserResponse(response: Response<AddUserResponse>): Resources<AddUserResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

  private suspend fun handleNetworkAuthUserResponse(userId: String,password:String){
        _authUserResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.authUser(userId,password)
                _authUserResponse.value = handleAuthUserResponse(response)
            }else{
                _authUserResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _authUserResponse.value = Resources.Error("Network failure")
                else -> _authUserResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleAuthUserResponse(response: Response<AuthUserResponse>): Resources<AuthUserResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkUpdateUserResponse(params: String,userId: Int){
        _updateUserResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.updateUser(params,userId)
                _updateUserResponse.value = handleUpdateUserResponse(response)
            }else{
                _updateUserResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _updateUserResponse.value = Resources.Error("Network failure")
                else -> _updateUserResponse.value = Resources.Error(t.message.toString())
            }
        }

    }

    private fun handleUpdateUserResponse(response: Response<UpdateUserResponse>): Resources<UpdateUserResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkRequestOtpResponse(emailId: String){
        _otpResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.requestOtp(emailId)
                _otpResponse.value = handleOtpResponse(response)
            }else{
                _otpResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _otpResponse.value = Resources.Error("Network failure")
                else -> _otpResponse.value = Resources.Error(t.message.toString())
            }
        }
    }

    private fun handleOtpResponse(response: Response<AddUserResponse>): Resources<AddUserResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeDynamicValuesResponse(){
        _dynamicValuesResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getDynamicValues()
                _dynamicValuesResponse.value = handleDynamicResponse(response)
            }else{
                _dynamicValuesResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _dynamicValuesResponse.value = Resources.Error("Network failure")
                else -> _dynamicValuesResponse.value = Resources.Error(t.message.toString())
            }
        }
    }

    private fun handleDynamicResponse(response: Response<DynamicValuesResponse>): Resources<DynamicValuesResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeContactUsResponse(params: String){
        _contactUsResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.sendContactUsData(params)
                _contactUsResponse.value = handleContactUsResponse(response)
            }else{
                _contactUsResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _contactUsResponse.value = Resources.Error("Network failure")
                else -> _contactUsResponse.value = Resources.Error(t.message.toString())
            }
        }
    }

    private fun handleContactUsResponse(response: Response<ContactUsResponse>): Resources<ContactUsResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeProductRecommendationResponse(){
        _productRecommendationResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getProductRecommendationData()
                _productRecommendationResponse.value = handleProductRecommendationResponse(response)
            }else{
                _productRecommendationResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _productRecommendationResponse.value = Resources.Error("Network failure")
                else -> _productRecommendationResponse.value = Resources.Error(t.message.toString())
            }
        }
    }

    private fun handleProductRecommendationResponse(response: Response<ProductRecommendationResponse>): Resources<ProductRecommendationResponse> {
        if (response.isSuccessful) {
            response.body()?.let { responseList ->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

     private suspend fun handleNetworkSafeEventDetailsResponse(eventId:Int){
         _eventDetailsResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getEventDetails(eventId)
                _eventDetailsResponse.value = handleEventDetailsResponse(response)
            }else{
                _eventDetailsResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _eventDetailsResponse.value = Resources.Error("Network failure")
                else -> _eventDetailsResponse.value = Resources.Error(t.message.toString())
            }
        }
    }

    private fun handleEventDetailsResponse(response: Response<EventDetailsResponse>): Resources<EventDetailsResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun handleNetworkSafeEventInfoResponse(eventId:Int){
         _eventInfoResponse.value = Resources.Loading()
        try {
            if (network.hasInternetConnection(app)){
                val response = repository.getEventInfo(eventId)
                _eventInfoResponse.value = handleEventInfoResponse(response)
            }else{
                _eventInfoResponse.value = Resources.Error("No Internet")
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> _eventInfoResponse.value = Resources.Error("Network failure")
                else -> _eventInfoResponse.value = Resources.Error(t.message.toString())
            }
        }
    }

    private fun handleEventInfoResponse(response: Response<EventInfoResponse>): Resources<EventInfoResponse> {
        if (response.isSuccessful){
            response.body()?.let {responseList->
                return Resources.Success(responseList)
            }
        }
        return Resources.Error(response.message().toString())
    }

    fun generateEncodedParamsToUpdateUser(
        id: Int? = null,
        name: String? = null,
        email: String? = null,
        loginId: String? = null,
        pwd: String? = null,
        mobile: String? = null,
        provider: String? = null,
        city: String? = null,
        dob: String? = null,
        gender: String? = null,
        interest: String? = null,
        jewels: Int? = null,
        otp: Int? = null,
        otpVerified: Int? = null,
        idVerified: Int? = null,
        proceed: Int? = null,
        lastseen: String? = null
    ): String {
        val mobileNo = mobile?.let { "mobile=$it" }
        val params = listOf(
            id?.let { "id=$it" },
            name?.let { "name=$it" },
            email?.let { "email=$it" },
            loginId?.let { "loginid=$it" },
            pwd?.let { "pwd=$it" },
            mobileNo,
            provider?.let { "provider=$it" },
            city?.let { "city=$it" },
            dob?.let { "dob=$it" },
            gender?.let { "gender=$it" },
            interest?.let { "interests=$it" },
            jewels?.let { "jewels=$it" },
            otp?.let { "otp=$it" },
            otpVerified?.let { "otpVerified=$it" },
            idVerified?.let { "idVerified=$it" },
            proceed?.let { "proceed=$it" },
            lastseen?.let { "lastseen=$it" }
        ).mapNotNull { it } // Filter out null values

        return params.joinToString(separator = ",")
    }

    fun generateEncodedParamsToAddUser(userName: String?, email: String?,loginId:String?,pwd:String?,
                                       mobile: String?,provider:String?): String {
        val params = mutableListOf<String>()
        userName?.let { params.add(it) }
        email?.let { params.add(it) }
        loginId?.let { params.add(it) }
        pwd?.let { params.add(it) }
        mobile?.let { params.add(it) }
        provider?.let { params.add(it) }
        return params.joinToString(separator = ",")
    }

    fun isValidEmail(email: String): Boolean {
        // Define the regular expression pattern for email validation
        val emailRegex = Regex("([a-z0-9_.-]+)@([da-z.-]+)\\.([a-z.]{2,6})")

        // Check if the input email matches the pattern
        return emailRegex.matches(email)
    }

    fun separateName(fullName: String): Pair<String, String> {
        val parts = fullName.trim().split(Regex("\\s+")) // Split by whitespace
        return when (parts.size) {
            1 -> Pair(parts[0], "") // If only one part, assume it's the first name
            2 -> Pair(parts[0], parts[1]) // If two parts, assume the first is first name and second is last name
            else -> Pair(parts[0], parts.drop(1).joinToString(" ")) // Otherwise, first part is first name, rest is last name
        }
    }
}