package kz.stepanenkos.notes.notification

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import kz.stepanenkos.notes.common.model.TaskData

const val NOTIFICATION_CONTENT_KEY = "notification_title"
const val NOTIFICATION_TASK_ID_KEY = "TASK_ID"

class NotificationAlarmHelper(
    private val application: Application,
) {
    private var pendingIntent: PendingIntent? = null
    private val alarmManager: AlarmManager? = application.getSystemService(
        Context.ALARM_SERVICE
    ) as? AlarmManager

    fun createNotificationAlarm(taskData: TaskData) {
        if(taskData.notificationOn) {
            pendingIntent =
                Intent(application, NotificationAlarmBroadcastReceiver::class.java).apply {
                    putExtra(NOTIFICATION_CONTENT_KEY, taskData.contentTask)
                    putExtra(NOTIFICATION_TASK_ID_KEY, taskData.id)
                }.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.getBroadcast(application,
                            taskData.id,
                            it,
                            PendingIntent.FLAG_IMMUTABLE)
                    } else {
                        PendingIntent.getBroadcast(application,
                            taskData.id,
                            it,
                            PendingIntent.FLAG_UPDATE_CURRENT)
                    }
                }

            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                taskData.dateOfNotification,
                pendingIntent
            )
        }
    }

    fun cancelNotificationAlarm(taskData: TaskData) {
        pendingIntent ?: return
        val intent = Intent(application, NotificationAlarmBroadcastReceiver::class.java)
        val pendingIn = PendingIntent.getBroadcast(application,
            taskData.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager?.cancel(pendingIn)
    }
}