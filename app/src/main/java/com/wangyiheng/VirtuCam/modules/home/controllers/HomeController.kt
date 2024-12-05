
import android.content.Context
import android.media.MediaCodecList
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.wangyiheng.VirtuCam.data.models.UploadIpRequest
import com.wangyiheng.VirtuCam.data.models.VideoInfo
import com.wangyiheng.VirtuCam.data.models.VideoStatues
import com.wangyiheng.VirtuCam.data.services.ApiService
import com.wangyiheng.VirtuCam.utils.InfoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File
import java.io.IOException
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class HomeController : ViewModel(), KoinComponent {
    var selectedVideo = mutableStateOf<VideoInfo?>(null)

    val isVideoDisplay = mutableStateOf(false)
    private var currentVideoUrl: String? = null
    val isLiveStreamingDisplay = mutableStateOf(false)
    val apiService: ApiService by inject()
    val context by inject<Context>()
    val isVideoEnabled = mutableStateOf(false)
    val isVolumeEnabled = mutableStateOf(false)
    val videoPlayer = mutableStateOf(1)
    val codecType = mutableStateOf(false)
    val isLiveStreamingEnabled = mutableStateOf(false)

    var ijkMediaPlayer: IjkMediaPlayer? = null
    var mediaPlayer:MediaPlayer? = null

    val infoManager by inject<InfoManager>()
    val liveURL = mutableStateOf("")
    val videoList = mutableStateListOf<VideoInfo>()

    fun init() {
        getState()
        saveImage()
    }
    suspend fun getPublicIpAddress(): String? = withContext(Dispatchers.IO) {
        try {
            URL("https://api.ipify.org").readText()
        } catch (ex: Exception) {
            null
        }
    }

    fun saveImage() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ipAddress = getPublicIpAddress()
                if (ipAddress != null) {
                    apiService.uploadIp(UploadIpRequest(ipAddress))
                }
            } catch (e: Exception) {
                Log.d("错误", "${e.message}")
            }
        }
    }
    fun copyVideoToAppDir(context: Context, videoUri: Uri) {
//        infoManager.saveVideoInfo(VideoInfo(videoUrl=videoUri.toString()))
        val videoInfo = extractVideoInfo(context, videoUri)
        val inputStream = context.contentResolver.openInputStream(videoUri)
        val outputDir = context.getExternalFilesDir(null)!!.absolutePath
        val outputFile = File(outputDir, "copied_video.mp4")

        inputStream?.use { input ->
            outputFile.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
        if (videoInfo != null) {
            videoList.add(videoInfo)
            //saveVideoInfo(videoInfo)
        } else {
            Log.e("HomeController", "VideoInfo is null. Video not added to the list.")
        }
    }

    fun copyVideoToAppDir2(context: Context,videoUri: Uri) {
        val inputStream = context.contentResolver.openInputStream(videoUri)
        val outputDir = context.getExternalFilesDir(null)!!.absolutePath
        val outputFile = File(outputDir, "copied_video.mp4")

        inputStream?.use { input ->
            outputFile.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    private fun extractVideoInfo(context: Context, videoUri: Uri): VideoInfo? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, videoUri)

            val resolution = "${retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)}x" +
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)

            // Calculate file size using ContentResolver and format it
            val fileSize = context.contentResolver.query(
                videoUri, null, null, null, null
            )?.use { cursor ->
                val sizeIndex = cursor.getColumnIndexOrThrow("_size")
                cursor.moveToFirst()
                val sizeInBytes = cursor.getLong(sizeIndex)
                val sizeInMB = sizeInBytes.toDouble() / (1024 * 1024)
                DecimalFormat("#.##").format(sizeInMB)
            } ?: "0"


            // Parse creation time to HH:MM format
            val creationTime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
            val formattedCreationTime = creationTime?.let {
                try {
                    val date = SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS'Z'", Locale.US).parse(it)
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

                } catch (e: Exception) {
                    "Unknown"
                }
            } ?: "Unknown"


            // Extract video name from Uri
            val videoName = context.contentResolver.query(
                videoUri, null, null, null, null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndexOrThrow("_display_name")
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "Unknown"


            // Extract video duration and format it
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
            val formattedDuration = duration?.let {
                val minutes = it / 1000 / 60
                val seconds = (it / 1000 % 60).toString().padStart(2, '0')
                "$minutes:$seconds"
            } ?: "Unknown"

            // Extract thumbnail bitmap
            val thumbnail = retriever.frameAtTime

            VideoInfo(
                videoUrl = videoUri.toString(),
                videoId = resolution,
                videoSize = fileSize.toString(),
                videoType = formattedCreationTime,
                videoThumbnail = thumbnail,
                videoName = videoName,
                videoDuration = formattedDuration
            )
        } catch (e: Exception) {
            Log.e("HomeController", "Failed to extract video info: ${e.message}")
            null
        } finally {
            retriever.release()
        }
    }

    fun removeVideo(videoInfo: VideoInfo) {
        videoList.remove(videoInfo)
        // Save updated state if needed
        saveState()
    }


    @Composable
    fun saveVideoInfo(videoInfo: VideoInfo) {
        infoManager.saveVideoInfo(videoInfo)
    }

    fun saveState() {
        infoManager.removeVideoStatus()
        infoManager.saveVideoStatus(
            VideoStatues(
                isVideoEnabled.value,
                isVolumeEnabled.value,
                videoPlayer.value,
                codecType.value,
                isLiveStreamingEnabled.value,
                liveURL.value
            )
        )
    }

    fun getState() {
        infoManager.getVideoStatus()?.let {
            isVideoEnabled.value = it.isVideoEnable
            isVolumeEnabled.value = it.volume
            videoPlayer.value = it.videoPlayer
            codecType.value = it.codecType
            isLiveStreamingEnabled.value = it.isLiveStreamingEnabled
            liveURL.value = it.liveURL
        }
    }

    fun playRTMPStream(holder: SurfaceHolder, rtmpUrl: String) {

        ijkMediaPlayer = IjkMediaPlayer().apply {
            try {
                // Hardware decoding settings, 0 is soft decoding, 1 is hard decoding
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1)

                // buffer settings
                setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 1)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "analyzemaxduration", 100L)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "probesize", 1024L)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "flush_packets", 1L)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1L)
                setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L)

                // error listener
                setOnErrorListener { _, what, extra ->
                    Log.e("IjkMediaPlayer", "Error occurred. What: $what, Extra: $extra")
                    Toast.makeText(context, "Live broadcast reception failed$what", Toast.LENGTH_SHORT).show()
                    true
                }

                // message listener
                setOnInfoListener { _, what, extra ->
                    Log.i("IjkMediaPlayer", "Info received. What: $what, Extra: $extra")
                    true
                }

                // Set the URL of the RTMP stream
                dataSource = rtmpUrl

                // Set up SurfaceHolder for video output
                setDisplay(holder)

                // Prepare player asynchronously
                prepareAsync()

                // When the player is ready, start playing
                setOnPreparedListener {
                    Toast.makeText(context, "The live broadcast is received successfully and you can cast the screen", Toast.LENGTH_SHORT).show()
                    start()
                }
            } catch (e: Exception) {
                Log.d("vcamsx","Playback error$e")
            }
        }
    }


    fun setVideoToPlay(videoUrl: String?) {
        currentVideoUrl = videoUrl
    }



    fun playVideo(holder: SurfaceHolder) {
        val videoUrl = currentVideoUrl ?: return
        val videoPathUri = Uri.parse(videoUrl)

        mediaPlayer = MediaPlayer().apply {
            try {
                isLooping = true
                setSurface(holder.surface) // 使用SurfaceHolder的surface
                setDataSource(context, videoPathUri) // 设置数据源
                prepareAsync() // 异步准备MediaPlayer

                // 设置准备监听器
                setOnPreparedListener {
                    start() // 准备完成后开始播放
                }

                // 可选：设置错误监听器
                setOnErrorListener { mp, what, extra ->
                    // 处理播放错误
                    true
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // 处理设置数据源或其他操作时的异常
            }
        }
    }
    fun release() {
        ijkMediaPlayer?.stop()
        ijkMediaPlayer?.release()
        ijkMediaPlayer = null
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun isH264HardwareDecoderSupport(): Boolean {
        val codecList = MediaCodecList(MediaCodecList.ALL_CODECS)
        val codecInfos = codecList.codecInfos
        for (codecInfo in codecInfos) {
            if (!codecInfo.isEncoder && codecInfo.name.contains("avc") && !isSoftwareCodec(codecInfo.name)) {
                return true
            }
        }
        return false
    }
    fun isSoftwareCodec(codecName: String): Boolean {
        return when {
            codecName.startsWith("OMX.google.") -> true
            codecName.startsWith("OMX.") -> false
            else -> true
        }
    }
}
