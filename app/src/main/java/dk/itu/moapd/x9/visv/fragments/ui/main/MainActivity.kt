package dk.itu.moapd.x9.visv.fragments.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val navController =
            (supportFragmentManager.findFragmentById(R.id.fragment_container)
                    as NavHostFragment).navController*/
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