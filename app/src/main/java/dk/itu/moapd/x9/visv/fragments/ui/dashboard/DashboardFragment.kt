package dk.itu.moapd.x9.visv.fragments.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.databinding.FragmentDashboardBinding
import dk.itu.moapd.x9.visv.fragments.ui.utils.viewBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val binding by viewBinding(FragmentDashboardBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toReport.setOnClickListener {
            findNavController().navigate(
                R.id.action_dashboard_to_report)
        }
    }



}