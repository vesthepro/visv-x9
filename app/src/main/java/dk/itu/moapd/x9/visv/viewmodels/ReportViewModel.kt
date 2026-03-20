package dk.itu.moapd.x9.visv.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.visv.model.ReportModel

class ReportViewModel : ViewModel() {
    private val _reports = MutableLiveData<List<ReportModel>>(emptyList())
    val reports: LiveData<List<ReportModel>> = _reports
    fun addReport(report: ReportModel) {
        _reports.value = _reports.value.orEmpty() + report
    }

}