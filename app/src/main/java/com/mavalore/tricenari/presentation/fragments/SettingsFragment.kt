package com.mavalore.tricenari.presentation.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.PendingIntentCompat.Flags
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentSettingsBinding
import com.mavalore.tricenari.domain.interfaces.AllInterface
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.presentation.activity.MainActivity
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Const
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection
    @Inject
    lateinit var alertDialogBox: AlertDialogBox


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        val userImageUrl = Const.fireBaseAuth().currentUser?.photoUrl
        Glide.with(requireContext())
            .load(userImageUrl)
            .into(binding.circleImageView3)

        binding.textView13.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        }

        viewModel.initSharedPreferences(requireContext())

        binding.logoutOption.setOnClickListener{
          showLogoutDialog()
            viewModel.clearSharedPreferences()
        }

        return binding.root
    }

    private fun handleLogout(){
        Const.fireBaseAuth().signOut()
//        if (checkInternetConnection.hasInternetConnection(requireContext())){
            if (Const.fireBaseAuth().currentUser == null){
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                }
//        }else{
//            showNoInternetDialog(requireContext())
//        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun showNoInternetDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("No Internet Connection")
        alertDialogBuilder.setMessage("Please check your internet connection and try again.")
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Retry") { dialog, _ ->
            // Perform the retry action here
            // You can add your retry logic here
            handleLogout()
            dialog.dismiss()

        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            // Close the app or take appropriate action on cancel
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showLogoutDialog(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Set a transparent background for the dialog window
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_logout)
        dialog.setCancelable(true)

        dialog.show()

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val cancel = dialog.findViewById<TextView>(R.id.btnCancel)
        val logOut = dialog.findViewById<TextView>(R.id.btnLogout)

        cancel.setOnClickListener{
            dialog.dismiss()
        }

        logOut.setOnClickListener{
            viewModel.clearSharedPreferences()
           handleLogout()
        }
    }

}