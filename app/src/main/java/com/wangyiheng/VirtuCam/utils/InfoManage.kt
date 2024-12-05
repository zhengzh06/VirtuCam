package com.wangyiheng.VirtuCam.utils

import android.content.Context
import android.util.Log
import com.crossbowffs.remotepreferences.RemotePreferences
import com.google.gson.Gson
import com.wangyiheng.VirtuCam.data.models.VideoInfo
import com.wangyiheng.VirtuCam.data.models.VideoStatues

class InfoManager(context: Context) {
    val prefs = RemotePreferences(context, "com.wangyiheng.VirtuCam.preferences", "main_prefs")
    private val gson = Gson()
    fun saveVideoStatus(videoStatus: VideoStatues) {
        val jsonString = gson.toJson(videoStatus)
        prefs.edit().putString("videoStatus", jsonString).apply()
    }

    fun getVideoStatus(): VideoStatues? {
        val jsonString = prefs.getString("videoStatus", null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, VideoStatues::class.java)
        } else {
            null
        }
    }

    fun removeVideoStatus() {
        prefs.edit().remove("videoStatus").apply()
    }

    fun saveVideoInfo(videoInfo: VideoInfo) {
        val jsonString = gson.toJson(videoInfo)
        prefs.edit().putString("videoInfo", jsonString).apply()
        Log.d("CHECK_THIS_DEBUG", "INFONAMANEGE VIDEO ADDED")
        Log.d("CHECK_THIS_DEBUG", "VIdeo url: " + videoInfo.videoUrl)
        //Toast.makeText(LocalContext.current, "infomange video added", Toast.LENGTH_SHORT)
    }

    fun getVideoInfo(): VideoInfo? {
        val jsonString = prefs.getString("videoInfo", null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, VideoInfo::class.java)
        } else {
            null
        }
    }

    fun removeVideoInfo() {
        prefs.edit().remove("videoInfo").apply()
    }
}