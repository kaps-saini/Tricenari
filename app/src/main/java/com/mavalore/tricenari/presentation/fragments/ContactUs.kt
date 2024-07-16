package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentContactUsBinding
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactUs : Fragment() {

    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection
    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    private var userId:Int = 0
    private lateinit var currentEmail:String
    private lateinit var currentMobile:String
    private lateinit var currentUserName:String
    private var addMobileToProfile = false
    private lateinit var mobileNo:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us, container, false)

        //Initialized here to use globally
        mobileNo = binding.etMobileNo.text.toString()

        viewModel.initAuthSharedPreferences(requireContext())
        val userData = viewModel.getUserData()
        if (userData != null) {
            userId = userData.id
            currentMobile = userData.mobile.toString()
            currentUserName = userData.name
            currentEmail = userData.email
        }

        viewModel.contactUsResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {
                    btnIsVisible()
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_SHORT).show()
                }
                is Resources.Loading -> {
                    btnIsInvisible()
                }
                is Resources.Success -> {
                    if (response.data?.data?.equals("success",true) == true){
                        if (addMobileToProfile){
                            val paramUpdateUserProfile = viewModel.generateEncodedParamsToUpdateUser(mobile = mobileNo)
                            viewModel.updateUser(paramUpdateUserProfile,userId)
                        }
                    }else if (response.data?.data?.equals("Invalid Mobile format",true)  == true){
                        btnIsVisible()
                        Toast.makeText(requireContext(),"Invalid Mobile format",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.updateUserResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {
                    btnIsVisible()
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_SHORT).show()
                }
                is Resources.Loading -> {
                    btnIsInvisible()
                }
                is Resources.Success -> {
                    if (response.data?.data?.equals("success",true) == true){
                        viewModel.saveUserData(mobile = mobileNo)
                        btnIsVisible()
                        Toast.makeText(requireContext(),"Profile updated successfully",Toast.LENGTH_SHORT).show()
                        Toast.makeText(requireContext(),"Thanks for contacting us",Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                        clearInputFields()
                    }
                }
            }
        }

        btnVisibility()

        binding.etUserNameContactus.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })

        binding.etMobileNo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                mobileNo = binding.etMobileNo.text.toString()
                if (mobileNo != (currentMobile)){
                    binding.checkBoxAddMobile.visibility = View.VISIBLE
                }else{
                    binding.checkBoxAddMobile.visibility = View.GONE
                }
                btnVisibility()
            }

        })

        binding.etEmailContactus.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })

        binding.etMessage.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })

        binding.checkBoxAddMobile.setOnCheckedChangeListener { buttonView, isChecked ->
            addMobileToProfile = isChecked
        }

        binding.btnSubmitContactUs.setOnClickListener {
            handleContactUs()
        }


        return binding.root
    }

    private fun handleContactUs(){
        val userName = binding.etUserNameContactus.text.toString()
        val email = binding.etEmailContactus.text.toString()
        val message = binding.etMessage.text.toString()

        // Reset helper text colors
        binding.tilUserNameContactus.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),
            R.color.red
        ))
        binding.tilMobileNoContactus.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),
            R.color.red
        ))
        binding.tilEmailContactus.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),
            R.color.red
        ))
        binding.tilMessage.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),
            R.color.red
        ))

        var isValid = true

        if (userName.isEmpty() || userName != currentUserName) {
            binding.tilUserNameContactus.helperText = "*Required"
            isValid = false
        }else{
            binding.tilUserNameContactus.helperText = ""
        }

        if (mobileNo.isEmpty()) {
            binding.tilMobileNoContactus.helperText = "*Required"
            isValid = false
        }else{
            binding.tilMobileNoContactus.helperText = ""
        }

        if (email.isEmpty() || email != currentEmail) {
            binding.tilEmailContactus.helperText = "*Required"
            isValid = false
        }else{
            binding.tilEmailContactus.helperText = ""
        }

        if (message.isEmpty()) {
            binding.tilMessage.helperText = "*Required"
            binding.tilMessage.boxStrokeErrorColor = ContextCompat.getColorStateList(requireContext(),
                R.color.red
            )
            isValid = false
        }else{
            binding.tilMessage.helperText = ""
        }

        // If all fields are valid then perform action
        if (isValid) {
            val param = viewModel.generateEncodedParamsToAddUser(
                userId.toString(),userName,email,mobileNo,message,null)
            Log.i("contactus",param)
            viewModel.sendContactUsData(param)
        }
    }

    private fun btnVisibility() {
        val isUserEmailEmpty =
            binding.etEmailContactus.getText() == null || binding.etEmailContactus.getText().toString()
                .isEmpty()
        val isMobileNoEmpty =
            binding.etMobileNo.getText() == null || binding.etMobileNo.getText().toString()
                .isEmpty()
        val isUserNameEmpty =
            binding.etUserNameContactus.getText() == null || binding.etUserNameContactus.getText().toString()
                .isEmpty()
        val isMessageEmpty =
            binding.etMessage.getText() == null || binding.etMessage.getText().toString()
                .isEmpty()

        if (isUserEmailEmpty || isMobileNoEmpty || isUserNameEmpty || isMessageEmpty) {
            binding.btnSubmitContactUs.setAlpha(0.4f)
            binding.btnSubmitContactUs.isActivated = false
            binding.btnSubmitContactUs.isClickable = false
        } else {
            binding.btnSubmitContactUs.setAlpha(1f)
            binding.btnSubmitContactUs.isActivated = true
            binding.btnSubmitContactUs.isClickable = true
        }
    }

    private fun clearInputFields(){
        binding.etUserNameContactus.setText("")
        binding.etMobileNo.setText("")
        binding.etEmailContactus.setText("")
        binding.etMessage.setText("")
    }
    private fun btnIsVisible(){
        binding.btnSubmitContactUs.visibility = View.VISIBLE
        binding.pbContactUs.visibility = View.INVISIBLE
    }

    private fun btnIsInvisible(){
        binding.btnSubmitContactUs.visibility = View.INVISIBLE
        binding.pbContactUs.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}