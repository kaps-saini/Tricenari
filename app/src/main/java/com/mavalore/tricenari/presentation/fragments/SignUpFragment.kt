package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.mavalore.tricenari.databinding.FragmentSignupBinding
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.Helper
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_signup, container, false)

        btnVisibility()
        binding.ivBackSignup.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvSignInFromSignup.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_signInFragment)
        }

        binding.etUserName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {btnVisibility() }
            override fun afterTextChanged(s: Editable?) {}

        })

        binding.etUserEmail.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {btnVisibility()}
            override fun afterTextChanged(s: Editable?) {}
        })

         binding.etPassword.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {btnVisibility()}
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etConfirmPassword.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {btnVisibility()}
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSignup.setOnClickListener {
            validateInputsAndSignUp()
        }

        viewModel.addUserResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Error -> {
                    binding.btnSignup.isClickable = true
                    binding.btnSignup.visibility = View.VISIBLE
                    binding.pbSignup.visibility = View.INVISIBLE
                    if (response.message?.contains("No Internet") == true) {
                        alertDialogBox.showNoInternetDialog(requireContext())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                is Resources.Loading -> {
                    binding.btnSignup.isClickable = false
                    binding.btnSignup.visibility = View.INVISIBLE
                    binding.pbSignup.visibility = View.VISIBLE
                }

                is Resources.Success -> {
                    binding.btnSignup.isClickable = true
                    binding.btnSignup.visibility = View.VISIBLE
                    binding.pbSignup.visibility = View.INVISIBLE
                    response.data.let {
                        if (it?.status_message?.contains("Success") == true) {
                            Toast.makeText(
                                requireContext(),
                                "OTP send successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_registerFragment_to_userOtpConfirmation2)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                it?.status.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        return binding.root
    }

    // Function to validate input fields
    private fun validateInputsAndSignUp() {
        val userName = binding.etUserName.text.toString()
        val email = binding.etUserEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Reset helper text colors
        binding.tilUserName.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))
        binding.tilEmail.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))
        binding.tilPassword.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))
        binding.tilConfirmPassword.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))

        var isValid = true

        if (userName.isEmpty()) {
            binding.tilUserName.helperText = "*Required"
            isValid = false
        }else{
            binding.tilUserName.helperText = ""
        }

        //only email valid so check for email pattern
        if (email.isEmpty()) {
            binding.tilEmail.helperText = "*Required"
            isValid = false
        }else if (!(viewModel.isValidEmail(email))){
            Helper.showSnackbar(requireView(),"Invalid Email address")
            isValid = false
        }else{
            binding.tilEmail.helperText = ""
        }

        if (password.isEmpty()) {
            //12345
            binding.tilPassword.helperText = "*Required"
            isValid = false
        }else{
            binding.tilPassword.helperText = ""
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.helperText = "*Required"
            isValid = false
        }else{
            binding.tilConfirmPassword.helperText = ""
        }

        if (!password.contains(confirmPassword) || !confirmPassword.contains(password)) {
            binding.tilConfirmPassword.helperText = "Password doesn't match"
            binding.tilPassword.helperText = "Password doesn't match"
            isValid = false
        }else{
            binding.tilConfirmPassword.helperText = ""
            binding.tilPassword.helperText = ""
        }

        // If all fields are valid, perform sign up
        if (isValid) {
            val param = viewModel.generateEncodedParamsToAddUser(userName,email,email,password,null,"email")
            // Perform sign up logic here
            viewModel.addNewUser(param)
        }
    }

    private fun btnVisibility() {
        Helper.btnVisibility(requireContext(),binding.etUserName,binding.etPassword,
            binding.etConfirmPassword,binding.etUserEmail, button = binding.btnSignup)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}