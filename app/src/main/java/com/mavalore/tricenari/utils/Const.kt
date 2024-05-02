package com.mavalore.tricenari.utils

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

object Const {
    const val BASE_URL = "https://tricenari.com/"

    var dashboardAdIsVisible = true
    var superWomenAdIsVisible = true
    var articleAdIsVisible = true
    private var auth = Firebase.auth

    fun fireBaseAuth():FirebaseAuth{
        auth = FirebaseAuth.getInstance()
        return auth
    }


}