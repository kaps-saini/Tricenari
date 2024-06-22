package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentResetPasswordBinding
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.helper.Helper
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResetPassword : Fragment() {

    private var _binding:FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()
    private val args:ResetPasswordArgs by navArgs()
    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_reset_password, container, false)

        btnVisibility()
        viewModel.initAuthSharedPreferences(requireContext())

        binding.etNewPassword.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { btnVisibility() }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.etNewConfirmPassword.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { btnVisibility() }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnResetPassword.setOnClickListener{
            validateInputsAndResetPassword()
        }

        viewModel.updateUserResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {
                    progressBarInvisible()
                    if (response.message?.contains("No Internet") == true){
                          alertDialogBox.showNoInternetDialog(requireContext())
                    }else{ Helper.showSnackbar(requireView(),response.message.toString()) }
                }
                is Resources.Loading -> {progressBarVisible()}
                is Resources.Success ->{
                    response.data.let {
                        Helper.showSnackbar(requireView(),"Password updated successfully")
                        val password = binding.etNewConfirmPassword.text.toString()
                        val loginId = args.loginId
                        viewModel.authUser(loginId,password)
                    }
                }
            }
        }

        viewModel.authUserResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Error -> {
                    progressBarInvisible()
                    if (response.message?.contains("No Internet") == true) {
                        alertDialogBox.showNoInternetDialog(requireContext())
                    } else if (response.message?.contains("User Not Found") == true) {
                        Helper.showSnackbar(requireView(),"Wrong email or password")
                    } else if (response.message?.contains("Incorrect Password") == true) {
                        Helper.showSnackbar(requireView(),"Wrong password")
                    } else { Helper.showSnackbar(requireView(),response.message.toString()) }
                }

                is Resources.Loading -> { progressBarVisible() }
                is Resources.Success -> {
                    progressBarInvisible()
                    response.data.let { authResponse ->
                        authResponse?.data?.let { userDetails ->
                            val otpVerified = userDetails.otpVerified
                            if (otpVerified != 1) {
                                Helper.showSnackbar(requireView(),"Verification Pending")
                                val email = args.loginId
                                val destination =
                                    ResetPasswordDirections.actionResetPasswordToUserOtpConfirmation2(email)
                                findNavController().navigate(destination)
                            } else {
                                authResponse.data.let {
                                    viewModel.saveUserLoginDataValue(it.id)
                                    viewModel.saveUserData(
                                        it.id, it.name, it.email, it.mobile, it.provider,
                                        it.city, it?.dob, it.gender.toString(),
                                        it.interests, it?.jewels, it.otpVerified,
                                        it.IDverified, it.proceed, it.loggedin
                                    )
                                }
                                viewModel.saveUserLoginStatus(true)
                                findNavController().navigate(R.id.action_resetPassword_to_dashboardFragment)
                            }
                        }
                    }
                }
                else -> {}
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun validateInputsAndResetPassword(){
        val newPassword = binding.etNewPassword.text.toString()
        val newConfirmPassword = binding.etNewConfirmPassword.text.toString()

        // Reset helper text colors
        binding.tilNewPassword.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),
            R.color.red
        ))
        binding.tilNewConfirmPassword.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),
            R.color.red
        ))

        var isValid = true

        if (newPassword.isEmpty()){
            binding.tilNewPassword.helperText = "Enter new password"
            isValid = false
        }else{
           binding.tilNewPassword.helperText = ""
        }

        if(newConfirmPassword.isEmpty()){
            binding.tilNewConfirmPassword.helperText = "Enter confirm password"
            isValid = false
        }else{
            binding.tilNewConfirmPassword.helperText = ""
        }

        if (!newPassword.contains(newConfirmPassword) || !newConfirmPassword.contains(newPassword)) {
            binding.tilNewPassword.helperText = "Password doesn't match"
            binding.tilNewConfirmPassword.helperText = "Password doesn't match"
            isValid = false
        }else{
            binding.tilNewConfirmPassword.helperText = ""
            binding.tilNewPassword.helperText = ""
        }

        if (isValid) {
            val params = viewModel.generateEncodedParamsToUpdateUser(pwd = newConfirmPassword)
            viewModel.updateUser(params,args.userId)
        }
    }

    private fun btnVisibility() {
        Helper.btnVisibility(requireContext(),binding.etNewPassword,binding.etNewConfirmPassword,
            button = binding.btnResetPassword)
    }

    private fun progressBarVisible(){
        binding.btnResetPassword.visibility = View.INVISIBLE
        binding.pbResetPassword.visibility = View.VISIBLE
    }
    private fun progressBarInvisible(){
        binding.btnResetPassword.visibility = View.VISIBLE
        binding.pbResetPassword.visibility = View.INVISIBLE
    }

}