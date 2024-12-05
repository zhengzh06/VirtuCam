
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wangyiheng.VirtuCam.R
import com.wangyiheng.VirtuCam.components.LivePlayerDialog
import com.wangyiheng.VirtuCam.components.VideoPlayerDialog
import com.wangyiheng.VirtuCam.data.models.VideoInfo



@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val homeController = viewModel<HomeController>()
    var selectedVideo by remember { mutableStateOf<VideoInfo?>(null) }
    val vc = VideoCardC()
    LaunchedEffect(Unit) {
        homeController.init()
    }

    val selectVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("HomeScreen", "Video URI: $uri")
            homeController.copyVideoToAppDir(context, uri)
        } else {
            Log.d("HomeScreen", "No video selected.")
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("HomeScreen", "Permission granted, launching video picker")
            selectVideoLauncher.launch("video/*")
        } else {
            Log.d("HomeScreen", "Permission denied")
            Toast.makeText(context, "Permission is required to select videos.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //var selectedVideo by remember { mutableStateOf<VideoInfo?>(null) }

    LazyColumn(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        item {
            Column {
                // Custom Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_telegram_logo),
                        contentDescription = "logo",
                        Modifier.width(100.dp)
                    )
                    IconButton(
                        onClick = {
                            val url = "https://t.me/lab_110"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                            // Handle logo click if needed
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(id = R.drawable.ic_telegram), // Replace with your logo resource ID
                            contentDescription = "Brand Logo",
                            tint = Color.Unspecified // Use this if your logo has its own colors
                        )
                    }
                }

                // Add to stream section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextField(
                        value = homeController.liveURL.value,
                        onValueChange = { homeController.liveURL.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    )
                    IconButton(
                        onClick = {
                            homeController.saveState()
                            Toast.makeText(context, "State saved successfully", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(55.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    bottomStart = 0.dp,
                                    topEnd = 12.dp,
                                    bottomEnd = 12.dp
                                )
                            )
                            .background(Color(0xff25d47f))
                    ) {

                        Text(text = "Save", fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    AddVideoButton(
//                        isLiveStream = true,
//                        onClick = {
//                            homeController.isLiveStreamingDisplay.value = true
//                        },
//                        modifier = Modifier.weight(1f)
//                    )

                    AddVideoButton(
                        isLiveStream = false,
                        onClick = {
                            when {
                                Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
                                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }

                                else -> {
                                    selectVideoLauncher.launch("video/*")
                                }
                            }
                            Toast.makeText(context, "Opening video picker...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(
                    color = Color(0xFF000000),
                    thickness = (1.5).dp,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
            }
        }

        // Video Cards Section

        item {
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxSize(),
                content = {
                    if (homeController.videoList.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_add_video),
                                    contentDescription = "Add Video",
                                    Modifier.size(100.dp)
                                )
                                Text(
                                    text = "Add a new video",
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = Color(0xFF9EA3AD),
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Italic
                                    )
                                )
                            }
                        }
                    }

                    else {

                        items(homeController.videoList) { videoInfo ->

                            VideoCard(
                                videoInfo = videoInfo,
//                                onRemoveClick = { video ->
//                                    homeController.removeVideo(video)
//                                    if (selectedVideo == video) {
//                                        selectedVideo = null
//                                    }
//                                },
                                onRemoveClick = { video ->
                                    homeController.removeVideo(video)
                                    if (homeController.selectedVideo.value == video) {
                                        homeController.selectedVideo.value = null
                                    }
                                },
//                                isSelected = selectedVideo == videoInfo,

                                isSelected = homeController.selectedVideo.value == videoInfo,
                                onCardClick = {
                                    homeController.selectedVideo.value = videoInfo
                                    selectedVideo = videoInfo
                                    vc.infoManager.getVideoInfo()
                                    vc.infoManager.saveVideoInfo(videoInfo)
                                    homeController.infoManager.removeVideoInfo()
                                    homeController.infoManager.saveVideoInfo(videoInfo)
                                    //homeController.copyVideoToAppDir2(context,selectedVideo.videoUrl)
                                    Toast.makeText(context, "ADDED NEW VIDEO", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                })
            Button(
                onClick = {
                    val videoInfo = vc.infoManager.getVideoInfo()

                    if (videoInfo != null) {
                        val videoUri = videoInfo.videoUrl
                        Log.d("HS_ADEBUG", "THIS IS VID: " + videoUri)
                        homeController.setVideoToPlay(videoUri)
                        homeController.isVideoDisplay.value = true
                    }
//                    selectedVideo?.let {
//                        homeController.setVideoToPlay(it.videoUrl)
//                        homeController.isVideoDisplay.value = true
//                    }
                },
                modifier = Modifier
//                    .fillMaxWidth()
                    .width(200.dp)
                    .padding(16.dp),
                enabled = selectedVideo != null,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D47F), // Green color
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Check",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Column {
                Divider(
                    color = Color(0xFF000000),
                    thickness = (1.5).dp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )

                // Chip Set
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {


                    Chip(
                        text = "Audio",
                        isSelected = homeController.isVolumeEnabled.value,
                        onClick = {
                            homeController.isVolumeEnabled.value =
                                !homeController.isVolumeEnabled.value
                            homeController.saveState()
                        }
                    )
                    Chip(
                        text = "Video",
                        isSelected = homeController.isVideoEnabled.value,
                        onClick = {
                            homeController.isVideoEnabled.value =
                                !homeController.isVideoEnabled.value
                            homeController.saveState()
                        }
                    )
                    Chip(
                        text = "Live Stream",
                        isSelected = homeController.isLiveStreamingEnabled.value,
                        onClick = {
                            homeController.isLiveStreamingEnabled.value =
                                !homeController.isLiveStreamingEnabled.value
                            homeController.saveState()
                        }
                    )
                    Chip(
                        text = "Soft Decoding",
                        isSelected = !homeController.codecType.value,
                        onClick = {
                            if (!homeController.codecType.value) {
                                Toast.makeText(context, "Soft decoding is already enabled", Toast.LENGTH_SHORT).show()
                            } else {
                                homeController.codecType.value = false
                                homeController.saveState()
                            }
                        }
                    )

                    Chip(
                        text = "Hard Decoding",
                        isSelected = homeController.codecType.value,
                        onClick = {
                            if (homeController.isH264HardwareDecoderSupport()) {
                                if (homeController.codecType.value){
                                    Toast.makeText(context, "Hard decoding is already enabled", Toast.LENGTH_SHORT).show()
                                }else {
                                    homeController.codecType.value = true
                                    homeController.saveState()
                                }
                            } else {
                                Toast.makeText(context, "Hard decoding is not supported on this device", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )


                }
            }
        }

        item { LivePlayerDialog(homeController) }
        item { VideoPlayerDialog(homeController) }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(55.dp)
            .background(Color(0xFFFFFFFF))
            .border(
                width = (1.5).dp,
                color = Color(0xFF25D47F),
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty() || value == "") {
                        Text(
                            text = "Enter RMTP Link Here",
                            color = Color(0x4D000000),
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    innerTextField()
                }
            )
        }
        Box(
            modifier = Modifier.background(Color(0xffffffff)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "arrow left",
                Modifier.size(25.dp)
                //                .padding(8.dp)
            )
        }
    }
}

@Composable
fun Chip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    Button(
        onClick = {
            onClick()
            val feedbackText = if (isSelected) "$text disabled" else "$text enabled"
            Toast.makeText(context, feedbackText, Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(topEnd = 5.dp)),
        border = if (isSelected) BorderStroke(
            width = 1.dp,
            color = Color(0xFF25D47F)
        ) else BorderStroke(width = 1.dp, color = Color(0xFF000000)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF25D47F) else Color(0x00FFFFFF),
            contentColor = Color(0xFF000000)
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}