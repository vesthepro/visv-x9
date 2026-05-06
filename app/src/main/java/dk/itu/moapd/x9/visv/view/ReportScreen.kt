package dk.itu.moapd.x9.visv.view


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dk.itu.moapd.x9.visv.viewmodels.ReportViewModel
import dk.itu.moapd.x9.visv.model.ReportModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import dk.itu.moapd.x9.visv.data.FirebaseStorageRepository
import dk.itu.moapd.x9.visv.permissions.CameraPermissionHelper
import androidx.core.net.toUri
import dk.itu.moapd.x9.visv.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(viewModel: ReportViewModel, navController: NavController) {
    val context = LocalContext.current
    val currentLocation by viewModel.currentLocation.collectAsState()

    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val selectedSeverity by viewModel.selectedSeverity.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val capturedImageUri by viewModel.photoUri.collectAsState()

    var titleError by remember { mutableStateOf<String?>(null) }
    var descError by remember { mutableStateOf<String?>(null) }
    var severityError by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val reportTypes = stringArrayResource(id = R.array.report_types)
    val severities = stringArrayResource(id = R.array.severities)
    val permissionDeniedText = stringResource(id = R.string.camera_permission_required)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) navController.navigate("camera")
        else Toast.makeText(context,
            permissionDeniedText, Toast.LENGTH_SHORT).show()
    }

    // Receive photo from CameraScreen
    val navBackStackEntry = navController.currentBackStackEntry
    val savedUriString = navBackStackEntry
        ?.savedStateHandle
        ?.get<String>("photo_uri")

    LaunchedEffect(savedUriString) {
        savedUriString?.let {
            viewModel.updatePhoto(it)
            navBackStackEntry.savedStateHandle.remove<String>("photo_uri")
        }
    }

    fun validateAndSubmit() {
        titleError = if (title.trim().isEmpty()) "Title cannot be empty" else null
        descError = if (description.trim().isEmpty()) "Description cannot be empty" else null
        severityError = selectedSeverity == null

        if (titleError != null || descError != null || severityError) return

        isSubmitting = true
        val imageUri = capturedImageUri?.toUri()

        fun onSuccess() {
            viewModel.clearForm()
            Toast.makeText(context, "Report received: $title", Toast.LENGTH_LONG).show()
            isSubmitting = false
            navController.popBackStack()
        }

        fun onFailure() {
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
            isSubmitting = false
        }

        if (imageUri != null) {

            FirebaseStorageRepository.uploadImage(
                context = context,
                uri = imageUri,
                onSuccess = { downloadUrl ->

                    val report = ReportModel(
                        reportTitle = title.trim(),
                        reportType = selectedType,
                        reportSeverity = selectedSeverity ?: "None",
                        reportDescription = description.trim(),
                        latitude = currentLocation?.latitude ?: "",
                        longitude = currentLocation?.longitude ?: "",
                        altitude = currentLocation?.altitude ?: "",
                        speed = currentLocation?.speed ?: "",
                        locationTime = currentLocation?.time ?: "",
                        photoUri = downloadUrl
                    )

                    viewModel.addReport(report)
                    onSuccess()
                },
                onFailure = {
                    onFailure()
                }
            )

        } else {

            val report = ReportModel(
                reportTitle = title.trim(),
                reportType = selectedType,
                reportSeverity = selectedSeverity ?: "None",
                reportDescription = description.trim(),
                latitude = currentLocation?.latitude ?: "",
                longitude = currentLocation?.longitude ?: "",
                altitude = currentLocation?.altitude ?: "",
                speed = currentLocation?.speed ?: "",
                locationTime = currentLocation?.time ?: "",
                photoUri = ""
            )

            viewModel.addReport(report)
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "New Report",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = {
                viewModel.updateTitle(it)
                titleError = null
            },
            label = { Text("Title") },
            isError = titleError != null,
            supportingText = { titleError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Type dropdown
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                reportTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            viewModel.updateType(type)
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Severity
        Column {
            Text(
                text = "Severity",
                color = if (severityError)
                    MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                severities.forEach { severity ->
                    val isSelected = selectedSeverity == severity

                    Button(
                        onClick = {
                            viewModel.updateSeverity(severity)
                            severityError = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(severity)
                    }
                }
            }
        }

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = {
                viewModel.updateDescription(it)
                descError = null
            },
            label = { Text("Description") },
            isError = descError != null,
            supportingText = { descError?.let { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        // Photo preview
        if (capturedImageUri != null) {
            AsyncImage(
                model = capturedImageUri,
                contentDescription = "Attached photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        // Camera button
        OutlinedButton(
            onClick = {
                if (CameraPermissionHelper.hasCameraPermission(context)) {
                    navController.navigate("camera")
                } else {
                    permissionLauncher.launch(CameraPermissionHelper.CAMERA_PERMISSION)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (capturedImageUri == null) "Add Picture" else "Retake Picture")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }

            Button(
                onClick = { validateAndSubmit() },
                enabled = !isSubmitting,
                modifier = Modifier.weight(1f)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.submit))
                }
            }
        }
    }
}