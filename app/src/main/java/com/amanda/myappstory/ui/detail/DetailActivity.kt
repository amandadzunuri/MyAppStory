package com.amanda.myappstory.ui.detail

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amanda.myappstory.R
import com.amanda.myappstory.data.response.Story
import com.amanda.myappstory.databinding.ActivityDetailBinding
import com.amanda.myappstory.ui.ViewModelFactory
import com.amanda.myappstory.ui.addStory.getToken
import com.amanda.myappstory.ui.addStory.token
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(EXTRA_ID)

        setupView()
        getToken(this)

        detailViewModel.getDetail(id.toString(), token)
        detailViewModel.detailStory.observe(this) { detailStory ->
            setData(detailStory)
        }
        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setData(detailStory: Story?) {
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(detailStory?.photoUrl)
                .into(ivDetail)
            tvName.text = "${detailStory?.name}"
            tvDeskripsiDetail.text = "${detailStory?.description}"
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
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.blue)))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar!!.title = getString(R.string.title_detail)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}