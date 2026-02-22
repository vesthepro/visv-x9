package dk.itu.moapd.x9.visv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dk.itu.moapd.x9.visv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private val TAG = "MainActivity"

    private val reportLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val title = data?.getStringExtra("title")
                val type = data?.getStringExtra("type")
                val desc = data?.getStringExtra("description")
                val severity = data?.getStringExtra("severity")

                Log.d(TAG, "Received report:")
                Log.d(TAG, "Title:  $title")
                Log.d(TAG, "Type: $type")
                Log.d(TAG, "description: $desc")
                Log.d(TAG, "severity: $severity")

                Toast.makeText(this, "Report received: $title", Toast.LENGTH_LONG).show()
            }
        }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            reportLauncher.launch(intent)
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