package com.sarftec.cardnotes.presentation.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.sarftec.cardnotes.entity.TodoNotify
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotifyAlarmMaker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager: AlarmManager? by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun getPendingIntent(todoNotify: TodoNotify?): PendingIntent {
        val intent = Intent(context, NotifyAlarmReceiver::class.java)
        val bundle = Bundle()
        bundle.putParcelable(NotifyAlarmReceiver.PARCEL, todoNotify)
        intent.putExtra(NotifyAlarmReceiver.BUNDLE, bundle)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun startAlarm(todoNotify: TodoNotify) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                todoNotify.time,
                getPendingIntent(todoNotify)
            )
        } else {
            alarmManager?.set(
                AlarmManager.RTC_WAKEUP,
                todoNotify.time,
                getPendingIntent(todoNotify)
            )
        }
    }

    fun stopAlarm() {
        alarmManager?.cancel(getPendingIntent(null))
    }
}