package com.example.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.service.MusicService;

/**
 * MusicActivity
 * - Màn hình phát nhạc với MediaPlayer thông qua MusicService
 * - Có các nút Play/Pause/Stop và SeekBar để điều khiển
 */
public class MusicActivity extends AppCompatActivity {

    // ==================== CONSTANTS ====================
    private static final int UPDATE_INTERVAL_MS = 100;

    // ==================== VIEW COMPONENTS ====================
    private Button btnPlayPause;
    private Button btnStop;
    private SeekBar seekBar;
    private TextView txtCurrentTime;
    private TextView txtTotalTime;
    private TextView txtStatus;

    // ==================== SERVICE ====================
    private MusicService musicService;
    private boolean isServiceBound = false;

    // ==================== HANDLER ====================
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBar = this::updateProgress;

    // ==================== SERVICE CONNECTION ====================
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isServiceBound = true;
            initializePlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            musicService = null;
        }
    };

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initializeViews();
        setupListeners();
        startMusicService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isServiceBound) {
            bindMusicService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindServiceIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
        unbindServiceIfNeeded();
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnStop = findViewById(R.id.btnStop);
        seekBar = findViewById(R.id.seekBar);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtTotalTime = findViewById(R.id.txtTotalTime);
        txtStatus = findViewById(R.id.txtStatus);

        seekBar.setEnabled(false);
    }

    private void setupListeners() {
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnStop.setOnClickListener(v -> stopMusic());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicService != null) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause updates while user is dragging
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume updates after user releases
            }
        });
    }

    // ==================== SERVICE MANAGEMENT ====================
    private void startMusicService() {
        try {
            Intent intent = new Intent(this, MusicService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            bindMusicService();
        } catch (Exception e) {
            showError("Lỗi khởi động service", e);
        }
    }

    private void bindMusicService() {
        if (isServiceBound)
            return;

        try {
            Intent intent = new Intent(this, MusicService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            showError("Lỗi kết nối service", e);
        }
    }

    private void unbindServiceIfNeeded() {
        if (isServiceBound) {
            try {
                unbindService(serviceConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isServiceBound = false;
        }
    }

    // ==================== MUSIC CONTROL ====================
    private void initializePlayer() {
        if (musicService == null) {
            txtStatus.setText("Service chưa sẵn sàng");
            return;
        }

        try {
            musicService.preparePlayer();
            int duration = musicService.getDuration();

            if (duration > 0) {
                seekBar.setMax(duration);
                txtTotalTime.setText(formatTime(duration));
                seekBar.setEnabled(true);
                txtStatus.setText("Ready");
            } else {
                txtStatus.setText("Không tìm thấy file nhạc. Vui lòng thêm song.mp3 vào res/raw/");
                seekBar.setEnabled(false);
            }

            updatePlayPauseButton();
            handler.post(updateSeekBar);
        } catch (Exception e) {
            showError("Lỗi khởi tạo player", e);
        }
    }

    private void togglePlayPause() {
        if (!isServiceReady())
            return;

        try {
            if (musicService.isPlaying()) {
                musicService.pause();
                txtStatus.setText("Paused");
            } else {
                musicService.play();
                txtStatus.setText("Playing");
                startProgressUpdates();
            }
            updatePlayPauseButton();
        } catch (Exception e) {
            showError("Lỗi phát nhạc", e);
        }
    }

    private void stopMusic() {
        if (!isServiceReady())
            return;

        try {
            musicService.stop();
            resetUI();
            txtStatus.setText("Stopped");
            updatePlayPauseButton();
        } catch (Exception e) {
            showError("Lỗi dừng nhạc", e);
        }
    }

    private void updateProgress() {
        if (musicService != null && musicService.isPlaying()) {
            int currentPosition = musicService.getCurrentPosition();
            int totalDuration = musicService.getDuration();

            if (totalDuration > 0) {
                seekBar.setMax(totalDuration);
                seekBar.setProgress(currentPosition);
                txtCurrentTime.setText(formatTime(currentPosition));
                txtTotalTime.setText(formatTime(totalDuration));
            }
            updatePlayPauseButton();
        }
        handler.postDelayed(updateSeekBar, UPDATE_INTERVAL_MS);
    }

    private void startProgressUpdates() {
        handler.removeCallbacks(updateSeekBar);
        handler.post(updateSeekBar);
    }

    private void resetUI() {
        seekBar.setProgress(0);
        txtCurrentTime.setText("0:00");
    }

    private void updatePlayPauseButton() {
        boolean isPlaying = musicService != null && musicService.isPlaying();
        btnPlayPause.setText(isPlaying ? "⏸" : "▶");
    }

    // ==================== UTILITIES ====================
    private String formatTime(int milliseconds) {
        int totalSeconds = milliseconds / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private boolean isServiceReady() {
        if (musicService == null) {
            txtStatus.setText("Service chưa sẵn sàng");
            return false;
        }
        return true;
    }

    private void showError(String message, Exception e) {
        e.printStackTrace();
        txtStatus.setText(message + ": " + e.getMessage());
    }
}
