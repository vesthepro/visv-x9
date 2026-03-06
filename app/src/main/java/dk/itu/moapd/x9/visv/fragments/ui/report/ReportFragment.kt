package dk.itu.moapd.x9.visv.fragments.ui.report

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.databinding.FragmentReportBinding
import dk.itu.moapd.x9.visv.fragments.ui.model.ReportModel
import dk.itu.moapd.x9.visv.fragments.ui.model.ReportViewModel
import dk.itu.moapd.x9.visv.fragments.ui.utils.viewBinding


class ReportFragment : Fragment(R.layout.fragment_report) {

    private val binding by viewBinding(FragmentReportBinding::bind)
    private var selectedSeverity: String? = null
    private val TAG = "ReportFragment"
    private val viewModel: ReportViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack() //pobackstack :)
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

        val report = ReportModel(
            reportTitle = title,
            reportType = type,
            reportSeverity = severity,
            reportDescription = desc
        )

        viewModel.reports.add(report)

        Log.d(TAG, "Received report:")
        Log.d(TAG, "Title:  $title")
        Log.d(TAG, "Type: $type")
        Log.d(TAG, "description: $desc")
        Log.d(TAG, "severity: $severity")

        Toast.makeText(requireContext(), "Report received: $title", Toast.LENGTH_LONG).show()
        findNavController().popBackStack() //pobackstack :)
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