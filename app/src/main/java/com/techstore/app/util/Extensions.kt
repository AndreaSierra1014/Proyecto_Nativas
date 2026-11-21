package com.techstore.app.util

import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.techstore.app.R
import java.text.NumberFormat
import java.util.Locale

fun Double.toCOP(): String =
    NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(this)

fun TextView.setPrice(price: Double) {
    text = price.toCOP()
}

fun ShapeableImageView.loadImage(url: String) {
    load(url) {
        placeholder(R.drawable.ic_launcher)
        error(R.drawable.ic_launcher)
    }
}

fun FragmentActivity.showBiometricPrompt(
    context: Context,
    title: String,
    subtitle: String,
    negativeButtonText: String,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {
    val prompt = BiometricPrompt(
        this,
        ContextCompat.getMainExecutor(context),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onAuthSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onAuthError(errString.toString())
            }
        }
    )
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setNegativeButtonText(negativeButtonText)
        .build()
    prompt.authenticate(promptInfo)
}
