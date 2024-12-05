package com.wangyiheng.VirtuCam.utils

import com.crossbowffs.remotepreferences.RemotePreferenceProvider


class MultiprocessSharedPreferences : RemotePreferenceProvider("com.wangyiheng.VirtuCam.preferences", arrayOf("main_prefs"))