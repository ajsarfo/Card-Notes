package com.sarftec.simplenotes.presentation.activity

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.sarftec.simplenotes.R

abstract class BaseActivity : AppCompatActivity() {

    protected fun <T> navigateTo(
        klass: Class<T>,
        finish: Boolean = false,
        slideIn: Int = R.anim.slide_in_right,
        slideOut: Int = R.anim.slide_out_left,
        parcel: Parcelable? = null
    ) {
        val intent = Intent(this, klass).also {
            it.putExtra(ACTIVITY_PARCEL, parcel)
        }
        startActivity(intent)
        if (finish) finish()
        overridePendingTransition(slideIn, slideOut)
    }

    protected fun <T> navigateTo(
        klass: Class<T>,
        intent: Intent,
        slideIn: Int = R.anim.slide_in_right,
        slideOut: Int = R.anim.slide_out_left,
    ) {
        startActivity(intent)
        overridePendingTransition(slideIn, slideOut)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        const val ACTIVITY_PARCEL = "activity_parcel"
    }
}