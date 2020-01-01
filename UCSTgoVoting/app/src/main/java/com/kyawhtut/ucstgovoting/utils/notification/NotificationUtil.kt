package com.kyawhtut.ucstgovoting.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import java.lang.IllegalArgumentException

class NotificationUtil private constructor(private val ctx: Context) {

    private val manager: NotificationManager by lazy {
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private var enableLight: Boolean = false
    private var enableSound: Boolean = false
    private var enableVibrate: Boolean = false

    private fun getManager(channelId: String, channelName: String): NotificationManager {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableLights(enableLight)
                    if (enableSound)
                        setSound(
                            Settings.System.DEFAULT_NOTIFICATION_URI,
                            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
                        )
                    enableVibration(enableVibrate)
                    vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 100)
                }
            manager.createNotificationChannel(notificationChannel)
        }
        return manager
    }

    class Builder(private val ctx: Context) {
        var enableLight: Boolean = false
        var enableSound: Boolean = false
        var enableVibrate: Boolean = false
        var channelId: String = ""
        var channelName: String = ""

        fun build(
            channelId: String = this@Builder.channelId,
            channelName: String = this@Builder.channelName
        ): NotificationManager {
            if (channelId.isEmpty() || channelName.isEmpty())
                throw IllegalArgumentException("Channel ID and Channel Name should not be null.")
            return NotificationUtil(ctx).apply {
                enableLight = this@Builder.enableLight
                enableSound = this@Builder.enableSound
                enableVibrate = this@Builder.enableVibrate
            }.getManager(channelId, channelName)
        }
    }
}