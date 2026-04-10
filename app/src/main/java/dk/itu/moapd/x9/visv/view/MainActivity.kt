package dk.itu.moapd.x9.visv.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.visv.view.ui.theme.X9Theme
import dk.itu.moapd.x9.visv.viewmodels.ReportViewModel

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private lateinit var auth: FirebaseAuth
    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        setContent {
            X9Theme {

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(viewModel = viewModel, navController = navController, auth = auth)
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

        auth.currentUser ?: startLoginActivity()
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
    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }
}