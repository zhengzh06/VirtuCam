package com.wangyiheng.VirtuCam.utils

import com.wangyiheng.VirtuCam.MainHook
import com.wangyiheng.VirtuCam.data.models.VideoStatues

object InfoProcesser {
    var videoStatus: VideoStatues? = null
    var infoManager : InfoManager?= null


    fun initStatus(){
        infoManager = InfoManager(MainHook.context!!)
        videoStatus = infoManager!!.getVideoStatus()
    }
}