package com.sarftec.simplenotes

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.rateApp() {
    val appId = packageName
    val rateIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$appId")
    )
    var marketFound = false
    // find all applications able to handle our rateIntent
    // find all applications able to handle our rateIntent
    val otherApps = packageManager
        .queryIntentActivities(rateIntent, 0)
    for (otherApp in otherApps) {
        // look for Google Play application
        if (otherApp.activityInfo.applicationInfo.packageName
            == "com.android.vending"
        ) {
            val otherAppActivity = otherApp.activityInfo
            val componentName = ComponentName(
                otherAppActivity.applicationInfo.packageName,
                otherAppActivity.name
            )
            // make sure it does NOT open in the stack of your activity
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // task repeating if needed
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            // if the Google Play was already open in a search result
            //  this make sure it still go to the app page you requested
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // this make sure only the Google Play app is allowed to
            // intercept the intent
            rateIntent.component = componentName
            startActivity(rateIntent)
            marketFound = true
            break
        }
    }

    // if GP not present on device, open web browser

    // if GP not present on device, open web browser
    if (!marketFound) {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$appId")
        )
        startActivity(webIntent)
    }
}
