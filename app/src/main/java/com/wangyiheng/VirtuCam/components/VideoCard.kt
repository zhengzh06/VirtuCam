
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wangyiheng.VirtuCam.R
import com.wangyiheng.VirtuCam.components.ConfirmDeleteDialog
import com.wangyiheng.VirtuCam.data.models.VideoInfo
import com.wangyiheng.VirtuCam.utils.InfoManager
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VideoCardC : KoinComponent {
    val infoManager by inject<InfoManager>()
}
@Composable
fun VideoCard(
    videoInfo: VideoInfo,
    onRemoveClick: (VideoInfo) -> Unit,
    isSelected: Boolean,
    onCardClick: () -> Unit
) {
    val borderColor = if (isSelected) Color.Red else Color(0xFF25D47F) // Red if selected, green otherwise
    //val vc = VideoCardC()
    var isPressed by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val homeController = viewModel<HomeController>()


    ElevatedCard(
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = Modifier
            .padding(16.dp)
            .width(230.dp)
            .height(280.dp)
            .border(BorderStroke(3.dp, borderColor), shape = RoundedCornerShape(12.dp)) // Border with selected color
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isPressed = true
                        showDialog = true
                    },
                    onTap = { onCardClick() }
                )
            }
//            .graphicsLayer(
//                scaleX = scale,
//                scaleY = scale
//            ),
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background video thumbnail with gradient overlay
            videoInfo.videoThumbnail?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Image(
                    painter = painterResource(R.drawable.video_icon),
                    contentDescription = "Video Thumbnail Placeholder",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Gradient overlay from top to bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0x33000000), Color(0xFF000000)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Content on top of the gradient
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Duration at the top right
                Box()
                {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            text = videoInfo.videoDuration ?: "0:0",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Video information in the middle
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
//                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Size: ${videoInfo.videoSize} MB",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = Color(0xB3FFFFFF),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Resolution: ${videoInfo.videoId}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = Color(0xB3FFFFFF),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Creation Time: ${videoInfo.videoType}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = Color(0xB3FFFFFF),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            // Video name in bold above the button
                            Text(
                                text = videoInfo.videoName,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                //                            modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }


                }
            }

//            // Remove button at the top left
//            IconButton(
//                onClick = { onRemoveClick(videoInfo) },
//                modifier = Modifier
//                    .align(Alignment.TopStart)
//                    .padding(8.dp)
//            ) {
//                Image(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = "Remove Video",
//                    colorFilter = ColorFilter.tint(Color(0xFFFB7C5C)) // Bright delete icon color
//                )
//            }
        }
    }

    if (showDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                onRemoveClick(videoInfo)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)  // Delay to show the pressed state briefly
            isPressed = false
        }
    }
}
@Composable
fun Dp.toPx(): Float {
    val density = LocalDensity.current.density
    return this.value * density
}
