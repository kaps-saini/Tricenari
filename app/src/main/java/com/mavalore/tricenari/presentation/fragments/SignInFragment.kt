package com.mavalore.tricenari.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentSignInBinding
import com.mavalore.tricenari.domain.interfaces.AllInterface
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.presentation.activity.HomeActivity
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment(){

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val viewModel by viewModels<TriceNariViewModel>()

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection
    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    companion object {
        private const val RC_SIGN_IN = 120
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_in, container, false)

        viewModel.initSharedPreferences(requireContext())

        binding.tvResetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment2_to_forgetPasswordFragment2)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment2_to_registerFragment2)
        }

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = FirebaseAuth.getInstance()
        binding.ivGoogleSignIn.setOnClickListener { signIn() }


        binding.btnSignIn.setOnClickListener {
            authUserWithEmail()
        }

        binding.etAuthEmail.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })

        binding.etAuthPassword.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                btnVisibility()
            }

        })

        viewModel.authUserResponse.observe(viewLifecycleOwner){response->
            when(response){
                is Resources.Error -> {
                    binding.btnSignIn.visibility = View.VISIBLE
                    binding.pbAuth.visibility = View.INVISIBLE
                    if (response.message?.contains("No Internet") == true){
                        alertDialogBox.showNoInternetDialog(requireContext())
                    }else if (response.message?.contains("User Not Found") == true){
                        Toast.makeText(requireContext(),"Wrong email or password",Toast.LENGTH_SHORT).show()
                    }else if (response.message?.contains("Incorrect Password") == true){
                        Toast.makeText(requireContext(),"Wrong password",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
                is Resources.Loading -> {
                    binding.btnSignIn.visibility = View.INVISIBLE
                    binding.pbAuth.visibility = View.VISIBLE
                }

                is Resources.Success ->{
                    response.data.let {authResponse->
                        authResponse?.data?.let { userDetails ->
                            val otpVerified = userDetails.otpVerified
                                if (otpVerified != 1) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Verification Pending",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    authUserWithEmail()
                                    val email = binding.etAuthEmail.text.toString().trim()
                                    val destination = SignInFragmentDirections.actionSignInFragment2ToUserOtpConfirmation(email)
                                    findNavController().navigate(destination)
                                } else {
                                    authResponse.data.let { it ->
                                        viewModel.saveUserLoginDataValue(
                                            it.id
                                        )
                                    }
                                    viewModel.saveUserLoginStatus(true)
                                    startActivity(Intent(activity, HomeActivity::class.java))
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            }

                    }

                }

            }
        }



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

private fun signIn() {
    val signInIntent = googleSignInClient.signInIntent
    startActivityForResult(signInIntent, RC_SIGN_IN)
}

override fun onStart() {
    super.onStart()
    // Check if user is signed in (non-null) and update UI accordingly.
    auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    if(currentUser != null){
        val intent = Intent(requireActivity(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d("signIn", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w("signIn", "${task.exception}")
            Toast.makeText(requireContext(), "${task.exception}", Toast.LENGTH_LONG).show()
        }
    }
}

private fun firebaseAuthWithGoogle(idToken: String) {

    if (checkInternetConnection.hasInternetConnection(requireContext())) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("firebaseAuth", "signInWithCredential:success")
                    val intent = Intent(requireActivity(), HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(requireContext(), "Sign in Failed", Toast.LENGTH_LONG).show()
                    Log.w("firebaseAuth", "signInWithCredential:failure", task.exception)
                }
            }
        }else{
      alertDialogBox.showNoInternetDialog(requireContext())
        }
    }
    private fun authUserWithEmail() {
        val emailOrMobile = binding.etAuthEmail.text.toString().trim()
        val password = binding.etAuthPassword.text.toString().trim()

        binding.textInputLayout.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))
        binding.textInputLayout2.setHelperTextColor(ContextCompat.getColorStateList(requireContext(),R.color.red))

        var isValid = true

        if (emailOrMobile.isEmpty() || !(viewModel.isValidEmail(emailOrMobile))) {
            binding.textInputLayout.helperText = "*Required"
            isValid = false
            if (!(viewModel.isValidEmail(emailOrMobile))){
                Toast.makeText(requireContext(),"Invalid Email address",Toast.LENGTH_SHORT).show()
            }
        }else{
            binding.textInputLayout.helperText = ""
        }

        if (password.isEmpty()) {
            binding.textInputLayout2.helperText = "*Required"
            isValid = false
        }else{
            binding.textInputLayout2.helperText = ""
        }

        if (isValid){
            viewModel.authUser(emailOrMobile, password)
        }
    }

    private fun btnVisibility() {
        val isUserEmailEmpty = binding.etAuthEmail.getText() == null || binding.etAuthEmail.getText().toString().isEmpty()
        val isPasswordEmpty = binding.etAuthPassword.getText() == null || binding.etAuthPassword.getText().toString().isEmpty()

        if (isUserEmailEmpty || isPasswordEmpty) {
            binding.btnSignIn.setAlpha(0.4f)
            binding.btnSignIn.isActivated = false
            binding.btnSignIn.isClickable = false
        } else {
            binding.btnSignIn.setAlpha(1f)
            binding.btnSignIn.isActivated = true
            binding.btnSignIn.isClickable = true
        }
    }




}