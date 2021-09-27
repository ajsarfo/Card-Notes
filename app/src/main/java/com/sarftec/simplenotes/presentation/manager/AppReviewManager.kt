package com.sarftec.simplenotes.presentation.manager

import com.sarftec.simplenotes.readSettings
import kotlinx.coroutines.flow.first
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.sarftec.simplenotes.editSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AppReviewManager(
    private val activity: AppCompatActivity
) {
    private val reviewManager = ReviewManagerFactory.create(activity)
    private val reviewStateFlow = MutableStateFlow<ReviewInfo?>(null)

    private var job: Job? = null

    init {
        job = activity.lifecycleScope.launch {
            reviewStateFlow.collect {
                it?.let {
                    reviewManager.launchReviewFlow(activity, it)
                    throw CancellationException()
                }
            }
        }
    }

    suspend fun triggerReview() {
        activity.readSettings(START_UP_TIMES, 0).first().let { count ->
            if(count >= 3) review() else {
                activity.editSettings(START_UP_TIMES, count + 1)
                job?.cancel()
            }
        }
    }

    private fun review() {
        reviewManager.requestReviewFlow().let {
            it.addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    reviewStateFlow.value = request.result
                }
            }
        }
    }

    companion object {
        val START_UP_TIMES = intPreferencesKey("App_start_up")
    }
}