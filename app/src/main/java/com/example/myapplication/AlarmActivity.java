package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.receiver.AlarmReceiver;
import com.example.myapplication.utils.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * AlarmActivity
 * - Màn hình đặt báo thức/nhắc nhở
 * - Sử dụng AlarmManager để đặt lịch
 * - Hiển thị notification khi đến giờ
 */
public class AlarmActivity extends AppCompatActivity {

    // ==================== CONSTANTS ====================
    private static final String PREFS_NAME = "AlarmPrefs";
    private static final String KEY_ALARM_TIME = "alarm_time";
    private static final String KEY_ALARM_MESSAGE = "alarm_message";
    private static final int ALARM_REQUEST_CODE = 100;

    // ==================== VIEW COMPONENTS ====================
    private EditText edtMinutes;
    private EditText edtMessage;
    private Button btnSetAlarm;
    private Button btnCancelAlarm;
    private TextView txtStatus;
    private TextView txtAlarmTime;

    // ==================== DATA ====================
    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;
    private SharedPreferences prefs;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        initializeViews();
        initializeData();
        checkNotificationPermission();
        loadAlarmStatus();
        setupListeners();
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        edtMinutes = findViewById(R.id.edtMinutes);
        edtMessage = findViewById(R.id.edtMessage);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        btnCancelAlarm = findViewById(R.id.btnCancelAlarm);
        txtStatus = findViewById(R.id.txtStatus);
        txtAlarmTime = findViewById(R.id.txtAlarmTime);
    }

    private void initializeData() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Tạo notification channel
        NotificationHelper.createNotificationChannel(this);
    }

    private void setupListeners() {
        btnSetAlarm.setOnClickListener(v -> setAlarm());
        btnCancelAlarm.setOnClickListener(v -> cancelAlarm());
    }

    // ==================== PERMISSIONS ====================
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        200);
            }
        }
    }

    // ==================== ALARM OPERATIONS ====================
    private void setAlarm() {
        String minutesStr = edtMinutes.getText().toString().trim();
        String message = edtMessage.getText().toString().trim();

        if (minutesStr.isEmpty()) {
            edtMinutes.setError("Vui lòng nhập số phút");
            edtMinutes.requestFocus();
            return;
        }

        try {
            int minutes = Integer.parseInt(minutesStr);
            if (minutes <= 0) {
                edtMinutes.setError("Số phút phải lớn hơn 0");
                edtMinutes.requestFocus();
                return;
            }

            btnSetAlarm.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            // Tính thời gian báo thức
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, minutes);
            long alarmTime = calendar.getTimeInMillis();

            // Tạo Intent cho AlarmReceiver
            Intent intent = new Intent(this, AlarmReceiver.class);
            if (!message.isEmpty()) {
                intent.putExtra(AlarmReceiver.EXTRA_MESSAGE, message);
            }
            alarmPendingIntent = PendingIntent.getBroadcast(
                    this, ALARM_REQUEST_CODE, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            // Đặt alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, alarmTime, alarmPendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, alarmPendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmPendingIntent);
            }

            // Lưu thông tin alarm
            saveAlarmStatus(alarmTime, message);

            // Hiển thị thông báo
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale.getDefault());
            String timeStr = sdf.format(new Date(alarmTime));
            txtStatus.setText("Đã đặt báo thức!");
            txtAlarmTime.setText("Thời gian: " + timeStr);
            Toast.makeText(this, "Đã đặt báo thức sau " + minutes + " phút", Toast.LENGTH_LONG).show();

        } catch (NumberFormatException e) {
            edtMinutes.setError("Vui lòng nhập số hợp lệ");
            edtMinutes.requestFocus();
        }
    }

    private void cancelAlarm() {
        if (alarmPendingIntent == null) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            alarmPendingIntent = PendingIntent.getBroadcast(
                    this, ALARM_REQUEST_CODE, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
        }

        if (alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
            alarmPendingIntent.cancel();
            alarmPendingIntent = null;
        }

        // Xóa thông tin đã lưu
        clearAlarmStatus();

        btnCancelAlarm.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.scale_in));

        txtStatus.setText("Đã hủy báo thức");
        txtAlarmTime.setText("");
        Toast.makeText(this, "Đã hủy báo thức", Toast.LENGTH_SHORT).show();
    }

    // ==================== DATA PERSISTENCE ====================
    private void saveAlarmStatus(long alarmTime, String message) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_ALARM_TIME, alarmTime);
        editor.putString(KEY_ALARM_MESSAGE, message);
        editor.apply();
    }

    private void loadAlarmStatus() {
        long alarmTime = prefs.getLong(KEY_ALARM_TIME, 0);
        if (alarmTime > 0 && alarmTime > System.currentTimeMillis()) {
            String message = prefs.getString(KEY_ALARM_MESSAGE, "");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale.getDefault());
            String timeStr = sdf.format(new Date(alarmTime));
            txtStatus.setText("Đã đặt báo thức");
            txtAlarmTime.setText("Thời gian: " + timeStr);
        } else {
            txtStatus.setText("Chưa đặt báo thức");
            txtAlarmTime.setText("");
        }
    }

    private void clearAlarmStatus() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ALARM_TIME);
        editor.remove(KEY_ALARM_MESSAGE);
        editor.apply();
    }
}

