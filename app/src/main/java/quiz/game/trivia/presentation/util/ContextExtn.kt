package quiz.game.trivia.presentation.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import quiz.game.trivia.R
import quiz.game.trivia.data.receivers.AlarmReceiver
import quiz.game.trivia.domain.INTERVAL_MINUTES

fun Context.findActivity(): ComponentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is ComponentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

fun Context.isTablet() = resources.getBoolean(R.bool.isTablet)

private val pendingIntentForAlarm: (Context) -> PendingIntent = { context ->
    val broadcastIntent = Intent(context, AlarmReceiver::class.java)
    PendingIntent.getBroadcast(
        /* context = */ context,
        /* requestCode = */ 0,
        /* intent = */ broadcastIntent,
        /* flags = */  PendingIntent.FLAG_IMMUTABLE
    )
}

fun Context.cancelAlarm() {
    val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmMgr.cancel(pendingIntentForAlarm(this))
}

fun Context.setAlarm() {
    // Setting up AlarmManager
    val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmMgr.setRepeating(
        AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis(),
        (1000 * 60 * INTERVAL_MINUTES).toLong(),
        pendingIntentForAlarm(this)
    )
}