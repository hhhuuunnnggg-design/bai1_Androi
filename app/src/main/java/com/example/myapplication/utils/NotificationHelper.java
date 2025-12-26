package com.example.myapplication.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/**
 * NotificationHelper
 * - Utility class để quản lý Notification Channels
 * - Tạo channel cho Android 8.0+
 */
public class NotificationHelper {

    public static final String CHANNEL_ID = "alarm_channel";
    public static final String CHANNEL_NAME = "Báo Thức & Nhắc Nhở";
    public static final String CHANNEL_DESCRIPTION = "Kênh thông báo cho báo thức và nhắc nhở";

    /**
     * Tạo Notification Channel (cần thiết cho Android 8.0+)
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}

