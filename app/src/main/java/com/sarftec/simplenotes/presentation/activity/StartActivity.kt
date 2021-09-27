package com.sarftec.simplenotes.presentation.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.sarftec.simplenotes.R
import kotlinx.coroutines.delay

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        lifecycleScope.launchWhenCreated {
            delay(2000)
            navigateTo(MainActivity::class.java, true)
        }
    }
}