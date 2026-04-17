package dk.itu.moapd.x9.visv.model

data class ReportModel(
    val reportTitle: String = "",
    val reportType: String = "",
    val reportSeverity: String = "",
    val reportDescription: String = "",
    val createdAt: Long = 0L,
    val uid: String = ""
)