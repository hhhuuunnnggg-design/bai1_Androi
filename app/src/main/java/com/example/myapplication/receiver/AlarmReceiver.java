package com.example.myapplication.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.AlarmActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.NotificationHelper;

/**
 * AlarmReceiver
 * - BroadcastReceiver để nhận alarm từ AlarmManager
 * - Hiển thị notification khi alarm được kích hoạt
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_MESSAGE = "alarm_message";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Đảm bảo channel đã được tạo
        NotificationHelper.createNotificationChannel(context);

        // Lấy nội dung nhắc nhở
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        if (message == null || message.isEmpty()) {
            message = "Đã đến giờ báo thức!";
        }

        // Tạo Intent để mở app khi click notification
        Intent notificationIntent = new Intent(context, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Tạo Notification
        Notification notification = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setContentTitle("⏰ Báo Thức")
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        // Hiển thị notification
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notification);
        }
    }
}

