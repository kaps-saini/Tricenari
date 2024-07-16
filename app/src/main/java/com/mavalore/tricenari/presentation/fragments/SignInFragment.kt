package com.mavalore.tricenari.presentation.fragments

import android.content.Intent
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
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentSignInBinding
import com.mavalore.tricenari.helper.AlertDialogBox
import com.mavalore.tricenari.helper.CheckInternetConnection
import com.mavalore.tricenari.helper.Helper
import com.mavalore.tricenari.presentation.vm.TriceNariViewModel
import com.mavalore.tricenari.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val viewModel by viewModels<TriceNariViewModel>()

    private lateinit var callbackManager: CallbackManager
    private val TAG = "Auth"

    @Inject
    lateinit var checkInternetConnection: CheckInternetConnection

    @Inject
    lateinit var alertDialogBox: AlertDialogBox

    private var authUserWithEmailAndPassword = false

    companion object {
        private const val RC_SIGN_IN = 120
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)

        btnVisibility()
        viewModel.initAuthSharedPreferences(requireContext())
        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.tvResetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_forgetPasswordFragment)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_registerFragment)
        }

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

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        binding.loginButton.setReadPermissions("email", "public_profile")
        binding.loginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    loginWithFacebook()
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                }
            },
        )


        // Assuming getCurrentAccessToken() is a method to get the current access token
        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()

        // Check if the access token is valid and not expired
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn){
            Helper.showSnackbar(requireView(),"login true")
        }

        binding.etAuthEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {btnVisibility()}
            override fun afterTextChanged(s: Editable?) { }
        })

        binding.etAuthPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {btnVisibility()}
            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.authUserResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Error -> {
                    binding.btnSignIn.visibility = View.VISIBLE
                    binding.pbAuth.visibility = View.INVISIBLE
                    if (response.message?.contains("No Internet") == true) {
                        alertDialogBox.showNoInternetDialog(requireContext())
                    } else if (response.message?.contains("User Not Found") == true) {
                        Toast.makeText(
                            requireContext(),
                            "Wrong email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (response.message?.contains("Incorrect Password") == true) {
                        Toast.makeText(requireContext(), "Wrong password", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            response.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is Resources.Loading -> {
                    binding.btnSignIn.visibility = View.INVISIBLE
                    binding.pbAuth.visibility = View.VISIBLE
                }
                is Resources.Success -> {
                    response.data.let { authResponse ->
                        authResponse?.data?.let { userDetails ->
                            val otpVerified = userDetails.otpVerified
                            if (otpVerified != 1) {
                                Toast.makeText(requireContext(), "Verification Pending", Toast.LENGTH_SHORT).show()

                                val email = binding.etAuthEmail.text.toString().trim()
                                val destination =
                                    SignInFragmentDirections.actionSignInFragmentToUserOtpConfirmation2(email)
                                findNavController().navigate(destination)
                            } else {
                                authResponse.data.let { it ->
                                    viewModel.saveUserLoginDataValue(it.id)
                                    viewModel.saveUserData(
                                        it.id, it.name, it.email, it.mobile, it.provider,
                                        it.city, it?.dob, it.gender.toString(),
                                        it.interests, it?.jewels, it.otpVerified,
                                        it.IDverified, it.proceed, it.loggedin
                                    )
                                }
                                viewModel.saveUserLoginStatus(true)
                                val user = auth.currentUser
                                if (authUserWithEmailAndPassword){
                                    findNavController().navigate(R.id.action_signInFragment_to_dashboardFragment)
                                }else{
                                    switchToDashboard(user)
                                }
                            }
                        }
                    }

                }
                else -> {}
            }
        }

        viewModel.addUserResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Error -> {
                    Log.d(
                        "signIn",
                        "TricenariNewUserAddingException:${response.message.toString()}"
                    )
                }
                is Resources.Loading -> {}
                is Resources.Success -> {
                    Log.d(
                        "signIn",
                        "TricenariNewUserAdded:${response.data?.status_message.toString()}"
                    )
                }
                else -> {}
            }
        }

        return binding.root
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val facebookAuthId = auth.currentUser?.uid.toString()
                    viewModel.authUser(facebookAuthId, facebookAuthId)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
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
        val currentUser = viewModel.getUserLoginStatus()
        if (currentUser) {
            val user = auth.currentUser
            if (authUserWithEmailAndPassword){
                findNavController().navigate(R.id.action_signInFragment_to_dashboardFragment)
            }else{
                switchToDashboard(user)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (checkInternetConnection.hasInternetConnection(requireContext())) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("signIn", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                    val name = auth.currentUser?.displayName.toString()
                    val email = auth.currentUser?.email.toString()
                    val userId = auth.currentUser?.uid.toString()
                    val param = viewModel.generateEncodedParamsToAddUser(
                        name,
                        email,
                        userId,
                        userId,
                        null,
                        "google"
                    )
                    viewModel.addNewUser(param)
                    Log.d("signIn", "TricenariNewUserAdded:$name")
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("signIn", "${task.exception}")
                    Toast.makeText(requireContext(), "${task.exception}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            alertDialogBox.showNoInternetDialog(requireContext())
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
                        val googleAuthUid = auth.currentUser?.uid.toString()
                        viewModel.authUser(googleAuthUid, googleAuthUid)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(requireContext(), "Sign in Failed", Toast.LENGTH_LONG).show()
                        Log.w("firebaseAuth", "signInWithCredential:failure", task.exception)
                    }
                }
        } else {
            alertDialogBox.showNoInternetDialog(requireContext())
        }
    }

    private fun authUserWithEmail() {
        val emailOrMobile = binding.etAuthEmail.text.toString().trim()
        val password = binding.etAuthPassword.text.toString().trim()

        binding.textInputLayout.setHelperTextColor(ContextCompat.getColorStateList(requireContext(), R.color.red))
        binding.textInputLayout2.setHelperTextColor(ContextCompat.getColorStateList(requireContext(), R.color.red))

        var isValid = true

        if (emailOrMobile.isEmpty()) {
            binding.textInputLayout.helperText = "*Required"
            isValid = false
        }else if(!(viewModel.isValidEmail(emailOrMobile))){
            Helper.showSnackbar(requireView(),"Invalid Email address")
            isValid = false
        } else{
            binding.textInputLayout.helperText = ""
        }

        if (password.isEmpty()) {
            binding.textInputLayout2.helperText = "*Required"
            isValid = false
        } else {
            binding.textInputLayout2.helperText = ""
        }

        if (isValid) {
            viewModel.authUser(emailOrMobile, password)
            authUserWithEmailAndPassword = true
        }
    }

    private fun btnVisibility() {
        Helper.btnVisibility(
            requireContext(),binding.etAuthEmail,binding.etAuthPassword, button = binding.btnSignIn)
    }

    private fun switchToDashboard(user:FirebaseUser?) {
        if (user != null){
            findNavController().navigate(R.id.action_signInFragment_to_dashboardFragment)
        }
    }

    private fun loginWithFacebook(){
        val name = auth.currentUser?.displayName.toString()
        val email = auth.currentUser?.email.toString()
        val userId = auth.currentUser?.uid.toString()
        val param = viewModel.generateEncodedParamsToAddUser(
            name,
            email,
            userId,
            userId,
            null,
            "facebook"
        )
        viewModel.addNewUser(param)
        Log.d("signIn", "TricenariNewUserAdded:$name")
    }


}