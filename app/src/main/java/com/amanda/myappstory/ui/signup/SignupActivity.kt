package com.amanda.myappstory.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.amanda.myappstory.R
import com.amanda.myappstory.databinding.ActivitySignupBinding
import com.amanda.myappstory.ui.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivitySignupBinding
    private var registerJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupView()
        playAnimation()

        showLoading(false)

        binding.btnSignup.setOnClickListener { setupAction() }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.tvDesc, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.edtName, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                desc,
                nameTextView,
                emailTextView,
                passwordTextView,
                signup
            )
            startDelay = 100
        }.start()
    }

    private fun setupAction() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        showLoading(true)
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showLoading(false)
            alertDialog(getString(R.string.message_empty), false)
        } else {
            if(binding.edtEmail.error == null || binding.edtPassword.error == null) {
                lifecycleScope.launch {
                    if (registerJob.isActive) registerJob.cancel()
                    registerJob = launch {
                        viewModel.registerUser(name, email, password).collect { result ->
                            result.onSuccess {
                                showLoading(false)
                                alertDialog(getString(R.string.message_register_success) + " " + email, true)
                            }

                            result.onFailure {
                                showLoading(false)
                                alertDialog(getString(R.string.email) + " " + email + " " + getString(R.string.message_register_failed), false)
                            }
                        }
                    }
                }
            } else {
                showLoading(false)
                alertDialog(getString(R.string.message_error), false)
            }
        }
    }

    private fun alertDialog(message: String, status: Boolean) {
        if (status == true) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_success))
                setMessage(message)
                setPositiveButton(getString(R.string.title_oke)) { _, _ ->
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_failed))
                setMessage(message)
                setPositiveButton(getString(R.string.title_oke)) { _, _ ->

                }
                create()
                show()
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}