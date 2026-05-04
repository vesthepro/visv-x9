package dk.itu.moapd.x9.visv.model

data class ReportModel(
    val key: String = "",
    val reportTitle: String = "",
    val reportType: String = "",
    val reportSeverity: String = "",
    val reportDescription: String = "",
    val createdAt: Long = 0L,
    val uid: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val altitude: String = "",
    val speed: String = "",
    val locationTime: String = "",
    val photoUri: String = "",
)