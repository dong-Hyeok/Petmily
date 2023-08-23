package com.petmily.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.petmily.presentation.view.MainActivity

private const val TAG = "Fetmily_FirebaseMessage"
class FirebaseMessageService : FirebaseMessagingService() {
    // 새로운 토큰이 생성될 때 마다 해당 콜백이 호출된다.
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
    }

    // Foreground에서 Push Service를 받기 위해 Notification 설정
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: $remoteMessage")

        remoteMessage.notification?.apply {
            val intent = Intent(this@FirebaseMessageService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(this@FirebaseMessageService, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(this@FirebaseMessageService, MainActivity.channel_id)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // 설정 안하면 클릭해도 삭제가 안됨
                .setFullScreenIntent(pendingIntent, true) // foreground에서 알림 화면 상단에 보이게

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(101, builder.build())
        }
    }
}
