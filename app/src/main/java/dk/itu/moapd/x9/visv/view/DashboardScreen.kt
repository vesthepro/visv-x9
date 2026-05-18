package dk.itu.moapd.x9.visv.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.sensors.ShakeDetector
import dk.itu.moapd.x9.visv.viewmodels.ReportViewModel


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DashboardScreen(
    viewModel: ReportViewModel,
    auth: FirebaseAuth,
) {
    val reports by viewModel.reports.collectAsState()
    val context = LocalContext.current
    val currentUid = auth.currentUser?.uid ?: ""

    var showQuickReport by remember {mutableStateOf(false)}

    DisposableEffect(Unit) {
        val detector = ShakeDetector(context, onShake = {showQuickReport = true})
        detector.register()
        onDispose { detector.unregistor() }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reports, key = { it.key }) { report ->
                    val isOwner = report.uid == currentUid
                    val dismissState = rememberSwipeToDismissBoxState()

                    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart && isOwner) {
                        viewModel.deleteReport(report)
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = isOwner,
                        backgroundContent = {
                            if (isOwner) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 8.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = CardDefaults.elevatedShape
                                        ),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = stringResource(R.string.delete),
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                }
                            }
                        }
                    ) {
                        ReportItemCard(
                            title = report.reportTitle,
                            type = report.reportType,
                            severity = report.reportSeverity,
                            description = report.reportDescription,
                            imageUrl = report.photoUri,
                            latitude = report.latitude,
                            longitude = report.longitude,
                            locationTime = report.locationTime,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {}
        }
        if (showQuickReport) {
            QuickReportSheet(
                viewModel = viewModel,
                onDismiss = { showQuickReport = false }
            )
        }
    }
}

@Composable
fun ReportItemCard(
    title: String,
    type: String,
    severity: String,
    description: String,
    imageUrl: String? = null,
    latitude: String = "",
    longitude: String = "",
    locationTime: String = "",
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                if (latitude.isNotEmpty() && longitude.isNotEmpty()) {
                    Column(horizontalAlignment = Alignment.End) {
                        if (locationTime.isNotEmpty()) {
                            Text(
                                text = locationTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "%.4f, %.4f".format(
                                latitude.toDoubleOrNull() ?: 0.0,
                                longitude.toDoubleOrNull() ?: 0.0
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = severity,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            if (!imageUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                AsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(R.string.report_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}
fun requestOrStartTracking(
    context: android.content.Context,
    onHasPermission: () -> Unit,
    onRequestPermission: () -> Unit,
) {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    if (hasPermission) onHasPermission() else onRequestPermission()
}