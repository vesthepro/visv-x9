package dk.itu.moapd.x9.visv.viewmodels

import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.visv.model.ReportModel

class ReportViewModel : ViewModel() {
    val reports = mutableListOf<ReportModel>()
}