package com.amanda.myappstory.data.pref

data class UserModel (
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
