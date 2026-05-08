package dk.itu.moapd.x9.visv.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.model.ReportModel
import dk.itu.moapd.x9.visv.viewmodels.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickReportSheet(
    viewModel: ReportViewModel,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val none = stringResource(id = R.string.none)

    var selectedType by remember {mutableStateOf(none)}
    var selectedSeverity by remember {mutableStateOf<String?>(null)}
    var severityError by remember {mutableStateOf(false)}
    var dropdownExpanded by remember {mutableStateOf(false)}

    val reportTypes = stringArrayResource(id = R.array.report_types)
    val severities = stringArrayResource(id = R.array.severities)
    val currentLocation by viewModel.currentLocation.collectAsState()
    val filedShakeGesture = stringResource(R.string.filed_via_shake_gesture)
    val quickRepType = stringResource(R.string.quick_report_type, selectedType)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.quick_report), style = MaterialTheme.typography.headlineSmall)
            Text(
                stringResource(R.string.shake_detected),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it}
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.type))},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = {dropdownExpanded = false}
                ) {
                    reportTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = { selectedType = type; dropdownExpanded = false }
                        )
                    }
                }
            }

            Column {
                Text(
                    text = stringResource(R.string.severity),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (severityError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    severities.forEach { severity ->
                        val isSelected = selectedSeverity == severity
                        Button(
                            onClick = { selectedSeverity= severity; severityError = false},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) { Text(severity) }
                    }
                }
                if (severityError) {
                    Text(
                        stringResource(R.string.please_select_a_severity),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Button(
                onClick = {
                    if (selectedSeverity == null) {
                        severityError = true
                        return@Button
                    }
                    viewModel.addReport(
                        ReportModel(
                            reportTitle = quickRepType,
                            reportType = selectedType,
                            reportSeverity = selectedSeverity!!,
                            reportDescription = filedShakeGesture,
                            latitude = currentLocation?.latitude ?: "",
                            longitude = currentLocation?.longitude ?: "",
                            altitude = currentLocation?.altitude ?: "",
                            speed = currentLocation?.speed ?: "",
                            locationTime = currentLocation?.time ?: "",
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.submit_quick_report)) }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.cancel)) }
        }
    }
}