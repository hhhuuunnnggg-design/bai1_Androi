package com.example.myapplication.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.MusicActivity;
import com.example.myapplication.R;

/**
 * MusicService
 * - Foreground Service để phát nhạc khi tắt màn hình
 * - Sử dụng MediaPlayer để phát nhạc
 */
public class MusicService extends Service {

    // ==================== CONSTANTS ====================
    private static final String CHANNEL_ID = "MusicPlayerChannel";
    private static final int NOTIFICATION_ID = 1;

    // ==================== MEDIA PLAYER ====================
    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();

    // ==================== BINDER ====================
    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    // ==================== LIFECYCLE ====================
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            createNotificationChannel();
            initializeMediaPlayer();
            // Start foreground ngay trong onCreate để tránh lỗi
            startForeground(NOTIFICATION_ID, createNotification("Ready"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case "ACTION_PLAY_PAUSE":
                            if (isPlaying()) {
                                pause();
                            } else {
                                play();
                            }
                            break;
                        case "ACTION_STOP":
                            stop();
                            break;
                    }
                }
            }
            // Cập nhật notification
            startForeground(NOTIFICATION_ID, createNotification(
                    isPlaying() ? "Playing" : "Ready"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    // ==================== MEDIA PLAYER ====================
    private void initializeMediaPlayer() {
        try {
            // Sử dụng file nhạc trong res/raw/
            // Người dùng cần thêm file nhạc vào res/raw/song.mp3
            // Lưu ý: Tên file phải là chữ thường: song.mp3
            int resId = getResources().getIdentifier("song", "raw", getPackageName());

            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId);
                if (mediaPlayer != null) {
                    mediaPlayer.setOnCompletionListener(mp -> {
                        stop();
                        updateNotification("Stopped");
                    });
                }
            }

            // Nếu không tìm thấy file nhạc, tạo MediaPlayer rỗng
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Tạo MediaPlayer rỗng nếu có lỗi
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
        }
    }

    public void preparePlayer() {
        if (mediaPlayer != null) {
            try {
                // Chỉ prepare nếu MediaPlayer chưa được prepare
                // MediaPlayer.create() đã tự động prepare rồi
                int resId = getResources().getIdentifier("song", "raw", getPackageName());
                if (resId != 0 && !mediaPlayer.isPlaying()) {
                    // Nếu MediaPlayer đã được tạo từ create() thì không cần prepare lại
                    // Chỉ prepare nếu MediaPlayer mới được tạo thủ công
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            try {
                int resId = getResources().getIdentifier("song", "raw", getPackageName());
                if (resId == 0) {
                    // Không có file nhạc
                    return;
                }
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    updateNotification("Playing");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateNotification("Paused");
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            try {
                int resId = getResources().getIdentifier("song", "raw", getPackageName());
                if (resId == 0) {
                    // Không có file nhạc
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateNotification("Stopped");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // ==================== NOTIFICATION ====================
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player Channel",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Channel for music player notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(String status) {
        Intent notificationIntent = new Intent(this, MusicActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playPauseIntent = new Intent(this, MusicService.class);
        playPauseIntent.setAction("ACTION_PLAY_PAUSE");
        PendingIntent playPausePendingIntent = PendingIntent.getService(
                this, 0, playPauseIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction("ACTION_STOP");
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 1, stopIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText(status)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_media_play, "Play/Pause", playPausePendingIntent)
                .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)
                .setOngoing(true)
                .build();
    }

    private void updateNotification(String status) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, createNotification(status));
        }
    }
}
