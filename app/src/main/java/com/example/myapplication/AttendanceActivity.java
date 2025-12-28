package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Attendance;
import com.example.myapplication.database.AttendanceDao;
import com.example.myapplication.database.Schedule;
import com.example.myapplication.database.ScheduleDao;
import com.example.myapplication.utils.ScheduleTimeHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * AttendanceActivity
 * - Màn hình điểm danh với mã code
 * - Kiểm tra mã code hợp lệ và lưu điểm danh
 */
public class AttendanceActivity extends AppCompatActivity {

    // ==================== CONSTANTS ====================
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_STUDENT_ID = "student_id";

    // ==================== VIEW COMPONENTS ====================
    private TextView txtSubjectName;
    private TextView txtSubjectCode;
    private EditText edtAttendanceCode;
    private Button btnSave;
    private TextView txtStatus;

    // ==================== DATA ====================
    private AppDatabase database;
    private ScheduleDao scheduleDao;
    private AttendanceDao attendanceDao;
    private String studentId;
    private int scheduleId;
    private String correctAttendanceCode;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Lấy dữ liệu từ Intent
        scheduleId = getIntent().getIntExtra("schedule_id", -1);
        if (scheduleId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy lịch học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        initializeDatabase();
        loadStudentId();
        loadScheduleInfo();
        setupListeners();
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        txtSubjectName = findViewById(R.id.txtSubjectName);
        txtSubjectCode = findViewById(R.id.txtSubjectCode);
        edtAttendanceCode = findViewById(R.id.edtAttendanceCode);
        btnSave = findViewById(R.id.btnSave);
        txtStatus = findViewById(R.id.txtStatus);
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        scheduleDao = database.scheduleDao();
        attendanceDao = database.attendanceDao();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> performAttendance());
    }

    // ==================== DATA LOADING ====================
    private void loadStudentId() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        studentId = prefs.getString(KEY_STUDENT_ID, "");
    }

    private void loadScheduleInfo() {
        Schedule schedule = scheduleDao.getScheduleById(scheduleId);
        if (schedule != null) {
            txtSubjectName.setText(schedule.getSubjectName());
            txtSubjectCode.setText("Mã môn: " + schedule.getSubjectCode());
            correctAttendanceCode = schedule.getAttendanceCode();

            // Kiểm tra lại thời gian học
            if (!ScheduleTimeHelper.isWithinClassTime(schedule)) {
                txtStatus.setText("⚠️ Không trong giờ học! Không thể điểm danh.");
                btnSave.setEnabled(false);
            }
        }
    }

    // ==================== ATTENDANCE OPERATIONS ====================
    private void performAttendance() {
        String inputCode = edtAttendanceCode.getText().toString().trim().toUpperCase();

        if (inputCode.isEmpty()) {
            edtAttendanceCode.setError("Vui lòng nhập mã code");
            edtAttendanceCode.requestFocus();
            return;
        }

        btnSave.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));

        // Kiểm tra mã code
        if (!inputCode.equals(correctAttendanceCode)) {
            txtStatus.setText("❌ Mã code không đúng!");
            Toast.makeText(this, "Mã code không đúng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra lại thời gian học
        Schedule schedule = scheduleDao.getScheduleById(scheduleId);
        if (schedule == null || !ScheduleTimeHelper.isWithinClassTime(schedule)) {
            txtStatus.setText("❌ Không trong giờ học! Không thể điểm danh.");
            Toast.makeText(this, "Không trong giờ học", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra đã điểm danh chưa (trong ngày)
        String today = getCurrentDate();
        Attendance existingAttendance = attendanceDao.getAttendanceByScheduleAndDate(
                studentId, scheduleId, today);

        if (existingAttendance != null) {
            txtStatus.setText("⚠️ Bạn đã điểm danh hôm nay rồi!");
            Toast.makeText(this, "Đã điểm danh hôm nay", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu điểm danh
        String currentTime = getCurrentTime();
        Attendance attendance = new Attendance(
                studentId,
                scheduleId,
                schedule.getSubjectCode(),
                today,
                currentTime,
                true
        );

        attendanceDao.insertAttendance(attendance);

        txtStatus.setText("✅ Điểm danh thành công!");
        Toast.makeText(this, "Điểm danh thành công", Toast.LENGTH_SHORT).show();

        // Vô hiệu hóa nút sau khi điểm danh thành công
        btnSave.setEnabled(false);
        edtAttendanceCode.setEnabled(false);

        // Quay lại sau 2 giây
        new android.os.Handler().postDelayed(() -> {
            finish();
        }, 2000);
    }

    // ==================== UTILITY ====================
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }
}

