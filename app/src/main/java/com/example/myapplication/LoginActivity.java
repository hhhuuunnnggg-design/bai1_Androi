package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Student;
import com.example.myapplication.database.StudentDao;

/**
 * LoginActivity
 * - Màn hình đăng nhập với mã số sinh viên và mật khẩu
 */
public class LoginActivity extends AppCompatActivity {

    // ==================== CONSTANTS ====================
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // ==================== VIEW COMPONENTS ====================
    private EditText edtStudentId;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView txtStatus;

    // ==================== DATA ====================
    private AppDatabase database;
    private StudentDao studentDao;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Kiểm tra đã đăng nhập chưa
        if (isLoggedIn()) {
            goToSchedule();
            return;
        }

        initializeViews();
        initializeDatabase();
        setupListeners();
        initializeSampleData();
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        edtStudentId = findViewById(R.id.edtStudentId);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtStatus = findViewById(R.id.txtStatus);
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        studentDao = database.studentDao();
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
    }

    // ==================== LOGIN OPERATIONS ====================
    private void performLogin() {
        String studentId = edtStudentId.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (studentId.isEmpty()) {
            edtStudentId.setError("Vui lòng nhập mã số sinh viên");
            edtStudentId.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        btnLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));

        // Kiểm tra đăng nhập
        Student student = studentDao.login(studentId, password);

        if (student != null) {
            // Đăng nhập thành công
            saveLoginState(studentId);
            txtStatus.setText("Đăng nhập thành công!");
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

            // Chuyển đến màn hình thời khóa biểu
            new android.os.Handler().postDelayed(() -> {
                goToSchedule();
            }, 500);
        } else {
            // Đăng nhập thất bại
            txtStatus.setText("Mã số sinh viên hoặc mật khẩu không đúng!");
            Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToSchedule() {
        Intent intent = new Intent(LoginActivity.this, ScheduleActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(
                R.anim.slide_in_bottom,
                R.anim.slide_out_top);
    }

    // ==================== DATA PERSISTENCE ====================
    private void saveLoginState(String studentId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ==================== SAMPLE DATA ====================
    private void initializeSampleData() {
        // Tạo dữ liệu mẫu nếu chưa có
        Student existingStudent = studentDao.getStudentById("SV001");
        if (existingStudent == null) {
            // Tạo sinh viên mẫu
            Student sampleStudent = new Student("SV001", "123456", "Nguyễn Văn A", "sv001@example.com");
            studentDao.insertStudent(sampleStudent);
        }

        // Lấy thứ trong tuần hiện tại (Calendar: 1=CN, 2=T2, ..., 7=T7)
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        // Chuyển sang format của Schedule: "2"=T2, "3"=T3, ..., "7"=T7, "8"=CN
        String todayScheduleDay = String.valueOf(currentDayOfWeek == 1 ? 8 : currentDayOfWeek - 1);

        com.example.myapplication.database.ScheduleDao scheduleDao = database.scheduleDao();

        // Kiểm tra xem đã có lịch học hôm nay chưa
        java.util.List<com.example.myapplication.database.Schedule> todaySchedules = scheduleDao
                .getSchedulesByDay("SV001", todayScheduleDay);

        // Tạo lịch học buổi tối (18:00 - 20:00) cho hôm nay để có thể điểm danh
        boolean hasEveningClass = false;
        for (com.example.myapplication.database.Schedule s : todaySchedules) {
            if (s.getStartHour() == 18 && s.getStartMinute() == 0) {
                hasEveningClass = true;
                break;
            }
        }

        if (!hasEveningClass) {
            // Tạo lịch học buổi tối cho hôm nay
            com.example.myapplication.database.Schedule eveningSchedule = new com.example.myapplication.database.Schedule(
                    "SV001", "Thực hành Android", "AND002",
                    todayScheduleDay, 18, 0, 20, 0, "P301");
            eveningSchedule.setAttendanceCode("TEST123");
            scheduleDao.insertSchedule(eveningSchedule);
        }

        // Tạo thời khóa biểu mẫu cho các ngày khác (nếu chưa có)
        java.util.List<com.example.myapplication.database.Schedule> allSchedules = scheduleDao
                .getSchedulesByStudentId("SV001");

        boolean hasSchedule1 = false;
        boolean hasSchedule2 = false;
        for (com.example.myapplication.database.Schedule s : allSchedules) {
            if (s.getSubjectCode().equals("AND001")) {
                hasSchedule1 = true;
            }
            if (s.getSubjectCode().equals("DB001")) {
                hasSchedule2 = true;
            }
        }

        if (!hasSchedule1) {
            com.example.myapplication.database.Schedule schedule1 = new com.example.myapplication.database.Schedule(
                    "SV001", "Lập trình Android", "AND001",
                    "2", 7, 0, 9, 30, "P101");
            schedule1.setAttendanceCode("ABC123");
            scheduleDao.insertSchedule(schedule1);
        }

        if (!hasSchedule2) {
            com.example.myapplication.database.Schedule schedule2 = new com.example.myapplication.database.Schedule(
                    "SV001", "Cơ sở dữ liệu", "DB001",
                    "4", 13, 0, 15, 30, "P202");
            schedule2.setAttendanceCode("XYZ789");
            scheduleDao.insertSchedule(schedule2);
        }
    }
}
