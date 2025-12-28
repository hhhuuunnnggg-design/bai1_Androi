package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ScheduleAdapter;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Schedule;
import com.example.myapplication.database.ScheduleDao;
import com.example.myapplication.database.Student;
import com.example.myapplication.database.StudentDao;

import java.util.List;

/**
 * ScheduleActivity
 * - Hiển thị thời khóa biểu của sinh viên
 * - Cho phép điểm danh khi đúng thời gian học
 */
public class ScheduleActivity extends AppCompatActivity {

    // ==================== CONSTANTS ====================
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_STUDENT_ID = "student_id";

    // ==================== VIEW COMPONENTS ====================
    private RecyclerView recyclerViewSchedule;
    private TextView txtStudentInfo;
    private TextView txtEmpty;
    private Button btnLogout;

    // ==================== DATA ====================
    private AppDatabase database;
    private ScheduleDao scheduleDao;
    private StudentDao studentDao;
    private String studentId;
    private ScheduleAdapter adapter;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Kiểm tra đăng nhập
        if (!isLoggedIn()) {
            goToLogin();
            return;
        }

        initializeViews();
        initializeDatabase();
        loadStudentInfo();
        setupListeners();
        loadSchedules();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách khi quay lại (để cập nhật trạng thái thời gian)
        if (adapter != null) {
            loadSchedules();
        }
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        recyclerViewSchedule = findViewById(R.id.recyclerViewSchedule);
        txtStudentInfo = findViewById(R.id.txtStudentInfo);
        txtEmpty = findViewById(R.id.txtEmpty);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        scheduleDao = database.scheduleDao();
        studentDao = database.studentDao();
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> performLogout());
    }

    // ==================== DATA LOADING ====================
    private void loadStudentInfo() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        studentId = prefs.getString(KEY_STUDENT_ID, "");

        if (!studentId.isEmpty()) {
            Student student = studentDao.getStudentById(studentId);
            if (student != null) {
                txtStudentInfo.setText("Sinh viên: " + student.getName() + " (" + studentId + ")");
            }
        }
    }

    private void loadSchedules() {
        if (studentId == null || studentId.isEmpty()) {
            return;
        }

        List<Schedule> schedules = scheduleDao.getSchedulesByStudentId(studentId);

        if (schedules.isEmpty()) {
            recyclerViewSchedule.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewSchedule.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new ScheduleAdapter(schedules, studentId);
                recyclerViewSchedule.setAdapter(adapter);
            } else {
                adapter.updateSchedules(schedules);
            }
        }
    }

    // ==================== LOGOUT ====================
    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_logged_in", false);
        editor.remove(KEY_STUDENT_ID);
        editor.apply();

        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(ScheduleActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(
                R.anim.slide_in_top,
                R.anim.slide_out_bottom);
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }
}

