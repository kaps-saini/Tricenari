package com.mavalore.tricenari.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.mavalore.tricenari.R
import com.mavalore.tricenari.databinding.FragmentEventBinding
import com.mavalore.tricenari.databinding.FragmentSignInBinding
import com.mavalore.tricenari.presentation.activity.HomeActivity

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnLogin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 120
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_in, container, false)


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



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

private fun signIn() {
    val signInIntent = googleSignInClient.signInIntent
    startActivityForResult(signInIntent, SignInFragment.RC_SIGN_IN)
}

override fun onStart() {
    super.onStart()
    // Check if user is signed in (non-null) and update UI accordingly.
    auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    if(currentUser != null){
        startActivity(Intent(requireActivity(), HomeActivity::class.java))
    }

}
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == SignInFragment.RC_SIGN_IN) {
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

   // if (NetworkManager().checkConnectivity(this)) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("firebaseAuth", "signInWithCredential:success")
                    startActivity(Intent(requireActivity(), HomeActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(requireContext(), "Sign in Failed", Toast.LENGTH_LONG).show()
                    Log.w("firebaseAuth", "signInWithCredential:failure", task.exception)
                }
            }
//    }else{
//        val dialog = AlertDialog.Builder(this)
//        dialog.setTitle("Error")
//        dialog.setMessage("Internet connection not found")
//        dialog.setPositiveButton("ok"){ text, lister ->
//            ActivityCompat.finishAffinity(this)
//        }
//        dialog.setNegativeButton("Open Settings"){text, listner ->
//            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
//            startActivity(intent)
//            this.finish()
//        }
//        dialog.create()
//        dialog.show()
//    }
    }



}