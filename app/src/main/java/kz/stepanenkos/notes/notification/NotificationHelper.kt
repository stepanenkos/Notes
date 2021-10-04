package kz.stepanenkos.notes.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import kz.stepanenkos.notes.MainActivity
import kz.stepanenkos.notes.R

object NotificationHelper {
    private const val CHANNEL_ID = "tasks_notification_channel"

    private lateinit var application: Application
    private var notificationIdCounter: Int = 0

    fun init(application: Application) {
        this.application = application
        initChannel()
    }

    fun sendNotification(
        taskId: Int,
        contentTasks: String
    ) {
        val notification: Notification = getNotification(
            taskId = taskId,
            contentTasks = contentTasks
        )

        NotificationManagerCompat.from(application).notify(
            notificationIdCounter++,
            notification
        )
    }

    private fun getNotification(
        taskId: Int,
        contentTasks: String
    ): Notification = NotificationCompat.Builder(
        application, CHANNEL_ID
    )
        .setContentTitle("Напоминание о задаче")
        .setContentText(contentTasks)
        .setSmallIcon(R.drawable.ic_notification)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(getPendingIntent(taskId))
        .setAutoCancel(true)
        .build()

    private fun getPendingIntent(
        taskId: Int,
    ): PendingIntent {
        val bundle = Bundle()
        bundle.putInt("TASK_ID", taskId)
        return NavDeepLinkBuilder(application)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.editorTasksFragment)
            .setArguments(bundle)
            .createPendingIntent()

        /*return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.
            PendingIntent.getActivity(application,
                0,
                eventDetailsEvent,
                PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(application,
                0,
                eventDetailsEvent,
                PendingIntent.FLAG_ONE_SHOT)
        }*/

    }

    private fun initChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            importance
        )

        val notificationManager: NotificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}