package quiz.game.trivia.data.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import quiz.game.trivia.GameApplication
import quiz.game.trivia.R
import quiz.game.trivia.domain.CHANNEL_ID
import quiz.game.trivia.domain.MAX_ENERGY
import quiz.game.trivia.domain.repository.GameRepository
import quiz.game.trivia.domain.repository.UserRepository
import quiz.game.trivia.presentation.MainActivity
import quiz.game.trivia.presentation.util.cancelAlarm
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var userRepository: UserRepository

    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        goAsync {

            //Pre-check
            var currentEnergy = gameRepository.energy.first()
            if (currentEnergy == MAX_ENERGY) {
                withContext(Dispatchers.Main) {
                    context.cancelAlarm()
                }
                return@goAsync
            }

            gameRepository.increaseEnergy()

            //Post-check
            currentEnergy = gameRepository.energy.first()
            if (currentEnergy != MAX_ENERGY) return@goAsync

            val canShowNotification = userRepository.isEnergyAlertEnabled.first()

            withContext(Dispatchers.Main) {
                context.cancelAlarm()
                if (canShowNotification) showNotification(context)
            }
        }
    }

    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(context: Context) {

        if ((context.applicationContext as GameApplication).isInForeground()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }

        val pendingIntent =
            PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Energy restored")
            .setContentText("Your energy has been restored. Click to play now.")
            .setSmallIcon(R.drawable.ic_energy)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build())
        }
    }
}

fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    CoroutineScope(SupervisorJob() + dispatcher).launch(context) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}