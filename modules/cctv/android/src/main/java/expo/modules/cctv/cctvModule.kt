package expo.modules.cctv

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.net.URL
// import android.Manifest
// import android.content.ContentValues
// import android.content.pm.PackageManager
// import android.os.Build
// import android.os.Bundle
// import android.provider.MediaStore
// import androidx.appcompat.app.AppCompatActivity
// import androidx.camera.core.ImageCapture
// import androidx.camera.video.Recorder
// import androidx.camera.video.Recording
// import androidx.camera.video.VideoCapture
// import androidx.core.app.ActivityCompat
// import androidx.core.content.ContextCompat
// import com.android.example.cameraxapp.databinding.ActivityMainBinding
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
// import androidx.camera.video.FallbackStrategy
// import androidx.camera.video.MediaStoreOutputOptions
// import androidx.camera.video.Quality
// import androidx.camera.video.QualitySelector
// import androidx.camera.video.VideoRecordEvent
// import androidx.core.content.PermissionChecker
// import java.nio.ByteBuffer
// import java.text.SimpleDateFormat
// import java.util.Locale

class cctvModule : Module() {
    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    override fun definition() = ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('cctv')` in JavaScript.
        Name("cctv")

        // Defines constant property on the module.
        Constant("PI") {
            Math.PI
        }

        // Defines event names that the module can send to JavaScript.
        Events("onChange")

        // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
        Function("hello") {
            "Hello world! 👋"
        }

        // Defines a JavaScript function that always returns a Promise and whose native code
        // is by default dispatched on the different thread than the JavaScript runtime runs on.
        AsyncFunction("setValueAsync") { value: String ->
            // Send an event to JavaScript.
            sendEvent("onChange", mapOf(
                "value" to value
            ))
        }

        // Enables the module to be used as a native view. Definition components that are accepted as part of
        // the view definition: Prop, Events.
        View(cctvView::class) {
            // Defines a setter for the `url` prop.
//            Prop("url") { view: cctvView, url: URL ->
//                view.webView.loadUrl(url.toString())
//            }
            // Defines an event that the view can send to JavaScript.
            Events("onImageTaken")
        }
    }
}
