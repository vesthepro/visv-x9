package dk.itu.moapd.x9.visv.fragments.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.x9.visv.R
import dk.itu.moapd.x9.visv.databinding.FragmentDashboardBinding
import dk.itu.moapd.x9.visv.fragments.ui.list.CustomAdapter
import dk.itu.moapd.x9.visv.fragments.ui.model.ReportViewModel
import dk.itu.moapd.x9.visv.fragments.ui.utils.viewBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val binding by viewBinding(FragmentDashboardBinding::bind)

    private val viewModel: ReportViewModel by activityViewModels()
    private lateinit var adapter: CustomAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()


        binding.toReport.setOnClickListener {
            findNavController().navigate(
                R.id.action_dashboard_to_report)
        }
    }

    private fun setupRecyclerView() = with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())

            adapter = CustomAdapter(viewModel.reports)
            this.adapter = adapter

            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = navBarHeight
                }
                insets
            }
    }
}