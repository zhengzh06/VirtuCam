package com.wangyiheng.VirtuCam.data.models

import android.graphics.Bitmap


data class VideoInfo(
    val videoId: String,        // Store resolution as String
    val videoName: String,      // Store name
    val videoSize: String,      // Store size
    val videoUrl: String,       // Video URL
    val videoType: String,    // Creation time or type
    val videoThumbnail: Bitmap? = null, // New field for the thumbnail
    val videoDuration: String? = null   // Video duration
)

data class VideoInfos(
    val videos: List<VideoInfo>
)