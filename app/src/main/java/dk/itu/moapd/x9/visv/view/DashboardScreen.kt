package dk.itu.moapd.x9.visv.view

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.viewmodels.ReportViewModel


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DashboardScreen(
    viewModel: ReportViewModel,
    navController: NavController,
    auth: FirebaseAuth,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onLocationReady: ((Location) -> Unit) -> Unit,
) {
    val reports by viewModel.reports.collectAsState()
    val context = LocalContext.current
    val currentUid = auth.currentUser?.uid ?: ""


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
           onStartTracking()
        }
    }
    LaunchedEffect(Unit) {
        onLocationReady { location ->
            Log.d("Location", "Lat: ${location.latitude}, Lng: ${location.longitude}")
            viewModel.updateLocation(context, location)
            // you can pass this to the viewModel if needed
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = "X9",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
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
                                        text = "Delete",
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
                            description = report.reportDescription
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("report") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("To Report")
                }
                // GPS button
                Button(
                    onClick = {
                        requestOrStartTracking(
                            context = context,
                            onHasPermission = {
                                onStartTracking()
                                Log.d("Location", "Started tracking")
                                              },
                            onRequestPermission = {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start Tracking")
                }
                Button(
                    onClick = {
                        onStopTracking()
                        Log.d("Location", "Stopped tracking")
                              },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Stop")
                }
                Button(
                    onClick = { navController.navigate("map") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Map")
                }
            }
        }
        Button(
            onClick = {
                auth.signOut()

                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}

@Composable
fun ReportItemCard(
    title: String,
    type: String,
    severity: String,
    description: String
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = severity, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                Text(text = type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
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