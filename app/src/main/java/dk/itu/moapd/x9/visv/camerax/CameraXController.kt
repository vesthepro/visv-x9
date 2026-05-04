/*
 * MIT License
 *
 * Copyright (c) 2026 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.x9.visv.camerax

import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

/**
 * Controller responsible for configuring and starting CameraX use cases.
 *
 * Keeps CameraX binding logic outside UI classes (Fragments/Activities).
 */
object CameraXController {

    /**
     * Starts the camera preview and sets up an ImageCapture use case.
     *
     * @param context The application context.
     * @param lifecycleOwner The lifecycle owner.
     * @param selector The camera selector.
     * @param viewFinder The preview view.
     * @param onImageCaptureReady Callback to set the ImageCapture use case.
     * @param onCanSwitchCamera Callback to enable/disable the camera switch button.
     * @param onError Callback to show an error message.
     */
    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        selector: CameraSelector,
        viewFinder: PreviewView,
        onImageCaptureReady: (ImageCapture) -> Unit,
        onCanSwitchCamera: (Boolean) -> Unit,
        onError: (String) -> Unit,
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            {
                val cameraProvider = try {
                    cameraProviderFuture.get()
                } catch (t: Throwable) {
                    onError("Failed to get camera provider: ${t.message ?: t}")
                    return@addListener
                }

                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = viewFinder.surfaceProvider }

                val imageCapture = ImageCapture.Builder()
                    .build()

                try {
                    // Unbind before rebinding.
                    cameraProvider.unbindAll()

                    // Bind use cases to the given lifecycle.
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageCapture,
                    )

                    onImageCaptureReady(imageCapture)
                    onCanSwitchCamera(canSwitchCamera(cameraProvider))

                } catch (t: Throwable) {
                    onError("Use case binding failed: ${t.message ?: t}")
                    onCanSwitchCamera(false)
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    /**
     * Checks if the Android device has two cameras and it is possible to change the used camera.
     *
     * @param provider The process camera provider.
     */
    private fun canSwitchCamera(provider: ProcessCameraProvider): Boolean {
        return try {
            provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) &&
                    provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        } catch (_: CameraInfoUnavailableException) {
            false
        }
    }
}