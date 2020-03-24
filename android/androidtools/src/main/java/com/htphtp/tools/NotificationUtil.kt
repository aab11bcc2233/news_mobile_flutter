package com.htphtp.tools

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import java.lang.RuntimeException


/**
 * Created by htp on 2018/4/19.
 */
class NotificationUtil private constructor() {
    companion object {

        fun getNotificationManager(ctx: Context) =
            ctx.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager

        fun canel(ctx: Context, id: Int) = getNotificationManager(ctx).cancel(id)

        /**
         * PendingIntent.FLAG_UPDATE_CURRENT 搭配不同的 requestCode 可以实现每个通知都有点击事件，
         * 否则会出现多个通知，只有最后一个会有点击事件的这种情况。
         */
        fun getPendingBroadCast(
            ctx: Context,
            requestCode: Int,
            intent: Intent,
            flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
        ) = PendingIntent.getBroadcast(ctx, requestCode, intent, flags)

        @Throws(ChannelDisableException::class)
        fun createNotificationBuilder(
            ctx: Context,
            title: String,
            text: String,
            smallIcon: Int,
            largeIcon: Int,
            channelId: String,
            channelName: String,
            importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        ): NotificationCompat.Builder {
            /* if (!hasChannel(ctx, channelId)) {
                 createNotificationChannel(ctx, channelId, channelName, importance)
             }
             if (channelIsDisable(ctx, channelId)) {
                 throw ChannelDisableException(channelId, "notification channel disable")
             }

             val build: NotificationCompat.Builder = NotificationCompat.Builder(ctx, channelId)*/
            var build: NotificationCompat.Builder =
                if (SDK_INT >= Build.VERSION_CODES.O) {
                    if (!hasChannel(ctx, channelId)) {
                        createNotificationChannel(ctx, channelId, channelName, importance)
                    }
                    if (channelIsDisable(ctx, channelId)) {
                        throw ChannelDisableException(channelId, "notification channel disable")
                    }
                    NotificationCompat.Builder(ctx, channelId)
                } else {
                    NotificationCompat.Builder(ctx)
                }

            build.setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.resources, largeIcon))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)

            return build
        }


        @TargetApi(Build.VERSION_CODES.O)
        fun createNotificationChannel(
            ctx: Context,
            channelId: String,
            channelName: String,
            importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        ) {
            val channel = NotificationChannel(channelId, channelName, importance)
            getNotificationManager(ctx).createNotificationChannel(channel)
        }


        @TargetApi(Build.VERSION_CODES.O)
        fun channelIsDisable(ctx: Context, channelId: String): Boolean {
            val channel = getNotificationChannel(ctx, channelId)
            return channel.importance == NotificationManager.IMPORTANCE_NONE
        }

        @TargetApi(Build.VERSION_CODES.O)
        private fun getNotificationChannel(ctx: Context, channelId: String) =
            getNotificationManager(ctx).getNotificationChannel(channelId)

        @TargetApi(Build.VERSION_CODES.O)
        fun hasChannel(ctx: Context, channelId: String): Boolean {
            return getNotificationChannel(ctx, channelId) != null
        }

        @TargetApi(Build.VERSION_CODES.O)
        fun startPageMangerChannel(ctx: Context, channelId: String) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            ctx.startActivity(intent)
        }


        class ChannelDisableException(val channelId: String, message: String) : RuntimeException(message) {

        }
    }

}