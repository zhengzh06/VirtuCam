package com.wangyiheng.VirtuCam.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wangyiheng.VirtuCam.R

@Composable
fun PrecautionsSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
//            .padding(16.dp),
    ) {
        PrecautionCard(
            icon = R.drawable.ic_video_format,
            title = "Video Playback Format",
            description = "The video playback needs to be in the same format as the platform playback, supporting 9:16 videos, such as: 3840x2160, 1920x1080, 1280x720, 854x480, 640x360, 426x240, 256x144."
        )

        PrecautionCard(
            icon = R.drawable.ic_camera,
            title = "Camera Fails to Start",
            description = "The screen is black and the camera fails to start because there is a problem with the video decoding. Please click the flip camera several times."
        )

        PrecautionCard(
            icon = R.drawable.ic_flip,
            title = "Screen Flip Issues",
            description = "The screen is flipped and does not match the original video. The current video playback has not been adjusted. Please adjust the video manually."
        )

        PrecautionCard(
            icon = R.drawable.ic_decoding,
            title = "Decoding Modes",
            description = "Different software has different requirements for hardware decoding and software decoding. If there is only sound but no picture for many times, please switch the video decoding mode."
        )

        PrecautionCard(
            icon = R.drawable.ic_hard_soft,
            title = "Hard vs Soft Decoding",
            description = "Hard decoding is smoother than soft decoding. Please determine whether your phone model supports hard decoding. Soft decoding has higher adaptability and basically supports playback of all videos."
        )
    }
}

@Composable
fun PrecautionCard(icon: Int, title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x00FFFFFF))
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ){
//
//                Image(
//                    painter = painterResource(id = icon),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(50.dp)
//                        .padding(end = 16.dp)
//                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black

                )
            }
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF3A3A3A),
                textAlign = TextAlign.Left
            )
        }
    }
}
