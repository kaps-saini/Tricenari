package com.mavalore.tricenari.presentation.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentSettingsBinding
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
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

        viewModel.initAuthSharedPreferences(requireContext())
        val userData = viewModel.getUserData()
        binding.tvSettingsUserName.text = userData?.name.toString()

        binding.tvProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        }

        binding.tvSignout.setOnClickListener {
            showLogoutDialog()
        }

        binding.tvFeedback.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_contact_us)
        }

        binding.tvAboutUs.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_about_us)
        }


        return binding.root
    }

    private fun handleLogout() {
        if (checkInternetConnection.hasInternetConnection(requireContext())){
            Const.fireBaseAuth().signOut()
            viewModel.saveUserLoginStatus(false)
            findNavController().navigate(R.id.action_settingsFragment_to_signInFragment)
        }else{
           alertDialogBox.showNoInternetDialog(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun showLogoutDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
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

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        logOut.setOnClickListener {
            viewModel.clearAuthSharedPreferences()
            handleLogout()
            dialog.dismiss()
        }
    }

}