package dk.itu.moapd.x9.visv.view

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.visv.R
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import dk.itu.moapd.x9.visv.camerax.CameraXController
import dk.itu.moapd.x9.visv.media.PhotoCaptureManager
import dk.itu.moapd.x9.visv.permissions.CameraPermissionHelper
import dk.itu.moapd.x9.visv.view.widgets.OutlinedIconCircleButton
import dk.itu.moapd.x9.visv.viewmodels.CameraViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SwitchCamera

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onPhotoTaken: (Uri) -> Unit,
    onClose: () -> Unit,
    ) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var cameraSelector by remember {
        mutableStateOf(viewModel.selector.value ?: CameraSelector.DEFAULT_BACK_CAMERA)
    }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var canSwitchCamera by remember { mutableStateOf(false) }

    // Keep the latest image Uri so the viewer button can be enabled.
    val imageUri: Uri? by viewModel.imageUri.observeAsState(initial = null)

    // Permission request.
    var hasPermission by remember {
        mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        },
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onPhotoTaken(it)
            onClose()
        }
    }

    // Launch permission prompt once.
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // We create and hold a PreviewView instance so CameraX can bind to it.
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
    }

    // Start/bind CameraX whenever permission or selector changes.
    LaunchedEffect(hasPermission, cameraSelector) {
        if (!hasPermission) return@LaunchedEffect

        CameraXController.startCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            selector = cameraSelector,
            viewFinder = previewView,
            onImageCaptureReady = { imageCapture = it },
            onCanSwitchCamera = { canSwitchCamera = it },
            onError = { msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // Camera preview.
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView },
        )

        // Bottom controls.
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionDp(context, R.dimen.margin_xlarge))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedIconCircleButton(
                enabled = hasPermission && canSwitchCamera,
                imageVector = Icons.Filled.SwitchCamera,
                contentDescription = "Switch camera",
                iconSize = 24.dp,
                strokeWidth = 2.dp,
                onClick = {
                    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    } else {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }
                    viewModel.onCameraSelectorChanged(cameraSelector)
                },
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedIconCircleButton(
                enabled = hasPermission,
                imageVector = Icons.Filled.Circle,
                contentDescription = "Last taken image",
                iconSize = 80.dp,
                strokeWidth = 2.dp,
                onClick = {
                    val capture = imageCapture ?: return@OutlinedIconCircleButton
                    takePhoto(
                        context = context,
                        imageCapture = capture,
                        onSaved = { uri, filename ->
                            viewModel.onImageUriChanged(uri)

                            // Send result back to ReportScreen
                            onPhotoTaken(uri)
                            onClose()

                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Photo capture succeeded: $filename"
                                )
                            }
                        },
                        onError = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Photo capture failed: $message"
                                )
                            }
                        },
                    )
                },
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedIconCircleButton(
                enabled = hasPermission,
                imageVector = Icons.Filled.Photo,
                contentDescription = "Gallery",
                iconSize = 24.dp,
                strokeWidth = 2.dp,
                onClick = {
                    galleryLauncher.launch("image/*")
                },
            )
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp),
        )
    }
}

/**
 * Take a photo with the camera.
 *
 * @param context The application context.
 * @param imageCapture The image capture use case.
 * @param onSaved Callback to set the last captured image Uri.
 * @param onError Callback to show an error message.
 */
private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onSaved: (Uri, String) -> Unit,
    onError: (String) -> Unit,
) {
    PhotoCaptureManager.takePhoto(
        context = context,
        contentResolver = context.contentResolver,
        imageCapture = imageCapture,
        onSaved = { uri, filename ->
            if (uri != null) {
                onSaved(uri, filename)
            } else {
                onError("Failed to save photo")
            }
        },
        onError = { message, _ -> onError(message) },
    )
}

/**
 * Retrieves a dimension resource and converts it to [Dp].
 *
 * This helper reads a dimension value (e.g., defined in `dimens.xml`) using the provided resource
 * ID, then converts the pixel value returned by the Android framework into density-independent
 * pixels (Dp) suitable for Jetpack Compose layouts.
 *
 * @param context The [Context] used to access application resources and display metrics.
 * @param resId The resource ID of the dimension (e.g., `R.dimen.padding_large`).
 *
 * @return The dimension value converted to [Dp].
 */
private fun dimensionDp(context: Context, resId: Int): Dp {
    return (context.resources.getDimension(resId) / context.resources.displayMetrics.density).dp
}