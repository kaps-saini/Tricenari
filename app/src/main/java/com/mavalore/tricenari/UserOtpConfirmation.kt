package com.mavalore.tricenari

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mavalore.tricenari.databinding.FragmentRegisterBinding
import com.mavalore.tricenari.databinding.FragmentUserOtpConfirmationBinding
import com.mavalore.tricenari.presentation.activity.HomeActivity
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserOtpConfirmation : Fragment() {

    private var _binding: FragmentUserOtpConfirmationBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    private var sendOtp = 0
    private var userId = 0

    private val args:UserOtpConfirmationArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user_otp_confirmation, container, false)

        viewModel.initSharedPreferences(requireContext())

        viewModel.addUserResponse.observe(viewLifecycleOwner){addResponse->
            when(addResponse){
                is Resources.Error -> {
                    Toast.makeText(requireContext(),addResponse.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is Resources.Loading -> {

                }
                is Resources.Success -> {
                    Toast.makeText(requireContext(),"OTP send successfully", Toast.LENGTH_SHORT).show()
                    addResponse.let {
                        sendOtp = it.data?.data?.otp!!
                        userId = it.data.data.id
                    }

                }
            }
        }

        val email = args.email
        viewModel.requestOtp(email)
        viewModel.otpResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {
                    if (response.message?.contains("No Internet") == true){
                      //  noInternetAlertDialogBox.showNoInternetDialog(requireContext())
                    }else if (response.message?.contains("User Not Found") == true){
                        Toast.makeText(requireContext(),"Wrong email",Toast.LENGTH_SHORT).show()
                    }
                }
                is Resources.Loading -> {

                }
                is Resources.Success ->{
                    Toast.makeText(requireContext(),"Please check your email",Toast.LENGTH_SHORT).show()
                    response.data.let { it ->
                        it?.data?.let {
                            sendOtp = it.otp
                            userId = it.id
                            Log.e("otp",it.toString())
                        }

                    }
                }

            }
        }


        binding.etOtp.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
              btnVisibility()
            }

        })


        binding.btnVerify.setOnClickListener {
            if (binding.etOtp.text?.isNotEmpty() == true){
                binding.btnVerify.visibility = View.INVISIBLE
                binding.pbVerify.visibility = View.VISIBLE
            }
            val inputOtp = binding.etOtp.text.toString().toInt()
            if (sendOtp == inputOtp){
                val params = viewModel.generateEncodedParamsToUpdateUser(null,null,null,1)
                viewModel.saveUserLoginDataValue(userId)
                viewModel.saveUserLoginStatus(true)
                viewModel.updateUser(params,userId)

                binding.btnVerify.visibility = View.VISIBLE
                binding.pbVerify.visibility = View.INVISIBLE

                Toast.makeText(requireContext(),"Email verified successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity,HomeActivity::class.java))
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                Intent.FLAG_ACTIVITY_NEW_TASK
            }else{
                Log.e("otp",inputOtp.toString())
                Log.e("otp",sendOtp.toString())
                binding.btnVerify.visibility = View.VISIBLE
                binding.pbVerify.visibility = View.INVISIBLE
                Toast.makeText(requireContext(),"Please enter correct OTP",Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivBackVerify.setOnClickListener{
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun btnVisibility() {
        val isOtpEmpty = binding.etOtp.getText() == null || binding.etOtp.getText().toString().isEmpty()

        if (isOtpEmpty) {
            binding.btnVerify.setAlpha(0.4f)
            binding.btnVerify.isActivated = false
            binding.btnVerify.isClickable = false
        } else {
            binding.btnVerify.setAlpha(1f)
            binding.btnVerify.isActivated = true
            binding.btnVerify.isClickable = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}