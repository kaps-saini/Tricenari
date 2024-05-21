package com.mavalore.tricenari.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentProfileBinding
import com.mavalore.tricenari.domain.models.dynamicValues.AllInterest
import com.mavalore.tricenari.domain.models.user.UserDetails
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TriceNariViewModel>()

    private lateinit var userData:UserDetails

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false)

        viewModel.initAuthSharedPreferences(requireContext())
        userData = viewModel.getUserData()
        val userFullName = userData.name.toString()
        val (userName,userLastName) = viewModel.separateName(userFullName)

        binding.tvUserNameProfile.text = userName
        if (userLastName.isEmpty()){
            binding.tvUserLastName.visibility = View.GONE
        }else{
            binding.tvUserNameProfile.visibility = View.VISIBLE
            binding.tvUserLastName.text = userLastName
        }
        binding.tvUserEmail.text = userData.email
        binding.tvUserGender.text = userData.gender
        if (userData.mobile?.isNotEmpty() == true){
            binding.tvUserMobileNo.visibility = View.VISIBLE
            binding.tvUserMobileNo.text = userData.mobile
        }else{
            binding.tvUserMobileNo.visibility = View.GONE
        }
        binding.tvUserDob.text = userData.dob

        if (userData.jewels.toString().isEmpty() || userData.jewels.toString().toInt() == 0){
            binding.tvJewels.visibility = View.GONE
            binding.lottieJewels.visibility = View.GONE
            binding.tvNoJewelsMsg.visibility = View.VISIBLE
        }else{
            binding.tvJewels.visibility = View.VISIBLE
            binding.lottieJewels.visibility = View.VISIBLE
            binding.tvNoJewelsMsg.visibility = View.GONE
        }

        binding.ivBackProfile.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.ivEditProfile.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_setup_profile)
        }

        viewModel.dynamicValuesResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {}
                is Resources.Loading -> {}
                is Resources.Success -> {
                    response.let {
                       val allInterestList = it.data?.data?.all_interests
                        if (allInterestList != null) {
                            populateUserPrefChips(allInterestList)
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun populateUserPrefChips(chipDataList: List<AllInterest>) {
        val userInterestIds = getUserPrefInterests()
        for (chipData in chipDataList) {
            if (userInterestIds.contains(chipData.id.toString())) {
                binding.cgUserInterest.addView(createChip(chipData))
            }
        }
    }

    private fun createChip(chipData: AllInterest): Chip {
        return Chip(requireContext()).apply {
            text = chipData.intName
            isCheckable = false
            chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.maroon)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun getUserPrefInterests(): List<String> {
        return userData.interests?.split(".") ?: emptyList()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.lottieJewels.clearAnimation()
        _binding = null
    }

}