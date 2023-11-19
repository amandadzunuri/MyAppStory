package com.amanda.myappstory.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amanda.myappstory.data.UserRepository
import com.amanda.myappstory.data.response.DetailStoryResponse
import com.amanda.myappstory.data.response.Story
import com.amanda.myappstory.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    private val _detailStory = MutableLiveData<Story>()
    val detailStory: MutableLiveData<Story> = _detailStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetail(id: String, token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).getDetail(id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _detailStory.postValue(responseBody.story)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object{
        private const val TAG = "DetailViewModel"
    }
}