package com.anfantanion.traintimes1.notify

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.repositories.JourneyRepo.activeJourney


object NotifyManager {
    lateinit var context : Context
    const val JOURNEYNOTIFYCHANNEL = "JOURNEYNOTIFYCHANNEL"
    const val changeReminder = 100;

    fun setup(context: Context){
        this.context = context
        if (Build.VERSION.SDK_INT >= 26) {
            val n1 = NotificationChannel(
                JOURNEYNOTIFYCHANNEL,
                context.getString(R.string.JOURNEYNOTIFYCHANNEL),
                NotificationManager.IMPORTANCE_HIGH
            )
            n1.description = context.getString(R.string.JOURNEYNOTIFYCHANNEL_Desc)
            n1.enableLights(true)
            n1.lightColor = ContextCompat.getColor(context, R.color.colorPrimary);
            n1.enableVibration(true)

            NotificationManagerCompat.from(context).createNotificationChannel(n1)
        }
    }

    fun sendNotification(activeJourney: ActiveJourney){
        var change = activeJourney.getNextChange() ?: return
        val notifyIntent = Intent(context, NotifyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            changeReminder,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        var timedate = TimeDate(change.arrivalTime())

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            timedate.calendar.timeInMillis,
            pendingIntent
        )
    }

    fun sendNotificationIn(seconds: Int){
        val notifyIntent = Intent(context, NotifyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            changeReminder,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        var timedate = TimeDate()

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            timedate.calendar.timeInMillis+seconds*1000,
            pendingIntent
        )
    }


}