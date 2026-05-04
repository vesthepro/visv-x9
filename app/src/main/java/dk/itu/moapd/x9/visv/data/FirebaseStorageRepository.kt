package dk.itu.moapd.x9.visv.data

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object FirebaseStorageRepository {

    private val storage = FirebaseStorage.getInstance()

    fun uploadImage(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "reports/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)

        val stream = context.contentResolver.openInputStream(uri)

        if (stream == null) {
            onFailure(Exception("Cannot open image stream"))
            return
        }

        ref.putStream(stream)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Upload failed")
                }
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}