package com.amanda.myappstory.ui.splashScreen

import androidx.lifecycle.ViewModel
import com.amanda.myappstory.data.UserRepository

class SplashScreenViewModel (private val repository: UserRepository): ViewModel() {
    fun getSession() = repository.getSession()
}