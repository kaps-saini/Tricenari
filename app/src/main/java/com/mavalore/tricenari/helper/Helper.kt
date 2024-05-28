package com.mavalore.tricenari.helper

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mavalore.tricenari.R

object Helper {

    fun btnVisibility(
        context: Context,
        vararg inputs: TextInputEditText?,
        button: Button
    ) {
        val anyInputEmpty = inputs.any { input ->
            input?.text?.toString()?.trim().isNullOrEmpty()
        }

        if (anyInputEmpty) {
            button.alpha = 0.4f
            button.isEnabled = false
            button.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            button.alpha = 1f
            button.isEnabled = true
        }
    }

    fun showSnackbar(view:View,msg:String){
        Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show()
    }


}