package com.anfantanion.traintimes1.notify

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.anfantanion.traintimes1.MainActivity
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.differenceOfTimesMinutes
import com.anfantanion.traintimes1.repositories.JourneyRepo.activeJourney
import com.anfantanion.traintimes1.repositories.StationRepo


object NotifyManager {
    lateinit var context : Context
    const val JOURNEYNOTIFYCHANNEL = "JOURNEYNOTIFYCHANNEL"
    const val changeReminder = 100;

    lateinit var alarmManager : AlarmManager
    var lastNotificationIntent : PendingIntent? = null
    var currentChange : ActiveJourney.Change? = null

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
        alarmManager = NotifyManager.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun setNextNotification(activeJourney: ActiveJourney){
        val change = activeJourney.getNextChange() ?: return
        currentChange = change
        if (lastNotificationIntent!=null) alarmManager.cancel(lastNotificationIntent)

        val notifyIntent = Intent(context, NotifyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            changeReminder,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        lastNotificationIntent = pendingIntent

        var timedate = TimeDate(change.arrivalTime())

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            timedate.calendar.timeInMillis,
            pendingIntent
        )
    }

    fun makeChangeNotification(context: Context, intent: Intent?){
        var builder =
            if (Build.VERSION.SDK_INT >= 26)
                Notification.Builder(context, NotifyManager.JOURNEYNOTIFYCHANNEL)
            else
                Notification.Builder(context)

        val notifyIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        builder.setContentTitle(context.getString(R.string.notification_change_title))
        val change = currentChange
        if (change != null)
            builder.setContentText(
                context.getString(
                    R.string.notification_change_short,
                    StationRepo.getStation(change.waypoint)!!.name,
                    differenceOfTimesMinutes(TimeDate(),TimeDate(change.arrivalTime())).toString(),
                    change.arrivalTime()
                )
            )
        builder.setSmallIcon(R.drawable.ic_train)
        val notificationCompat = builder.build()
        val managerCompat = NotificationManagerCompat.from(context)
        managerCompat.notify(NotifyManager.changeReminder, notificationCompat)
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