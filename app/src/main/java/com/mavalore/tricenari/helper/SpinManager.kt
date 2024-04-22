package com.mavalore.tricenari.helper

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import java.util.Calendar

class SpinManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SpinPreferences", Context.MODE_PRIVATE)

    private val lastSpinDateKey = "lastSpinDate"
    private val spinCountKey = "spinCount"

    fun canSpin(): Boolean {
        val currentDate = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val lastSpinDate = Calendar.getInstance().apply {
            timeInMillis = sharedPreferences.getLong(lastSpinDateKey, 0)
        }

         //Check if last spin date is not today
        return if (lastSpinDate == currentDate) {
            // Reset spin count to 1
            sharedPreferences.edit().putInt(spinCountKey, 1).apply()
            //  Update last spin date
            sharedPreferences.edit().putLong(lastSpinDateKey, currentDate.timeInMillis).apply()
            true
        }else{
            false
        }

    }


}
