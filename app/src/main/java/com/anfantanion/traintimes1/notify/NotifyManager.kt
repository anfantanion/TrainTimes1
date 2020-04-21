package com.anfantanion.traintimes1.notify

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.anfantanion.traintimes1.BuildConfig
import com.anfantanion.traintimes1.MainActivity
import com.anfantanion.traintimes1.R
import com.anfantanion.traintimes1.models.ActiveJourney
import com.anfantanion.traintimes1.models.TimeDate
import com.anfantanion.traintimes1.models.differenceOfTimesMinutes
import com.anfantanion.traintimes1.repositories.JourneyRepo
import com.anfantanion.traintimes1.repositories.StationRepo


object NotifyManager {
    lateinit var context : Context
    const val JOURNEYNOTIFYCHANNEL = "JOURNEYNOTIFYCHANNEL"
    const val changeReminder = 100;
    const val refreshID = 101;


    lateinit var alarmManager : AlarmManager
    var lastNotificationIntent : PendingIntent? = null
    var lastRefreshIntent : PendingIntent? = null
    var currentKeyPoint : ActiveJourney.KeyPoint? = null
    var activeJourney : ActiveJourney? = null


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

    fun setNextNotification(activeJourney: ActiveJourney, overrideTime : Long? = null){
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notify_change_enable",false)) return
        val change = activeJourney.getNextChange() ?: return
        currentKeyPoint = change
        if (lastNotificationIntent!=null) alarmManager.cancel(lastNotificationIntent)

        val notifyIntent = Intent(context, NotifyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            changeReminder,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        lastNotificationIntent = pendingIntent


        var timedate = TimeDate(startTime=change.arrivalTime())
        timedate.addMinutes(-PreferenceManager.getDefaultSharedPreferences(context).getString("notify_change_time","0")!!.toInt())

        if (timedate.calendar.timeInMillis < System.currentTimeMillis()) return
        if (BuildConfig.DEBUG || true){
            Toast.makeText(context,"Sending notification at "+timedate.getTime(),Toast.LENGTH_SHORT).show()
        }
        if (overrideTime!=null)
            timedate.calendar.timeInMillis = System.currentTimeMillis()+overrideTime



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
        val change = currentKeyPoint
        if (change != null)
            builder.setContentText(
                context.getString(
                    R.string.notification_change_short,
                    StationRepo.getStation(change.waypoint)!!.name,
                    differenceOfTimesMinutes(TimeDate(startTime=change.arrivalTime()), TimeDate()).toString(),
                    change.arrivalTime()
                )
            )
        builder.setSmallIcon(R.drawable.ic_train)
        builder.setPriority(Notification.PRIORITY_DEFAULT) // Set for older versions of android
        val notificationCompat = builder.build()
        val managerCompat = NotificationManagerCompat.from(context)
        managerCompat.notify(NotifyManager.changeReminder, notificationCompat)
    }

    fun sendNotificationIn(seconds: Int){
        setNextNotification(JourneyRepo.activeJourney.value!!,seconds.toLong()*1000)
    }

    fun queueNextRefresh(activeJourney: ActiveJourney, overrideTime : Long? = null){
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("automatic_refresh_enable",false)) return
        if (lastRefreshIntent!=null) alarmManager.cancel(lastRefreshIntent)

        this.activeJourney = activeJourney

        val intent = Intent(context, RefreshReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            changeReminder,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        lastNotificationIntent = pendingIntent


        var timedate = TimeDate()
        timedate.addMinutes(PreferenceManager.getDefaultSharedPreferences(context).getString("refresh_every","0")!!.toInt())
        if (BuildConfig.DEBUG || true){
            Toast.makeText(context,"Refreshing at "+timedate.getTime(),Toast.LENGTH_SHORT).show()
        }
        if (overrideTime!=null)
            timedate.calendar.timeInMillis = System.currentTimeMillis()+overrideTime

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            timedate.calendar.timeInMillis,
            pendingIntent
        )
    }

    fun refresh(){
        Toast.makeText(context,"Refreshing",Toast.LENGTH_SHORT).show()
        activeJourney?.getPlannedServices({
            val aj = activeJourney !!
            JourneyRepo.activeJourney.value = aj
        },{})
    }


}