package com.mavalore.tricenari.presentation.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentSetupProfileBinding
import com.mavalore.tricenari.domain.models.dynamicValues.AllInterest
import com.mavalore.tricenari.domain.models.user.UserDetails
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class SetupProfile : Fragment() {

    private var _binding: FragmentSetupProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    private var selectedDate: String? = null
    private var userId: Int = 0
    private lateinit var userName: String
    private lateinit var city: String
    private lateinit var userPrefCity: String
    private lateinit var dob: String
    private lateinit var gender: String
    private lateinit var mobileNo: String
    private lateinit var selectedInterestId: MutableList<Int>
    private var selectedChipData: MutableList<AllInterest>? = null
    private lateinit var chipIds: String
    private lateinit var chipsList: MutableList<AllInterest>
    private lateinit var availableInterests: List<AllInterest>
    private var currentIndex = 0 // Keep track of the last processed index
    private val batchSize = 10 // Number of items to process at a time
    private var isInInitialChipPopulate = false
    private lateinit var userPrefData: UserDetails

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setup_profile, container, false)

        viewModel.initAuthSharedPreferences(requireContext())
        userPrefData = viewModel.getUserData()
        userId = userPrefData.id
        userName = userPrefData.name
        userPrefCity = userPrefData.city.toString()

        selectedInterestId = mutableListOf()
        setupUI()
        setupObservers()

        binding.btnSubmit.setOnClickListener {
            if (userPrefCity.isEmpty()) {
                handleSetupProfile()
            } else {
                handleUpdateProfile()
            }
        }

        binding.ivShuffle.setOnClickListener {
            if (isInInitialChipPopulate) {
                shuffleChips()
            }
        }

        return binding.root
    }

    private fun setupUI() {
        if (userPrefCity.isEmpty()) {
            binding.tvWelcomeMsg.visibility = View.VISIBLE
            binding.tvWelcomeMsg.text = "Hi $userName,\nSetup your profile to continue"
            binding.tilUserFullName.visibility = View.GONE
            binding.tilMobileNo.visibility = View.GONE
            binding.tilGender.visibility = View.GONE
        } else {
            binding.btnSubmit.text = "Update"
            binding.tvWelcomeMsg.visibility = View.GONE
            binding.tilUserFullName.visibility = View.VISIBLE
            binding.tilMobileNo.visibility = View.VISIBLE
            binding.tilGender.visibility = View.VISIBLE
            binding.etUserName.setText(userName)
            binding.etGender.setText(userPrefData.gender.toString())
            binding.etCity.setText(userPrefData.city.toString())
            binding.etDob.setText(userPrefData.dob.toString())
            binding.etMobileNo.setText(userPrefData.mobile.toString())
        }

        binding.etDob.setOnClickListener { showDatePickerDialog() }

        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.etGender.setAdapter(adapter)

        handleBtnVisibilityForInputFields()
    }

    private fun setupObservers() {
        viewModel.dynamicValuesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Error -> {}
                is Resources.Loading -> {}
                is Resources.Success -> handleDynamicValuesResponse(response.data?.data?.all_interests)
            }
        }

        viewModel.updateUserResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Error -> {
                    btnIsVisible()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is Resources.Loading -> btnIsInvisible()
                is Resources.Success -> {
                    if (response.data?.data?.equals("success", true) == true) {
                        userName = binding.etUserName.text.toString()
                        gender = binding.etGender.text.toString()
                        city = binding.etCity.text.toString()
                        dob = binding.etDob.text.toString()
                        val mobileNo = binding.etMobileNo.text.toString()
                        viewModel.saveUserData(
                            name = userName, gender = gender, city = city, mobile = mobileNo.ifEmpty { null },
                            dob = selectedDate, interest = chipIds
                        )
                        Log.i("update",mobileNo)
                        btnIsVisible()
                        Toast.makeText(requireContext(), "Welcome to TriceNari", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.action_setup_profile_to_dashboardFragment)
                    }
                }
            }
        }
    }

    private fun handleDynamicValuesResponse(list: List<AllInterest>?) {
        list?.let {
            chipsList = it.shuffled().toMutableList()
            if (userPrefCity.isNotEmpty()) populateUserPrefChips(it)
            if (!isInInitialChipPopulate) {
                isInInitialChipPopulate = true
                populateNextBatchOfChips()
            }

            updateAutoCompleteAdapter()

            binding.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                val selectedInterestName = parent.getItemAtPosition(position) as String
                val selectedInterest = it.firstOrNull { interest -> interest.intName == selectedInterestName }

                if (selectedInterest != null && !selectedInterestId.contains(selectedInterest.id)) {
                    hideKeyboard()
                    addSelectedChip(selectedInterest)
                    chipsList.remove(selectedInterest)
                    removeChipFromGroup(selectedInterest)
                    binding.autoCompleteTextView.text.clear()
                    updateAutoCompleteAdapter()
                }
            }

            binding.autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) hideKeyboard()
            }
        }
    }

    private fun updateAutoCompleteAdapter() {
        availableInterests = chipsList.filter { !selectedInterestId.contains(it.id) }
        val interestAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            availableInterests.map { it.intName }
        )
        binding.autoCompleteTextView.setAdapter(interestAdapter)
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun populateNextBatchOfChips() {
        val remainingItems = chipsList.subList(currentIndex, availableInterests.size)
        val batch = remainingItems.take(batchSize)

        populateChips(batch)
        currentIndex += batchSize
    }

    private fun populateChips(chipDataList: List<AllInterest>) {
        for (chipData in chipDataList) {
            val chip = createChip(chipData)
            binding.chipGroup.addView(chip)
        }
    }

    private fun createChip(chipData: AllInterest): Chip {
        return Chip(requireContext()).apply {
            text = chipData.intName
            isCheckable = true
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    addSelectedChip(chipData)
                    binding.chipGroup.removeView(this)
                }
            }
        }
    }

    private fun populateUserPrefChips(chipDataList: List<AllInterest>) {
        val userPrefInterest = getUserPrefInterests()
        for (chipData in chipDataList) {
            if (userPrefInterest.contains(chipData.id.toString())) {
                addSelectedChip(chipData)
            }
        }
    }

    private fun getUserPrefInterests(): List<String> {
        return userPrefData.interests?.split(".") ?: emptyList()
    }

    private fun addSelectedChip(chipData: AllInterest) {
        val chip = createSelectedChip(chipData)
        selectedInterestId.add(chipData.id)
        binding.selectedChipsGroup.addView(chip)
        updateAutoCompleteAdapter()
        chipIds = selectedInterestId.joinToString(separator = ".")
        Log.i("chipSaved", chipIds)
    }

    private fun createSelectedChip(chipData: AllInterest): Chip {
        return Chip(requireContext()).apply {
            text = chipData.intName
            isCloseIconVisible = true
            isCheckable = false
            closeIconTint = ContextCompat.getColorStateList(requireContext(), R.color.white)
            chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.maroon)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            setOnCloseIconClickListener {
                removeSelectedChip(chipData)
            }
        }
    }

    private fun removeSelectedChip(chipData: AllInterest) {
        selectedInterestId.remove(chipData.id)
        updateAutoCompleteAdapter()
        chipIds = selectedInterestId.joinToString(separator = ".")
        Log.i("chipSaved", chipIds)

        for (i in 0 until binding.selectedChipsGroup.childCount) {
            val chip = binding.selectedChipsGroup.getChildAt(i) as Chip
            if (chip.text == chipData.intName) {
                binding.selectedChipsGroup.removeView(chip)
                val removedChip = createChip(chipData)
                binding.chipGroup.addView(removedChip)

                break
            }
        }
    }

    private fun removeChipFromGroup(chipData: AllInterest) {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            if (chip.text == chipData.intName) {
                binding.chipGroup.removeView(chip)
                break
            }
        }
    }

    private fun handleBtnVisibilityForInputFields() {
        binding.etCity.addTextChangedListener(createTextWatcher())
        binding.etGender.addTextChangedListener(createTextWatcher())
        binding.etDob.addTextChangedListener(createTextWatcher())
        binding.etUserName.addTextChangedListener(createTextWatcher())
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                btnUpdateVisibility()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.etDob.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun handleSetupProfile() {
        city = binding.etCity.text.toString()
        dob = binding.etDob.text.toString()

        // Reset helper text colors
        binding.tilCity.setHelperTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.red
            )
        )
        binding.tilDob.setHelperTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.red
            )
        )

        var isValid = true
        if (city.isEmpty()) {
            binding.tilCity.helperText = "*Required"
            isValid = false
        } else {
            binding.tilCity.helperText = ""
        }

        if (dob.isEmpty()) {
            binding.tilDob.helperText = "*Required"
            isValid = false
        } else {
            binding.tilDob.helperText = ""
        }

        if (!(::chipIds.isInitialized)) {
            isValid = false
            Toast.makeText(requireContext(), "Select at least 3 interests", Toast.LENGTH_SHORT)
                .show()
        }

        // If all fields are valid, perform action
        if (isValid) {

            val param = viewModel.generateEncodedParamsToUpdateUser(
                city = city, dob = selectedDate,
                interest = chipIds.toString())
            // Perform sign up logic here
            viewModel.updateUser(param, userId)
        }
    }

    private fun handleUpdateProfile() {
        userName = binding.etUserName.text.toString()
        gender = binding.etGender.text.toString()
        city = binding.etCity.text.toString()
        dob = binding.etDob.text.toString()
        val mobileNo = binding.etMobileNo.text.toString()

        // Reset helper text colors
        binding.tilUserFullName.setHelperTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.red
            )
        )
        binding.tilGender.setHelperTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.red
            )
        )
        binding.tilCity.setHelperTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.red
            )
        )
        binding.tilDob.setHelperTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.red
            )
        )
        binding.tilMobileNo.setHelperTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.red
            )
        )

        var isValid = true

        if (userName.isEmpty()) {
            binding.tilUserFullName.helperText = "*Required"
            isValid = false
        } else {
            binding.tilUserFullName.helperText = ""
        }

        if (gender.isEmpty()) {
            binding.tilGender.helperText = "*Required"
            isValid = false
        } else {
            binding.tilGender.helperText = ""
        }

        if (city.isEmpty()) {
            binding.tilCity.helperText = "*Required"
            isValid = false
        } else {
            binding.tilCity.helperText = ""
        }

        if (dob.isEmpty()) {
            binding.tilDob.helperText = "*Required"
            isValid = false
        } else {
            binding.tilDob.helperText = ""
        }

        if (!isValidMobile(mobileNo)) {
            binding.tilMobileNo.helperText = "*Entre valid number"
            isValid = false
        } else {
            binding.tilMobileNo.helperText = ""
        }

        if (!(::chipIds.isInitialized)) {
            isValid = false
            Toast.makeText(requireContext(), "Select at least 3 interests", Toast.LENGTH_SHORT)
                .show()
        }

        if (isValid) {
            val param = viewModel.generateEncodedParamsToUpdateUser(
                name = userName,
                gender = gender,
                city = city,
                dob = selectedDate,
                interest = chipIds,
                mobile = mobileNo.ifEmpty { null }
            )
            Log.i("update",param)
            // Perform sign up logic here
            viewModel.updateUser(param, userId)
        }
    }

    private fun btnIsVisible() {
        binding.btnSubmit.visibility = View.VISIBLE
        binding.pbContactUs.visibility = View.GONE
    }

    private fun btnIsInvisible() {
        binding.btnSubmit.visibility = View.GONE
        binding.pbContactUs.visibility = View.VISIBLE
    }

    private fun btnUpdateVisibility() {
        val isUserNameEmpty =
            binding.etUserName.getText() == null || binding.etUserName.getText().toString()
                .isEmpty()
        val isCityEmpty =
            binding.etCity.getText() == null || binding.etCity.getText().toString()
                .isEmpty()
        val isDobEmpty =
            binding.etDob.getText() == null || binding.etDob.getText().toString()
                .isEmpty()
        val isGenderEmpty =
            binding.etGender.getText() == null || binding.etGender.getText().toString()
                .isEmpty()

        if (isUserNameEmpty || isCityEmpty || isGenderEmpty || isDobEmpty) {
            binding.btnSubmit.setAlpha(0.4f)
            binding.btnSubmit.isActivated = false
            binding.btnSubmit.isClickable = false
        } else {
            binding.btnSubmit.setAlpha(1f)
            binding.btnSubmit.isActivated = true
            binding.btnSubmit.isClickable = true
        }
    }

    private fun shuffleChips() {
        binding.chipGroup.removeAllViews()
        chipsList = chipsList.shuffled().toMutableList()
        currentIndex = 0
        populateNextBatchOfChips()
    }

    private fun isValidMobile(mobile: String?): Boolean {
        // Define the regex pattern for a valid mobile number
        val mobilePattern = Regex("^[0-9]{10}$")
        // Check if the mobile number matches the pattern
        return mobile != null && mobilePattern.matches(mobile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
