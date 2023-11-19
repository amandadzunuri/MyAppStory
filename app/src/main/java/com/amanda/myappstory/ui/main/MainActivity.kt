package com.amanda.myappstory.ui.main

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.amanda.myappstory.R
import com.amanda.myappstory.data.response.ListStoryItem
import com.amanda.myappstory.databinding.ActivityMainBinding
import com.amanda.myappstory.ui.ViewModelFactory
import com.amanda.myappstory.ui.addStory.AddStoryActivity
import com.amanda.myappstory.ui.addStory.getToken
import com.amanda.myappstory.ui.addStory.token
import com.amanda.myappstory.ui.welcome.WelcomeActivity


class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cekSession()
        setupView()
        getToken(this)
        addStory()

        mainViewModel.getStory(token)

        mainViewModel.listStory.observe(this) { itemsItem ->
            setStoryData(itemsItem)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
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
        supportActionBar!!.title = getString(R.string.title_main)
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getStory(token)
    }

    private fun addStory() {
        binding.fabAdd.setOnClickListener{
            val intent = Intent(this, AddStoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun cekSession() {
        mainViewModel.getSession().observe(this) {
            if (!it.isLogin) {
                startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                finish()
            }else{
                Log.d("token",it.token)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setStoryData(itemsItem: List<ListStoryItem>?) {
        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        val adapter = ListStoryAdapter()
        adapter.submitList(itemsItem)

        binding.apply {
            rvListStory.layoutManager = layoutManager
            rvListStory.addItemDecoration(itemDecoration)
            rvListStory.adapter = adapter
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}