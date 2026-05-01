package dk.itu.moapd.x9.visv.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.visv.view.ui.theme.X9Theme
import dk.itu.moapd.x9.visv.viewmodels.ReportViewModel
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.core.preferences.LocationTrackingPreferences
import dk.itu.moapd.x9.visv.service.LocationService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = "MainActivity"
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel: ReportViewModel by viewModels()
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }
    private var locationService: LocationService? = null
    private var locationServiceBound: Boolean = false
    private var collectJob: Job? = null
    private var onLocationCallback: ((Location) -> Unit)? = null
    private var pendingStartTracking: Boolean = false
    private val serviceConnection = createLocationServiceConnection(
        onConnected = { service ->
            locationService = service
            locationServiceBound = true

            if (pendingStartTracking) {
                service.subscribeToLocationUpdates()
                pendingStartTracking = false
            }

            startCollectingIfReady()
        },
        onDisconnected = {
            locationService = null
            locationServiceBound = false
            collectJob?.cancel()
            collectJob = null
        },
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            X9Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = viewModel,
                            navController = navController,
                            auth = auth,
                            onStartTracking = {
                                if (locationServiceBound) {
                                    locationService?.subscribeToLocationUpdates()
                                } else {
                                    pendingStartTracking = true
                                    startLocationService()
                                }
                            },
                            onStopTracking = {
                                locationService?.unsubscribeToLocationUpdates()
                            },
                            onLocationReady = { callback ->
                                onLocationCallback = callback
                                startCollectingIfReady()
                            }
                        )
                    }
                    composable("report") {
                        ReportScreen(viewModel = viewModel, navController = navController)
                    }
                    composable("map") {
                        MapScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            return
        }
        // bind service

        Intent(this, LocationService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        //unbind service
        if (locationServiceBound) {
            unbindService(serviceConnection)
            locationServiceBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == LocationTrackingPreferences.KEY_TRACKING_ENABLED) {
            val enabled = LocationTrackingPreferences.isTrackingEnabled(this)
            if (!enabled) {
                collectJob?.cancel()
                collectJob = null
            } else {
                startCollectingIfReady()
            }
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    /**
     * Starts the collector if we have both the service bound and the composable's onLocation
     * callback available and tracking is enabled.
     */
    private fun startCollectingIfReady() {
        val isReady = onLocationCallback != null &&
                locationService != null &&
                LocationTrackingPreferences.isTrackingEnabled(this)

        if (!isReady) return

        collectJob?.cancel()
        collectJob = lifecycleScope.launch {
            locationService?.locationUpdates?.collect { location ->
                onLocationCallback?.invoke(location)
            }
        }
    }
}