package com.amanda.myappstory.ui.addStory

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.amanda.myappstory.R
import com.amanda.myappstory.data.response.FileUploadResponse
import com.amanda.myappstory.data.retrofit.ApiConfig
import com.amanda.myappstory.databinding.ActivityAddStoryBinding
import com.amanda.myappstory.ui.main.MainActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException


class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
        getToken(this)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener {uploadImage()}
        }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edLayoutDescription.text.toString()

            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService(token)
                    val successResponse = apiService.uploadImage(multipartBody, requestBody)
                    showLoading(false)
                    alertDialog(successResponse.message, true)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
                    showLoading(false)
                    alertDialog(errorResponse.message, false)
                }
            }
        } ?: alertDialog(getString(R.string.empty_image_warning), false)
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
        supportActionBar!!.title = getString(R.string.title_upload)
    }

    private fun playAnimation() {
        val image = ObjectAnimator.ofFloat(binding.previewImageView, View.ALPHA, 1f).setDuration(400)
        val btnCamera = ObjectAnimator.ofFloat(binding.cameraButton, View.ALPHA, 1f).setDuration(400)
        val btnGallery = ObjectAnimator.ofFloat(binding.galleryButton, View.ALPHA, 1f).setDuration(400)
        val descriptionText = ObjectAnimator.ofFloat(binding.tvDeskripsiUpload, View.ALPHA, 1f).setDuration(400)
        val descriptionLayout = ObjectAnimator.ofFloat(binding.edLayoutDescription, View.ALPHA, 1f).setDuration(400)
        val descriptionEdit = ObjectAnimator.ofFloat(binding.edtAddDeskripsi, View.ALPHA, 1f).setDuration(400)
        val btnUpload = ObjectAnimator.ofFloat(binding.uploadButton, View.ALPHA, 1f).setDuration(400)

        val btnTogether = AnimatorSet().apply {
            playTogether(btnCamera, btnGallery)
        }

        val descriptionTogether = AnimatorSet().apply {
            playTogether(descriptionLayout, descriptionEdit)
        }

        AnimatorSet().apply {
            playSequentially(image, btnTogether, descriptionText, descriptionTogether, btnUpload)
            start()
        }
    }

    private fun alertDialog(message: String, status: Boolean) {
        if (status == true) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_success))
                setMessage(message)
                setPositiveButton(getString(R.string.title_oke)) { _, _ ->
                    val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}