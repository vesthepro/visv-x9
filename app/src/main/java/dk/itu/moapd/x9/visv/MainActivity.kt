package dk.itu.moapd.x9.visv

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dk.itu.moapd.x9.visv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

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
        binding.sevMin.setOnClickListener { logInput("Minor") }
        binding.sevMod.setOnClickListener { logInput("Moderate") }
        binding.sevMaj.setOnClickListener { logInput("Major") }
    }

    private fun logInput(severity: String) {
        val title = binding.repTitle.text.toString()
        val type = binding.repType.selectedItem?.toString() ?: "None"
        val desc = binding.repDesc.text.toString()

        Log.d(TAG, "Button pressed:  $severity")
        Log.d(TAG, "Report title:  $title")
        Log.d(TAG, "Report type:  $type")
        Log.d(TAG, "Report description:  $desc")

    }
}