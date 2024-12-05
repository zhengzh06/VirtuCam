package com.wangyiheng.VirtuCam

import LoginScreen
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wangyiheng.VirtuCam.components.MainScreen
import com.wangyiheng.VirtuCam.services.VcamsxForegroundService
import com.wangyiheng.VirtuCam.ui.theme.VCAMSXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext



class MainActivity : ComponentActivity() {

    private val notificationSettingsResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (areNotificationsEnabled()) {
            startForegroundService()
        }
    }
    private val sharedPref by lazy {
        getSharedPreferences("platform-info", Context.MODE_PRIVATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VCAMSXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Observe and manage the state for which screen to show
                    var showLoginScreen by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        val storedKey = sharedPref.getString("KEY", null)
                        if (storedKey != null) {
                            val isKeyValid = checkKey(storedKey)
                            showLoginScreen = !isKeyValid
                        } else {
                            showLoginScreen = true
                        }
                    }

                    if (showLoginScreen) {
                        LoginScreen(onLoginSuccess = { key ->
                            sharedPref.edit().putString("KEY", key).apply()
                            showLoginScreen = false
                        })
                    } else {
                        MainScreen()
                    }
                }
            }
        }
    }



    private suspend fun checkKey(keyValue: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val db = Firebase.firestore
                val document = db.collection("VirtuCamKeys").document(keyValue).get().await()
                if (document.exists()) {
                    val isUsed = document.getBoolean("used")
                    if (isUsed == true) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Key: $keyValue is valid ✅", Toast.LENGTH_LONG).show()
                        }
                        true
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Key: $keyValue is not pre-authorized ❌", Toast.LENGTH_LONG).show()
                        }
                        false
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Invalid key ❌", Toast.LENGTH_LONG).show()
                    }
                    false
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error getting key: ${e.message}", Toast.LENGTH_LONG).show()
                }
                false
            }
        }
    }


    private fun openNotificationSettings() {
        val intent = Intent().apply {
            action = "android.settings.APP_NOTIFICATION_SETTINGS"
            putExtra("android.provider.extra.APP_PACKAGE", packageName)
            putExtra("app_package", packageName)
            putExtra("app_uid", applicationInfo.uid)
        }
        notificationSettingsResult.launch(intent)
    }

    private fun startForegroundService() {
        VcamsxForegroundService.start(this)
    }

    private fun areNotificationsEnabled(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }
}