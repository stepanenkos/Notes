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
import kz.stepanenkos.notes.listtasks.presentation.TASK_ID

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
        .setContentTitle(application.getString(R.string.content_title_for_notification))
        .setContentText(contentTasks)
        .setSmallIcon(R.drawable.ic_notification)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(getPendingIntent(taskId))
        .setVibrate(listOf(1L, 2L, 3L).toLongArray())
        .setAutoCancel(true)
        .build()

    private fun getPendingIntent(
        taskId: Int,
    ): PendingIntent {
        val bundle = Bundle()
        bundle.putInt(TASK_ID, taskId)
        return NavDeepLinkBuilder(application)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.editorTasksFragment)
            .setArguments(bundle)
            .createPendingIntent()
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