package dk.itu.moapd.x9.visv.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.itu.moapd.x9.visv.ui.theme.X9Theme
import dk.itu.moapd.x9.visv.viewmodels.ReportViewModel

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            X9Theme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(viewModel = viewModel, navController = navController)
                    }
                    composable("report") {
                        ReportScreen(viewModel = viewModel, navController = navController)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() method called.")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() method called.")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() method called.")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() method called.")
    }
}