

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddVideoButton(isLiveStream: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .height(45.dp)
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        androidx.compose.material3.Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if(isLiveStream) Color(0xff01d4ff) else Color(0xff25d47f),
                contentColor = Color(0xff090a09)
            ),
            contentPadding = PaddingValues(0.dp), // Remove default padding
            shape = RoundedCornerShape(20)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLiveStream) "View Live Stream" else "+ Add Video",
                    color = Color(0xff090a09)
                )
            }
        }
    }
}
