package com.example.pillpop

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pillpop.Constants.LABELS_PATH
import com.example.pillpop.Constants.MODEL_PATH
import com.example.pillpop.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), Detector.DetectorListener {

    private lateinit var binding: ActivityMainBinding
    private val isFrontCamera = true

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var detector: Detector? = null

    private lateinit var cameraExecutor: ExecutorService
    // Variable to track if the alert has been shown
    private var Alert1 = false
    private var Alert2 = false
    private var Alert3 = false
    private var Alert4 = false
    private var Alert5 = false
    private var AlertPlus = false

    private var registro_id: Int = 0

    private var perfilId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraExecutor.execute {
            detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
            detector?.setup()
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Obtener registro_id del Intent
        registro_id = intent.getIntExtra("registro_id", 0)
        perfilId = intent.getIntExtra("perfil_id", 0)
        bindListeners()
    }

    private fun bindListeners() {
        binding.apply {
            isGpu.setOnCheckedChangeListener { buttonView, isChecked ->
                cameraExecutor.submit {
                    detector?.setup(isGpu = isChecked)
                }
                if (isChecked) {
                    buttonView.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.orange))
                } else {
                    buttonView.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.gray))
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider  = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val rotation = binding.viewFinder.display.rotation

        val cameraSelector = CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        preview =  Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
            val bitmapBuffer =
                Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )
            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
            imageProxy.close()

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                if (isFrontCamera) {
                    postScale(
                        -1f,
                        1f,
                        imageProxy.width.toFloat(),
                        imageProxy.height.toFloat()
                    )
                }
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
                matrix, true
            )

            detector?.detect(rotatedBitmap)
        }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.CAMERA] == true) { startCamera() }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector?.close()
        cameraExecutor.shutdown()
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()){
            startCamera()
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    companion object {
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf (
            Manifest.permission.CAMERA
        ).toTypedArray()
    }

    override fun onEmptyDetect() {
        runOnUiThread {
            binding.overlay.clear()
        }
    }

    private fun showAlert(message: String, onDismiss: () -> Unit) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Detección de Pastilla")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    onDismiss()
                }
                .show()
        }
    }
    private fun showAlert2(message: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Detección de Pastilla")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showAlert3(message: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Detección de Pastilla Completa")
                .setMessage(message)
                .setIcon(R.drawable.check_circular_habilitado)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }


    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        runOnUiThread {
            binding.inferenceTime.text = "${inferenceTime}ms"
            binding.overlay.apply {
                setResults(boundingBoxes)
                invalidate()
            }
            var bocaAbiertaDetected = false
            var bocaCerradaDetected = false
            var pastillaDetected = false

            for (box in boundingBoxes) {
                when (box.clsName) {
                    "  bocaAbierta" -> bocaAbiertaDetected = true
                    "  pastilla" -> pastillaDetected = true
                    "  bocaCerrada" -> bocaCerradaDetected = true
                }
            }

            if (bocaCerradaDetected && !pastillaDetected && !bocaAbiertaDetected && !Alert3 && !Alert1) {
                showAlert2("Por favor, abrir la boca para verificar la existencia de la pastilla")
                Alert1 = true

            }

            if (bocaAbiertaDetected && !pastillaDetected && !bocaCerradaDetected && !Alert3 && !Alert2) {
                showAlert2("Por favor, ponga su pastilla al interior de su boca")
                Alert2 = true

            }

            if (bocaAbiertaDetected && pastillaDetected && !bocaCerradaDetected && !Alert3 && !AlertPlus) {
                showAlert("Se detectó la pastilla a tomar correctamente; por favor, tómarsela y mostrar su boca abierta nuevamente.") {
                    Alert3 = true
                }
                AlertPlus=true
            }

            if (bocaAbiertaDetected && !pastillaDetected && !bocaCerradaDetected && Alert3 && !Alert4) {
                showAlert3("Se verificó la correcta toma total de la pastilla.")
                Alert4 = true
                val intent = Intent(this@MainActivity, PrincipalView::class.java)
                startActivity(intent)
            }

            if (bocaCerradaDetected && !bocaAbiertaDetected && !pastillaDetected && Alert3 && !Alert5) {
                showAlert2("Por favor, abrir la boca para poder verificar la toma total de la pastilla.")
                Alert5 = true

            }
        }
    }

}
