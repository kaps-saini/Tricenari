package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TextViewBindingAdapter.OnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.engine.Resource
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentRegisterBinding
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register, container, false)


        binding.ivBackSignup.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvSignInFromSignup.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment2_to_signInFragment2)
        }

        binding.etUserName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })

        binding.etUserEmail.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })

         binding.etPassword.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })

        binding.etConfirmPassword.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })


        binding.btnSignup.setOnClickListener {
            validateInputsAndSignUp()
        }

        viewModel.addUserResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {
                    binding.btnSignup.isClickable = true
                    binding.btnSignup.visibility = View.VISIBLE
                    binding.pbSignup.visibility = View.INVISIBLE
                    if (response.message?.contains("No Internet") == true){
                      //  noInternetAlertDialogBox.showNoInternetDialog(requireContext())
                    }else{
                        Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading -> {
                    binding.btnSignup.isClickable = false
                    binding.btnSignup.visibility = View.INVISIBLE
                    binding.pbSignup.visibility = View.VISIBLE
                }
                is Resources.Success ->{
                    binding.btnSignup.isClickable = true
                    binding.btnSignup.visibility = View.VISIBLE
                    binding.pbSignup.visibility = View.INVISIBLE
                    response.data.let {
                        if (it?.status_message?.contains("Success") == true){
                            Toast.makeText(requireContext(),"OTP send successfully",Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment2_to_userOtpConfirmation)
                        }else{
                            Toast.makeText(requireContext(),it?.status.toString(),Toast.LENGTH_SHORT).show()
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
        if (email.isEmpty() || !(isValidEmail(email))) {
            binding.tilEmail.helperText = "*Required"
            isValid = false
            if (!(isValidEmail(email))){
                Toast.makeText(requireContext(),"Invalid Email address", Toast.LENGTH_SHORT).show()
            }
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
        val isUserEmailEmpty = binding.etUserEmail.getText() == null || binding.etUserEmail.getText().toString().isEmpty()
        val isPasswordEmpty = binding.etPassword.getText() == null || binding.etPassword.getText().toString().isEmpty()
        val isConfirmPasswordEmpty = binding.etConfirmPassword.getText() == null || binding.etConfirmPassword.getText().toString().isEmpty()
        val isUserNameEmpty = binding.etUserName.getText() == null || binding.etUserName.getText().toString().isEmpty()

        if (isUserEmailEmpty || isPasswordEmpty || isUserNameEmpty || isConfirmPasswordEmpty) {
            binding.btnSignup.setAlpha(0.4f)
            binding.btnSignup.isActivated = false
            binding.btnSignup.isClickable = false
        } else {
            binding.btnSignup.setAlpha(1f)
            binding.btnSignup.isActivated = true
            binding.btnSignup.isClickable = true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        // Define the regular expression pattern for email validation
        val emailRegex = Regex("([a-z0-9_.-]+)@([da-z.-]+)\\.([a-z.]{2,6})")

        // Check if the input email matches the pattern
        return emailRegex.matches(email)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}