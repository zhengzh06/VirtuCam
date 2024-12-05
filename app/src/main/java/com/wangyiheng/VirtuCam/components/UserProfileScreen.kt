package com.wangyiheng.VirtuCam.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertTimestampToString(timestamp: Timestamp): String {
    // Convert Firebase Timestamp to Date
    val date: Date = timestamp.toDate()

    // Define the date format
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Format the date to the desired string format
    return dateFormat.format(date)
}


suspend fun getNameAndExpiry(k: String): Pair<String?, Timestamp?>? {
    // Initialize Firestore
    val db = Firebase.firestore

    try {
        // Access the collection and document
        val documentSnapshot = db.collection("VirtuCamKeys").document("$k").get().await()

        // Check if the document exists
        if (documentSnapshot.exists()) {
            // Retrieve the "name" and "expiry" fields
            val name = documentSnapshot.getString("SellerName")
            val expiry = documentSnapshot.getTimestamp("expiry")

            // Return the values as a Pair
            return Pair(name, expiry)
        } else {
            // Document does not exist
            return null
        }
    } catch (e: Exception) {
        // Handle any errors
        e.printStackTrace()
        return null
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen() {
    var name: String? = null
    var expiry: Timestamp? = null
    var exppp: String? = null
    val sharedPref: SharedPreferences = LocalContext.current.getSharedPreferences("platform-info", Context.MODE_PRIVATE)
    val storedKey = sharedPref.getString("KEY", null)
    runBlocking {
        // Call the function and assign the result
        if(storedKey != null){
            val result = getNameAndExpiry(storedKey)
            name = result?.first
            expiry = result?.second
        }

    }
    if(expiry != null) {
        exppp = convertTimestampToString(expiry!!)
    }

    // State to track whether the key is revealed or hidden
    val isKeyRevealed = remember { mutableStateOf(false) }

    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF))
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            // User info card
            item {
                Text(
                    text = "User Information",
                    fontSize = 18.sp,
                    color = Color(0xFF000000), // Light color for headings
                    fontWeight = FontWeight.Bold
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                // Adjusted the padding and background to make the corners visible
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            color = Color(0xffEFEEF6) // Slightly lighter dark color for card background
                        )
                        .padding(16.dp) // Padding inside the rounded background
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), // Padding inside the row for spacing
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Unique Key",
                            color = Color(0xff000000) // Slightly dimmed color for labels
                        )
                        Text(
                            text = if (isKeyRevealed.value) "$storedKey" else "Click to reveal",
                            modifier = Modifier
                                .clickable {
                                    isKeyRevealed.value = !isKeyRevealed.value
                                },
                            color = if (isKeyRevealed.value) Color(0xff000000) else Color(0xFF929292), // Use lighter color when revealed
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Divider(
                        color = Color(0x16000000),
                        thickness = 2.dp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Expiry Date",
                            color = Color(0xff000000) // Same dimmed color for consistency
                        )
                        Text(
                            text = "$exppp",
                            color = Color(0xff000000) // Light color for values
                        )
                    }
                    Divider(
                        color = Color(0x16000000),
                        thickness = 2.dp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Seller Name",
                            color = Color(0xff000000)
                        )
                        Text(
                            text = "$name",
                            color = Color(0xff000000)
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Text(
                    text = "Precautions",
                    fontSize = 18.sp,
                    color = Color(0xFF000000), // Light color for headings
                    fontWeight = FontWeight.Bold
                )
            }
//            item { Spacer(modifier = Modifier.height(16.dp)) }
            item{ PrecautionsSection() }
        }
    }
}
