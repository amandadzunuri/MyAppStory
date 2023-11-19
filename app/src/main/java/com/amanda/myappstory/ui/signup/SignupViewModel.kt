package com.amanda.myappstory.ui.signup

import androidx.lifecycle.ViewModel
import com.amanda.myappstory.data.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    suspend fun registerUser(name: String, email: String, password: String) =
        repository.registerUser(name, email, password)
}