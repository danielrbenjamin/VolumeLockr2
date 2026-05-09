package com.dan.volumelockradvanced.service

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.dan.volumelockradvanced.R
import com.dan.volumelockradvanced.ui.Volume

class VolumeProvider(private val mContext: Context) {

    private val mAudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun getVolumes(): List<Volume> {
        val resource = mContext.resources
        val volumes = mutableListOf<Volume>()

        getDeviceTypes().forEach { deviceType ->
            volumes.add(
                Volume(
                    resource.getString(R.string.media_title),
                    AudioManager.STREAM_MUSIC,
                    fetchVolume(AudioManager.STREAM_MUSIC),
                    0,
                    fetchMaxVolume(AudioManager.STREAM_MUSIC),
                    false,
                    deviceType
                )
            )
            volumes.add(
                Volume(
                    resource.getString(R.string.call_title),
                    AudioManager.STREAM_VOICE_CALL,
                    fetchVolume(AudioManager.STREAM_VOICE_CALL),
                    1,
                    fetchMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    false,
                    deviceType
                )
            )
            volumes.add(
                Volume(
                    resource.getString(R.string.notification_title),
                    AudioManager.STREAM_NOTIFICATION,
                    fetchVolume(AudioManager.STREAM_NOTIFICATION),
                    0,
                    fetchMaxVolume(AudioManager.STREAM_NOTIFICATION),
                    false,
                    deviceType
                )
            )
            volumes.add(
                Volume(
                    resource.getString(R.string.alarm_title),
                    AudioManager.STREAM_ALARM,
                    fetchVolume(AudioManager.STREAM_ALARM),
                    1,
                    fetchMaxVolume(AudioManager.STREAM_ALARM),
                    false,
                    deviceType
                )
            )
        }

        return volumes
    }

    private fun getDeviceTypes(): List<String> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val devices = mAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val deviceTypes = devices.mapNotNull { device ->
                when (device.type) {
                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Speaker"
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES, AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Headphones"
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP, AudioDeviceInfo.TYPE_BLUETOOTH_SCO ->
                        device.productName.toString().takeIf { it.isNotBlank() } ?: "Bluetooth"
                    else -> null
                }
            }.distinct()
            if (deviceTypes.isNotEmpty()) {
                return deviceTypes
            }
        }
        return listOf("Speaker", "Headphones", "Bluetooth")
    }

    private fun fetchVolume(volume: Int): Int {
        return mAudioManager.getStreamVolume(volume)
    }

    private fun fetchMaxVolume(volume: Int): Int {
        return mAudioManager.getStreamMaxVolume(volume)
    }
}
