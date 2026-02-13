package dk.itu.moapd.x9.visv

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dk.itu.moapd.x9.visv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedSeverity: String? = null
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

        binding.sevMin.setOnClickListener { setSeverity("Minor") }
        binding.sevMod.setOnClickListener { setSeverity("Moderate") }
        binding.sevMaj.setOnClickListener { setSeverity("Major") }

        binding.submitRep.setOnClickListener {
            if (validateInputs()) {
                logInput()
            }
        }


    }
    private fun setSeverity(severity: String) {
        selectedSeverity = severity

        binding.sevMin.isSelected = severity == "Minor"
        binding.sevMod.isSelected = severity == "Moderate"
        binding.sevMaj.isSelected = severity == "Major"

    }

    private fun logInput() {
        val title = binding.repTitle.text.toString()
        val type = binding.repType.selectedItem?.toString() ?: "None"
        val desc = binding.repDesc.text.toString()

        val severity = selectedSeverity ?: "None"

        Log.d(TAG, "Button pressed:  $severity")
        Log.d(TAG, "Report title:  $title")
        Log.d(TAG, "Report type:  $type")
        Log.d(TAG, "Report description:  $desc")

    }
    private fun validateInputs(): Boolean {
        val title = binding.repTitle.text.toString().trim()
        val desc = binding.repDesc.text.toString().trim()

        var valid = true

        if (title.isEmpty()) {
            binding.repTitle.error = "Title cannot be empty"
            valid = false
        }
        if (desc.isEmpty()) {
            binding.repDesc.error = "Description cannot be empty"
            valid = false
        }
        if (selectedSeverity == null) {
            Toast.makeText(this, "Please select a severity", Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }
}