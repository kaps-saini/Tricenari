package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentEventBinding
import com.mavalore.tricenari.databinding.FragmentForgetPasswordBinding
import com.mavalore.tricenari.helper.Helper
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()
    private var userId = 0
    private var requestedOtp = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_forget_password, container, false)

        btnVisibility()
        viewModel.initAuthSharedPreferences(requireContext())

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvSignupFromReset.setOnClickListener {
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_registerFragment)
        }

        binding.button.setOnClickListener {
            if (requestedOtp == 0){
                requestOtp()
            }else{
                validateOtp()
            }
        }

        viewModel.otpResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {
                    if (response.message?.contains("No Internet") == true){
                        //  noInternetAlertDialogBox.showNoInternetDialog(requireContext())
                    }else if (response.message?.contains("User Not Found") == true){
                        Helper.showSnackbar(requireView(),"Wrong email")
                    }
                }
                is Resources.Loading -> {progressBarVisible()}
                is Resources.Success ->{
                    Helper.showSnackbar(requireView(),"Please check your email")
                    response.data.let { it ->
                        progressBarInvisible()
                        binding.tilOneTimePassword.visibility = View.VISIBLE
                        it?.data?.let {
                            requestedOtp = it.otp
                            userId = it.id
                            Log.e("otp",it.toString())
                        }
                    }
                }
            }
        }

        binding.etEmailForgetPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { btnVisibility() }
            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun requestOtp() {
        val email = binding.etEmailForgetPassword.text.toString()

        // Reset helper text colors
        binding.tilEmailForgetPassword.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))

        var isValid = true

        //only email valid so check for email pattern
        if (email.isEmpty()) {
            binding.tilEmailForgetPassword.helperText = "*Required"
            isValid = false
        }else if (!(viewModel.isValidEmail(email))){
            Helper.showSnackbar(requireView(),"Invalid Email address")
            isValid = false
        }else{
            binding.tilEmailForgetPassword.helperText = ""
        }

        // If all fields are valid, perform sign up
        if (isValid) {
            viewModel.requestOtp(email)
        }
    }

    private fun validateOtp(){
        val otp = binding.etOtpForgetPassword.text.toString()

        binding.tilOneTimePassword.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))

        val isValid: Boolean

        if (otp.isEmpty()){
            binding.tilOneTimePassword.helperText = "Enter Otp"
            isValid = false
        }else if (otp.toInt() != requestedOtp) {
            binding.tilOneTimePassword.helperText = "Wrong OTP"
            isValid = false
        }else{
                Log.e("otp", otp)
                Log.e("otp",requestedOtp.toString())
            isValid = true
            binding.tilOneTimePassword.helperText = ""
            binding.tilEmailForgetPassword.helperText = ""
        }

        if (isValid) {
            binding.button.visibility = View.VISIBLE
            binding.pbForgetPassword.visibility = View.INVISIBLE

            Helper.showSnackbar(requireView(),"OTP verified successfully")
            val email = binding.etEmailForgetPassword.text.toString()
            val destination = ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToResetPassword(userId,email)
            findNavController().navigate(destination)
        }
    }

    private fun btnVisibility() {
        Helper.btnVisibility(requireContext(),binding.etEmailForgetPassword, button = binding.button)
    }

    private fun progressBarVisible(){
        binding.button.visibility = View.INVISIBLE
        binding.pbForgetPassword.visibility = View.VISIBLE
    }
     private fun progressBarInvisible(){
        binding.button.visibility = View.VISIBLE
        binding.pbForgetPassword.visibility = View.INVISIBLE
    }

}