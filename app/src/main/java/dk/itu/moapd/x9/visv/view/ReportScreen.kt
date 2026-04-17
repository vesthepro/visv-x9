package dk.itu.moapd.x9.visv.view

import android.util.Log
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

private const val TAG = "ReportScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(viewModel: ReportViewModel, navController: NavController) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSeverity by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf("None") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var descError by remember { mutableStateOf<String?>(null) }
    var severityError by remember { mutableStateOf(false) }

    val reportTypes = listOf("None", "Heavy traffic", "Crash", "Speed camera", "Road incident", "Other")
    val severities = listOf("Minor", "Moderate", "Major")

    fun validateAndSubmit() {
        titleError = if (title.trim().isEmpty()) "Title cannot be empty" else null
        descError = if (description.trim().isEmpty()) "Description cannot be empty" else null
        severityError = selectedSeverity == null

        if (severityError) {
            Toast.makeText(context, "Please select a severity", Toast.LENGTH_SHORT).show()
        }

        if (titleError == null && descError == null && !severityError) {
            val report = ReportModel(
                reportTitle = title.trim(),
                reportType = selectedType,
                reportSeverity = selectedSeverity ?: "None",
                reportDescription = description.trim()
            )
            viewModel.addReport(report)

            Log.d(TAG, "Received report:")
            Log.d(TAG, "Title: $title")
            Log.d(TAG, "Type: $selectedType")
            Log.d(TAG, "Description: $description")
            Log.d(TAG, "Severity: $selectedSeverity")

            Toast.makeText(context, "Report received: $title", Toast.LENGTH_LONG).show()
            navController.popBackStack()
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
            color = MaterialTheme.colorScheme.onSurface
        )

        // Title field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; titleError = null },
            label = { Text("Title") },
            isError = titleError != null,
            supportingText = { if (titleError != null) Text(titleError!!) },
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
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
                        onClick = { selectedType = type; dropdownExpanded = false }
                    )
                }
            }
        }

        // Severity selector
        Column {
            Text(
                text = "Severity",
                style = MaterialTheme.typography.labelLarge,
                color = if (severityError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                severities.forEach { severity ->
                    val isSelected = selectedSeverity == severity
                    Button(
                        onClick = { selectedSeverity = severity; severityError = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(severity)
                    }
                }
            }
        }

        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it; descError = null },
            label = { Text("Description") },
            isError = descError != null,
            supportingText = { if (descError != null) Text(descError!!) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons
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
                modifier = Modifier.weight(1f)
            ) {
                Text("Submit")
            }
        }
    }
}