package com.sarftec.cardnotes.presentation.notify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sarftec.cardnotes.R
import com.sarftec.cardnotes.data.repository.TodoRepository
import com.sarftec.cardnotes.entity.TodoNotify
import com.sarftec.cardnotes.presentation.activity.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotifyMaker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val todoRepository: TodoRepository
) {
    private val notificationManager by lazy {
        NotificationManagerCompat.from(context)
    }

    suspend fun notify(todoNotify: TodoNotify) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(todoNotify))
    }

    private suspend fun getView(todoNotify: TodoNotify, layoutId: Int): RemoteViews {
        val message = todoRepository.findTodo(todoNotify.todoId)?.title ?: "ERROR! : TODO NOT FOUND"
        Log.v("TAG", "$message")
        return RemoteViews(context.packageName, layoutId).apply {
            setTextViewText(R.id.todo, message)
        }
    }

    private suspend fun buildNotification(quote: TodoNotify): Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(getView(quote, R.layout.layout_todo_notification_collapsed))
           // .setCustomBigContentView(getView(quote, R.layout.layout_todo_notification_expanded))
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.color_note_search_icon))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                createAction(
                    "MARK COMPLETED",
                    getActionIntent(quote, NotifyActionReceiver.ActionParcel.COMPLETE),
                    2
                )
            )
            .addAction(
                createAction(
                    "REMIND IN 10 MIN",
                    getActionIntent(quote, NotifyActionReceiver.ActionParcel.REMIND),
                    3
                )
            )
            .build()
    }

    private fun getActionIntent(todoNotify: TodoNotify, action: Int): Intent {
        return Intent(context, NotifyActionReceiver::class.java).apply {
            putExtra(
                NotifyActionReceiver.PARCEL,
                NotifyActionReceiver.ActionParcel(
                    todoNotify,
                    action
                )
            )
        }
    }

    private fun getPendingIntent() = PendingIntent.getActivity(
        context,
        1,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun createAction(
        text: String,
        intent: Intent,
        requestCode: Int
    ): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            null,
            text,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        ).build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                "Channel 1",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily Inspirational Quotes"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL = "notification_channel"
    }
}