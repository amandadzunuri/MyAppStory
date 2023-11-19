package com.amanda.myappstory.di

import android.content.Context
import com.amanda.data.pref.UserPreference
import com.amanda.myappstory.data.UserRepository
import com.amanda.myappstory.data.retrofit.ApiConfig
import com.amanda.myappstory.ui.splashScreen.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService,pref)
    }

}