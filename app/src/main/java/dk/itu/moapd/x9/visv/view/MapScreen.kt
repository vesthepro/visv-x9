package dk.itu.moapd.x9.visv.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.model.ReportModel
import dk.itu.moapd.x9.visv.viewmodels.ReportViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp


@Composable
fun MapScreen(viewModel: ReportViewModel) {
    val context = LocalContext.current
    val reports by viewModel.reports.collectAsState()

    // Check location permission
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    // Default to ITU if no location yet
    val itu = LatLng(55.6596, 12.5910)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(itu, 14f)
    }

    // Track which report marker was tapped
    var selectedReport by remember { mutableStateOf<ReportModel?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = hasPermission, // shows blue dot + my location button
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasPermission,
                zoomControlsEnabled = true,
            )
        ) {
            // ITU marker
            Marker(
                state = remember { MarkerState(position = itu) },
                title = stringResource(R.string.itu_title),
            )

            // Report markers from Firebase
            reports.forEach { report ->
                val lat = report.latitude.toDoubleOrNull()
                val lng = report.longitude.toDoubleOrNull()

                if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                    val position = LatLng(lat, lng)
                    Marker(
                        state = remember(report.key) { MarkerState(position = position) },
                        title = report.reportTitle,
                        snippet = "${report.reportSeverity} • ${report.reportType}",
                        onClick = {
                            selectedReport = report
                            false
                        }
                    )
                }
            }
        }

        // Detail card shown when a marker is tapped
        selectedReport?.let { report ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = report.reportTitle,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = report.reportSeverity,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = report.reportType,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.reportDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📍 ${report.latitude}, ${report.longitude}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { selectedReport = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}