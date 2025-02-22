package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentUserOtpConfirmationBinding
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.helper.Helper
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserOtpConfirmation : Fragment() {

    private var _binding: FragmentUserOtpConfirmationBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    private var sendOtp = 0
    private var userId = 0

    private val args:UserOtpConfirmationArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_user_otp_confirmation, container, false)

        btnVisibility()
        viewModel.initSharedPreferences(requireContext())

        viewModel.addUserResponse.observe(viewLifecycleOwner){addResponse->
            when(addResponse){
                is Resources.Error -> {
                    if (addResponse.message?.contains("No Internet", true)== true){
                        alertDialogBox.showNoInternetDialog(requireContext())
                    }else{
                        Toast.makeText(requireContext(),addResponse.message.toString(), Toast.LENGTH_SHORT).show()
                    }
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
                    if (response.message?.contains("No Internet", true) == true){
                        alertDialogBox.showNoInternetDialog(requireContext())
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { btnVisibility() }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnVerify.setOnClickListener {
            if (binding.etOtp.text?.isNotEmpty() == true){
                binding.btnVerify.visibility = View.INVISIBLE
                binding.pbVerify.visibility = View.VISIBLE
            }
            val inputOtp = binding.etOtp.text.toString().toInt()
            if (sendOtp == inputOtp){
                val params = viewModel.generateEncodedParamsToUpdateUser(otpVerified = 1)
                viewModel.saveUserLoginDataValue(userId)
                viewModel.saveUserLoginStatus(true)
                viewModel.updateUser(params,userId)

                binding.btnVerify.visibility = View.VISIBLE
                binding.pbVerify.visibility = View.INVISIBLE

                Toast.makeText(requireContext(),"Email verified successfully",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_userOtpConfirmation2_to_setup_profile)
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
        Helper.btnVisibility(requireContext(),binding.etOtp, button = binding.btnVerify)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}