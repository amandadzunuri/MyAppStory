package com.amanda.myappstory.ui.splashScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.amanda.myappstory.databinding.ActivitySplashScreenBinding
import com.amanda.myappstory.ui.ViewModelFactory
import com.amanda.myappstory.ui.main.MainActivity
import com.amanda.myappstory.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val viewModel by viewModels<SplashScreenViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val SPLASH_DELAY: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.getSession().collect {
                Handler().postDelayed({
                    if (it.isLogin) {
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, WelcomeActivity::class.java))
                        finish()
                    }
                    finish()
                }, SPLASH_DELAY)
            }
        }
    }
}