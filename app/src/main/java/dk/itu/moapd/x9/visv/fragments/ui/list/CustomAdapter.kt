package dk.itu.moapd.x9.visv.fragments.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.visv.databinding.RowItemBinding
import dk.itu.moapd.x9.visv.fragments.ui.list.CustomAdapter.ViewHolder
import dk.itu.moapd.x9.visv.fragments.ui.model.ReportModel

class CustomAdapter(private val data: List<ReportModel>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false).let(::ViewHolder)

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data[position].let(holder::bind)
        }


        override fun getItemCount() = data.size



        class ViewHolder(private val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ReportModel) =
            with(binding) {
                reportTitle.text  = model.reportTitle
                reportType.text = model.reportType
                reportSeverity.text = model.reportSeverity
                reportDescription.text = model.reportDescription
            }
    }


}
