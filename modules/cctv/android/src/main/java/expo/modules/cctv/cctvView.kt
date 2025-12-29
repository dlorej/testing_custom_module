package expo.modules.cctv

// import android.content.Context
// import android.webkit.WebView
// import android.webkit.WebViewClient
// import expo.modules.kotlin.AppContext
// import expo.modules.kotlin.viewevent.EventDispatcher
// import expo.modules.kotlin.views.ExpoView
// import android.Manifest
// import android.content.ContentValues
// import android.content.pm.PackageManager
// import android.os.Build
// import android.os.Bundle
// import android.provider.MediaStore
// import androidx.camera.extensions
// // import androidx.appcompat.app.AppCompatActivity
// import androidx.camera.core.ImageCapture
// import androidx.camera.video.Recorder
// import androidx.camera.video.Recording
// import androidx.camera.video.VideoCapture
// import androidx.core.app.ActivityCompat
// import androidx.core.content.ContextCompat
// // import com.android.example.cameraxapp.databinding.ActivityMainBinding
// import java.util.concurrent.ExecutorService
// import java.util.concurrent.Executors
// import android.widget.Toast
// import androidx.activity.result.contract.ActivityResultContracts
// import androidx.camera.lifecycle.ProcessCameraProvider
// import androidx.camera.core.Preview
// import androidx.camera.core.CameraSelector
// import android.util.Log
// import androidx.camera.core.ImageAnalysis
// import androidx.camera.core.ImageCaptureException
// import androidx.camera.core.ImageProxy
// import androidx.camera.core.ResolutionSelector
// import androidx.camera.video.FallbackStrategy
// import androidx.camera.video.MediaStoreOutputOptions
// import androidx.camera.video.Quality
// import androidx.camera.video.QualitySelector
// import androidx.camera.video.VideoRecordEvent
// import androidx.core.content.PermissionChecker
// import android.annotation.SuppressLint
// import android.view.Gravity
// import android.view.View
// import android.view.ViewGroup
// import android.widget.Button
// import android.widget.FrameLayout
// import androidx.camera.core.AspectRatio
// import androidx.camera.core.Camera
// import androidx.camera.core.resolutionselector.AspectRatioStrategy
// import androidx.camera.core.resolutionselector.ResolutionSelector
// import androidx.camera.extensions.ExtensionMode
// import androidx.camera.extensions.ExtensionsManager
// import androidx.camera.view.PreviewView
// import java.nio.ByteBuffer
// import java.text.SimpleDateFormat
// import java.util.Locale

class cctvView(context: Context, appContext: AppContext) : ExpoView(context, appContext) {
    // Creates and initializes an event dispatcher for the `onLoad` event.
    // The name of the event is inferred from the value and needs to match the event name defined in the module.
    private val onImageTaken by EventDispatcher()
    private val activity
        get() = requireNotNull((appContext.activityProvider?.currentActivity))

    private var frameLayout = FrameLayout(context)

    private var imageCapture: ImageCapture? = null

    private var captureButton: Button = Button(context)
    private var viewFinder: PreviewView = PreviewView(context)
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var mycameraProvider: ProcessCameraProvider? = null

    init {

        frameLayout.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(frameLayout)

        viewFinder.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        viewFinder.isFocusableInTouchMode = true
        viewFinder.requestFocusFromTouch()
        installHierarchyFilter(viewFinder)
        frameLayout.addView(viewFinder)

        captureButton.text = "Capture"
        captureButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        captureButton.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        captureButton.alpha = 0.8f

        val buttonParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            bottomMargin = 48
        }

        captureButton.layoutParams = buttonParams
        frameLayout.addView(captureButton)

        captureButton.setOnClickListener {
            takePhoto()
        }
    }

    override fun onAttachedToWindow(){
        super.onAttachedToWindow()
        if (hasPermissions()){
            viewFinder.post { setupCamera() }
        }
    }

    override fun onDetachedFromWindow(){
        super.onDetachedFromWindow()
        cameraExecutor.shutdown()
        mycameraProvider?.unbindAll()
    }

    private fun installHierarchyFitter(view: ViewGroup) {
        view.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
            override fun onChildViewRemoved(parent: View?, child: View?) = Unit
            override fun onChildViewAdded(parent: View?, child: View?) {
                parent?.measure(
                    MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
                )
                parent?.layout(0, 0, parent.measuredWidth, parent.measuredHeight)
            }
        })
    }
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun hasPermissions(): Boolean {
        val requiredPermissions = arrayOf(android.Manifest.permission.CAMERA)
        if (requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }) {
            return true
        }
        ActivityCompat.requestPermissions(
            activity,
            requiredPermissions,
            42 // random callback identifier
        )
        return false
    }

    companion object{
        private const val TAG = "MyCamera"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    //*Initialize CameraX, and prepare to bind the camera use cases */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupCamera() {
        val aspectRatioStrategy = AspectRatioStrategy(
            AspectRatio.RATIO_16_9, AspectRatioStrategy.FALLBACK_RULE_NONE
        )
        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(aspectRatioStrategy)
            .build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            mycameraProvider = cameraProvider
            val extensionsManagerFuture =
                ExtensionsManager.getInstanceAsync(context, cameraProvider)
            extensionsManagerFuture.addListener({
                val extensionsManager = extensionsManagerFuture.get()

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                //Available AUTO,BOKEH,FACE_RETOUCH
                //You can use different ExtensionMode like ExtensionMode.HDR, ExtensionMode.NIGHT,
                if (extensionsManager.isExtensionAvailable(cameraSelector, ExtensionMode.AUTO)) {
                    Toast.makeText(context, "Extension available", Toast.LENGTH_LONG).show()

                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(viewFinder.surfaceProvider)
                        }

                    imageCapture = ImageCapture.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    try {
                        val selector =
                            extensionsManager.getExtensionEnabledCameraSelector(
                                cameraSelector,
                                ExtensionMode.AUTO
                            )
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            activity as AppCompatActivity,
                            selector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Use case binding failed", e)
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "No extension", Toast.LENGTH_LONG).show()

                    val preview = Preview.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .build().also {
                            it.setSurfaceProvider(viewFinder.surfaceProvider)
                        }

                    imageCapture = ImageCapture.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    try {
                        // Unbind use cases before rebinding
                        cameraProvider.unbindAll()
                        // Bind use cases to camera
                        cameraProvider.bindToLifecycle(
                            activity as AppCompatActivity, cameraSelector, preview, imageCapture
                        )
                    } catch (exc: Exception) {
                        Log.e(Tag, "Use case binding failed", exc)
                    }
                }
            }, ContextCompat.getMainExecutor(context))
        }, ContextCompat.getMainExecutor(context))
    }
}