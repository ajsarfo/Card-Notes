package com.sarftec.cardnotes.presentation.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.sarftec.cardnotes.data.repository.TodoNotifyRepository
import com.sarftec.cardnotes.entity.TodoNotify
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class NotifyAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notifyMaker: NotifyMaker

    @Inject
    lateinit var todoNotifyRepository: TodoNotifyRepository

    override fun onReceive(context: Context, intent: Intent) {
        val todoNotify = intent.getParcelableExtra<Bundle>(BUNDLE)
            ?.getParcelable<TodoNotify>(PARCEL)
            ?: return
        runBlocking {
            notifyMaker.notify(todoNotify)
        }
    }

    companion object {
        const val PARCEL = "alarm_receiver_parcel"
        const val BUNDLE = "alarm_receiver_bundle"
    }
}