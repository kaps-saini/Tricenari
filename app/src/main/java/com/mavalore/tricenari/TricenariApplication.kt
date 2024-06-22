package com.mavalore.tricenari

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import dagger.hilt.android.HiltAndroidApp
import com.facebook.appevents.AppEventsLogger;
@HiltAndroidApp
class TricenariApplication:Application(){
    override fun onCreate() {
        super.onCreate()
        //Initialize Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        AppEventsLogger.activateApp(this)

    }
}