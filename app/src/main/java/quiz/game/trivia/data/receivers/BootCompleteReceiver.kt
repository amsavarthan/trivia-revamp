package quiz.game.trivia.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import quiz.game.trivia.presentation.util.setAlarm

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || intent.action != "android.intent.action.BOOT_COMPLETED") return
        context?.setAlarm()
    }
}