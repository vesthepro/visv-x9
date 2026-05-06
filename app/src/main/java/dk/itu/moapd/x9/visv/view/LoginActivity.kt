package dk.itu.moapd.x9.visv.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dk.itu.moapd.x9.visv.R


class LoginActivity : ComponentActivity() {

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Login success -> go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    private fun createSignInIntent() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.x9_android_vector)
            .setTosAndPrivacyPolicyUrls(
                "https://firebase.google.com/terms/",
                "https://firebase.google.com/policies/"
            )
            .build()

        signInLauncher.launch(intent)
    }
}