package kz.stepanenkos.notes.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

private const val DEFAULT_TASK_ID = 0

class NotificationAlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val content = intent?.getStringExtra(NOTIFICATION_CONTENT_KEY).orEmpty()
        val taskId =
            intent?.getIntExtra(NOTIFICATION_TASK_ID_KEY, DEFAULT_TASK_ID) ?: DEFAULT_TASK_ID
        NotificationHelper.sendNotification(
            contentTasks = content,
            taskId = taskId,
        )
    }
}