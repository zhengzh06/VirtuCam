
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wangyiheng.VirtuCam.R
import com.wangyiheng.VirtuCam.components.DisclaimerDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("CoroutineCreationDuringComposition")
suspend fun checkIT(keyy: String, contextThisss: Context):Boolean {
    val fdb = Firebase.firestore
    val sharedPref: SharedPreferences

    try {
        val result = fdb.collection("VirtuCamKeys").document("$keyy").get().await()
        if (result.exists()) {
            val used = result.getBoolean("used") ?: true
            if (used) {
                Toast.makeText(contextThisss, "Key In USE, If its yours DM the Seller for Reset.", Toast.LENGTH_LONG).show()
                return false

            } else {
                fdb.collection("VirtuCamKeys").document("$keyy").update("used", true)
                sharedPref = contextThisss.getSharedPreferences("platform-info", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("KEY", keyy)
                editor.apply()
                return true
            }
        } else {
            Toast.makeText(contextThisss, "Wrong Key Configuration.", Toast.LENGTH_SHORT).show()
            return false
        }
    } catch (e: Exception) {
        return false
    }
    return false
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit = {}) {
    val contextThisss = LocalContext.current

    val sharedPreferences: SharedPreferences = contextThisss.getSharedPreferences("setts", Context.MODE_PRIVATE)
    val areTermsAccepted = sharedPreferences.getBoolean("terms", false)

    // Show the DisclaimerDialog only if terms are not accepted
    if (!areTermsAccepted) {
        DisclaimerDialog()
    }



    var isLoading by remember { mutableStateOf(false) }

    var key by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {


        Image(
            painter = painterResource(id = R.drawable._login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_white),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "VCAMSX",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Apply blur to the background behind the Column
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(10.dp)
        )

        // Dark overlay with gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x80000000),
                            Color(0xCC000000)
                        )
                    )
                )
        )

        // UI elements
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color(0xB22B2B2B))
                .padding(32.dp)
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Enter Unique Key",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = key,
                onValueChange = { key = it },
                placeholder = { Text("Unique Key", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x1EFFFFFF)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    //textColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (key.isEmpty()) {
                        errorMessage = "Key cannot be empty"
                        showDialog = true
                    } else {
                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            val isValid = checkIT(key, contextThisss)
                            isLoading = false
                            if (isValid) {
                                onLoginSuccess(key)
                            } else {
                                errorMessage = "Invalid Key!"
                                showDialog = true
                            }
                        }
                    }
                },
                enabled = !isLoading,

                modifier = Modifier
                    .height(60.dp)
                    .width(500.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Submit", fontSize = 18.sp)
            }
        }

        if (showDialog) {
            ErrorDialog(errorMessage) {
                showDialog = false
            }
        }
    }
}

@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C2C2C))
                .padding(16.dp)
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
