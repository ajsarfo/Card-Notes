package com.sarftec.simplenotes.presentation

import java.text.SimpleDateFormat
import java.util.*

fun getDateString(time: Long) : String {
    val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return simpleDateFormat.format(Date(time))
}

fun getTimeString(time: Long) : String {
    val simpleDateFormat = SimpleDateFormat("HH:mm aa", Locale.getDefault())
    return simpleDateFormat.format(Date(time))
}

fun getExtendedDateString(time: Long) : String {
    val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm aa", Locale.getDefault())
    return simpleDateFormat.format(Date(time))
}