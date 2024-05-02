package com.mavalore.tricenari.helper

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import com.mavalore.tricenari.domain.interfaces.AllInterface
import com.mavalore.tricenari.utils.Const

class AlertDialogBox() {


    fun showNoInternetDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("No Internet Connection")
        alertDialogBuilder.setMessage("Please check your internet connection and try again.")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Retry") { dialog, _ ->
            // Perform the retry action here
            // You can add your retry logic here
            dialog.dismiss()

        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            // Close the app or take appropriate action on cancel
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun showDeleteAccountWarning(
        context: Context, navController: NavController, view: View
    ) {

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Delete Account")
        alertDialogBuilder.setMessage("All your saved data will be delete. Do you want to continue?")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Yes") { sl, _ ->
            //Delete user account

        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            // Close the app or take appropriate action on cancel
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    fun showLogoutWarningDialog(
        context: Context, view: View
    ) {

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Do your really want to logout?")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Yes") { sl, _ ->
            //Delete user account
            Snackbar.make(view,"Logout successfully", Snackbar.LENGTH_LONG).show()
            sl.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            // Close the app or take appropriate action on cancel
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}