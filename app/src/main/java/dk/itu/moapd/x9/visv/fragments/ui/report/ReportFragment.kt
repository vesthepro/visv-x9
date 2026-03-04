package dk.itu.moapd.x9.visv.fragments.ui.report

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.databinding.FragmentReportBinding
import dk.itu.moapd.x9.visv.fragments.ui.utils.viewBinding


class ReportFragment : Fragment(R.layout.fragment_report) {

    private val binding by viewBinding(FragmentReportBinding::bind)

    private var selectedSeverity: String? = null

    private val TAG = "ReportFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_report_to_dashboard) //pobackstack :)
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

        Log.d(TAG, "Received report:")
        Log.d(TAG, "Title:  $title")
        Log.d(TAG, "Type: $type")
        Log.d(TAG, "description: $desc")
        Log.d(TAG, "severity: $severity")

        Toast.makeText(requireContext(), "Report received: $title", Toast.LENGTH_LONG).show()
        findNavController().navigate(
            R.id.action_report_to_dashboard) //pobackstack :)
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
            Toast.makeText(requireContext(), "Please select a severity", Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }
}