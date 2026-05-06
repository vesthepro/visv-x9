package dk.itu.moapd.x9.visv.viewmodels

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.x9.visv.model.ReportModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dk.itu.moapd.x9.visv.mapper.fieldsFromLocation
import dk.itu.moapd.x9.visv.model.CurrentLocation

class ReportViewModel : ViewModel() {
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _selectedSeverity = MutableStateFlow<String?>(null)
    val selectedSeverity = _selectedSeverity.asStateFlow()

    private val _selectedType = MutableStateFlow("None")
    val selectedType = _selectedType.asStateFlow()

    private val _photoUri = MutableStateFlow<String?>(null)
    val photoUri = _photoUri.asStateFlow()

    private val database = FirebaseDatabase.getInstance()
    private val reportsRef = database.getReference("reports")

    private val _reports = MutableStateFlow<List<ReportModel>>(emptyList())
    val reports: StateFlow<List<ReportModel>> = _reports
    private val _currentLocation = MutableStateFlow<CurrentLocation?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    init {
        listenToReports()
    }

    private fun listenToReports() {
        reportsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("DB", "RAW VALUE = ${snapshot.value}")

                val list = snapshot.children.mapNotNull {
                    it.getValue(ReportModel::class.java)?.copy(key = it.key ?: "")
                }
                Log.d("DB", "PARSED SIZE = ${list.size}")

                _reports.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DB", "ERROR: ${error.message}")
            }
        })
    }

    fun addReport(report: ReportModel) {
        val reportId = reportsRef.push().key ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

        val reportToSave = report.copy(
            createdAt = System.currentTimeMillis(),
            uid = uid
        )

        reportsRef.child(reportId).setValue(reportToSave)
    }

    fun deleteReport(report: ReportModel) {
        if (report.key.isBlank()) return
        reportsRef.child(report.key).removeValue()
    }
    fun updateLocation(context: Context, location: Location) {
        _currentLocation.value = fieldsFromLocation(context, location)
    }
    fun updateTitle(value: String) {
        _title.value = value
    }

    fun updateDescription(value: String) {
        _description.value = value
    }

    fun updateSeverity(value: String?) {
        _selectedSeverity.value = value
    }

    fun updateType(value: String) {
        _selectedType.value = value
    }

    fun updatePhoto(uri: String?) {
        _photoUri.value = uri
    }
    fun clearForm() {
        _title.value = ""
        _description.value = ""
        _selectedSeverity.value = null
        _selectedType.value = "None"
        _photoUri.value = null
    }
}