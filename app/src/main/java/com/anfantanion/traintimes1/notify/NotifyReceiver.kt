package com.anfantanion.traintimes1.notify

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.anfantanion.traintimes1.MainActivity
import com.anfantanion.traintimes1.R


class NotifyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context!!
        var builder =
            if (Build.VERSION.SDK_INT >= 26)
                Notification.Builder(context, NotifyManager.JOURNEYNOTIFYCHANNEL)
            else
                Notification.Builder(context)

        val notifyIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        builder.setContentTitle("TEST")
        builder.setSmallIcon(R.drawable.ic_train)
        val notificationCompat = builder.build()
        val managerCompat = NotificationManagerCompat.from(context)
        managerCompat.notify(NotifyManager.changeReminder, notificationCompat)
    }

}