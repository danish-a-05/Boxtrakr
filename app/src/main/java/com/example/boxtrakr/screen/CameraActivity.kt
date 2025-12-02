package com.example.boxtrakr.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.view.setPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : FragmentActivity() {

    companion object {
        const val TAG = "CameraActivity"
        const val EXTRA_RESULT_PATH = "resultImagePath"
    }

    private lateinit var previewView: PreviewView
    private var imageCapture: ImageCapture? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            // permission denied - exit
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Simple full-screen PreviewView
        previewView = PreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(previewView)
        previewView.setPadding(0)

        // check camera permission
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // show rationale then request
                AlertDialog.Builder(this)
                    .setTitle("Camera permission required")
                    .setMessage("Camera access is required to take a photo for the box thumbnail.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Cancel") { _, _ -> finish() }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
                finish()
            }

            // overlay a simple tap-to-capture: when user taps preview, take picture
            previewView.setOnClickListener {
                takePhotoAndPreview()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhotoAndPreview() {
        val imageCapture = imageCapture ?: return

        // create temp file in cache first, we will show preview and then persist to filesDir on Keep
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val tempFile = File(cacheDir, "temp_${timeStamp}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // show preview dialog (simple)
                    lifecycleScope.launch(Dispatchers.Main) {
                        showPreviewDialog(tempFile)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Image save failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun showPreviewDialog(tempFile: File) {
        val imageUri = Uri.fromFile(tempFile)

        // build a simple dialog with the image and Keep/Retake actions.
        val previewView = PreviewImageDialog(this, imageUri) { keep ->
            if (keep) {
                // persist file to internal files directory
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val savedFile = saveImageToInternal(tempFile)
                        val intent = Intent().apply {
                            putExtra(EXTRA_RESULT_PATH, savedFile.absolutePath)
                        }
                        setResult(RESULT_OK, intent)
                    } catch (e: Exception) {
                        setResult(RESULT_CANCELED)
                    } finally {
                        finish()
                    }
                }
            } else {
                // retake: delete temp and let user tap again
                tempFile.delete()
            }
        }
        previewView.show()
    }

    private fun saveImageToInternal(temp: File): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val savedFile = File(filesDir, "box_${timeStamp}.jpg")
        temp.copyTo(savedFile, overwrite = true)
        temp.delete()
        return savedFile
    }
}
