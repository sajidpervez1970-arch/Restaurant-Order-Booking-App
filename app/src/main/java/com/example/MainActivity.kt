package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AppMainScreen
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels {
        AppViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val useDarkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppMainScreen(
                        viewModel = viewModel,
                        onLaunchWhatsApp = { intentData ->
                            launchWhatsApp(this, intentData.phoneNumber, intentData.message)
                        }
                    )
                }
            }
        }
    }

    private fun launchWhatsApp(context: Context, phone: String, message: String) {
        try {
            // Standard WhatsApp URL format: phone number should have the country code without plus sign
            val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
            val encodedMessage = Uri.encode(message)
            val uriString = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(uriString)
                // Try to set WhatsApp package specifically if available, but keep it generic
                // so browser redirect can handle it if the native app is not present.
                setPackage("com.whatsapp")
            }
            
            try {
                context.startActivity(intent)
            } catch (ex: Exception) {
                // Fallback to generic action view without package restriction (opens in browser)
                val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(uriString)
                }
                context.startActivity(fallbackIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Could not launch WhatsApp. Please make sure WhatsApp is installed.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
