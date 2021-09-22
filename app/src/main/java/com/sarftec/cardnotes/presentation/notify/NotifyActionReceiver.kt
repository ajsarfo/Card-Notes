package com.sarftec.cardnotes.presentation.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.app.NotificationManagerCompat
import com.sarftec.cardnotes.data.repository.TodoRepository
import com.sarftec.cardnotes.entity.TodoNotify
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NotifyActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notifyManager: NotifyManager

    @Inject
    lateinit var todoRepository: TodoRepository

    @Parcelize
    class ActionParcel(val todoNotify: TodoNotify, val action: Int) : Parcelable {
        companion object {
            const val COMPLETE = 0
            const val REMIND = 1
        }
    }

    override fun onReceive(context: Context, received: Intent?) {
        val parcel = received?.getParcelableExtra<ActionParcel>(PARCEL) ?: let {
            NotificationManagerCompat.from(context).cancel(NotifyMaker.NOTIFICATION_ID)
            return
        }
        when (parcel.action) {
            ActionParcel.COMPLETE -> {
                runBlocking {
                    notifyManager.removeTodoNotify(parcel.todoNotify.todoId)
                    todoRepository.findTodo(parcel.todoNotify.todoId)?.let {
                        it.isCompleted = true
                        todoRepository.update(it)
                    }
                }
            }
            ActionParcel.REMIND -> {
                runBlocking {
                    notifyManager.removeTodoNotify(parcel.todoNotify.todoId)
                    notifyManager.addTodoNotify(
                        parcel.todoNotify.apply {
                            time += TimeUnit.MINUTES.toMillis(10)
                        }
                    )
                }
            }
        }
        NotificationManagerCompat.from(context).cancel(NotifyMaker.NOTIFICATION_ID)
    }

    companion object {
        const val PARCEL = "notify_action_receiver_parcel"
    }
}