package dk.itu.moapd.x9.visv.viewmodels

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

class ReportViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val reportsRef = database.getReference("reports")

    private val _reports = MutableStateFlow<List<ReportModel>>(emptyList())
    val reports: StateFlow<List<ReportModel>> = _reports

    init {
        listenToReports()
    }

    private fun listenToReports() {
        reportsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("DB", "RAW VALUE = ${snapshot.value}")

                val list = snapshot.children.mapNotNull {
                    it.getValue(ReportModel::class.java)
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
}