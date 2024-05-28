package com.mavalore.tricenari.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mavalore.tricenari.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var btmNav:BottomNavigationView
    private lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TriceNari)
        setContentView(R.layout.activity_home)

        btmNav = findViewById(R.id.btmNavHome)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        btmNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when(destination.id){
                R.id.dashboardFragment, R.id.eventFragment, R.id.settingsFragment ->{
                    btmNav.visibility = View.VISIBLE
                }
                else ->{
                    btmNav.visibility = View.GONE
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Inside your activity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Check if the current destination is one of the specified fragments
        if (navController.currentDestination?.id == R.id.dashboardFragment ||
            navController.currentDestination?.id == R.id.eventFragment ||
            navController.currentDestination?.id == R.id.settingsFragment ||
            navController.currentDestination?.id == R.id.signInFragment ||
            navController.currentDestination?.id == R.id.registerFragment ||
            navController.currentDestination?.id == R.id.userOtpConfirmation2
        ) {
            // If the back stack has more than one entry, pop it
            if (navController.popBackStack().not()) {
                // If the back stack is empty, call super.onBackPressed() to exit the app
                super.onBackPressed()
            }
        } else {
            // Pop the back stack
            navController.popBackStack()
        }
    }

}