package com.app.tasty.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.tasty.R
import com.app.tasty.databinding.FragmentHolderActivityBinding
import com.app.tasty.utilities.ConnectivityObserver
import com.app.tasty.utilities.NetworkConnectivityObserver
import kotlinx.coroutines.launch

class FragmentHolderActivity : AppCompatActivity() {
    private var _binding: FragmentHolderActivityBinding? = null
    private val binding: FragmentHolderActivityBinding
        get() = _binding!!
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentHolderActivityBinding.inflate(layoutInflater)
        Thread.sleep(1000)
        installSplashScreen()
        setContentView(binding.root)
        connectivityObserver = NetworkConnectivityObserver(this)
        //nav controller setup
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        lifecycleScope.launch {
            connectivityObserver.Observe().collect { status ->
                if (status == ConnectivityObserver.Status.Available) {
                    binding.fragmentContainerView.visibility = View.VISIBLE
                    binding.connectivityStatus.visibility = View.GONE
                } else {
                    binding.fragmentContainerView.visibility = View.GONE
                    binding.connectivityStatus.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp() || super.onSupportNavigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}