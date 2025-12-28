package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.utils.UserDataManager;

/**
 * MainActivity
 * - Màn hình hiển thị thông tin người dùng (Avatar, Tên, Email)
 * - Dữ liệu được lưu bằng SharedPreferences
 * - Có nút chuyển sang EditActivity để chỉnh sửa thông tin
 */
public class MainActivity extends AppCompatActivity {

    // ==================== VIEW COMPONENTS, Member variables ====================
    private ImageView imgAvatar;
    private TextView txtName;
    private TextView txtEmail;
    private Button btnEdit;
    private Button btnMusic;
    private Button btnNote;
    private Button btnContact;
    private Button btnAlarm;
    private Button btnDetail;
    private Button btnPermission;
    private Button btnAttendance;

    // ==================== DATA ====================
    private UserDataManager dataManager;

    // ==================== LIFECYCLE ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // KHỞI TẠO Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar để hiển thị menu 3 chấm
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
        initializeDataManager();
        loadUserData();
        setupListeners();
    }

    @Override
    protected void onResume() {// MÀN HÌNH QUAY LẠI & CÓ THỂ TƯƠNG TÁC
        super.onResume();
        loadUserData();
        applyFadeInAnimations();
    }
    // gọi khi Activity quay lại trạng thái tương tác, dùng để cập nhật lại dữ liệu
    // và giao diện cho người dùng.

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        btnEdit = findViewById(R.id.btnEdit);
        btnMusic = findViewById(R.id.btnMusic);
        btnNote = findViewById(R.id.btnNote);
        btnContact = findViewById(R.id.btnContact);
        btnAlarm = findViewById(R.id.btnAlarm);
        btnDetail = findViewById(R.id.btnDetail);
        btnPermission = findViewById(R.id.btnPermission);
        btnAttendance = findViewById(R.id.btnAttendance);
    }

    private void initializeDataManager() {
        dataManager = new UserDataManager(this);
    }

    // ==================== DATA HANDLING ====================
    private void loadUserData() {
        String name = dataManager.getName();
        String email = dataManager.getEmail();

        // Sử dụng hint mặc định nếu rỗng
        txtName.setText(name.isEmpty() ? getString(R.string.hint_name) : name);
        txtEmail.setText(email.isEmpty() ? getString(R.string.hint_email) : email);

        Bitmap avatarBitmap = dataManager.getAvatarBitmap();
        if (avatarBitmap != null) {
            imgAvatar.setImageBitmap(avatarBitmap);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    // ==================== EVENTS ====================
    private void setupListeners() {
        btnEdit.setOnClickListener(v -> {
            btnEdit.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left);
        });

        btnMusic.setOnClickListener(v -> {
            btnMusic.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            Intent intent = new Intent(MainActivity.this, MusicActivity.class);
            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top);
        });

        btnNote.setOnClickListener(v -> {
            btnNote.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top);
        });

        btnContact.setOnClickListener(v -> {
            btnContact.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top);
        });

        btnAlarm.setOnClickListener(v -> {
            btnAlarm.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top);
        });

        btnDetail.setOnClickListener(v -> {
            btnDetail.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            // Gửi Implicit Intent với custom action
            sendImplicitIntent();
        });

        btnPermission.setOnClickListener(v -> {
            btnPermission.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            Intent intent = new Intent(MainActivity.this, PermissionActivity.class);
            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top);
        });

        btnAttendance.setOnClickListener(v -> {
            btnAttendance.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

            overridePendingTransition(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top);
        });
    }

    // ==================== IMPLICIT INTENT ====================
    private void sendImplicitIntent() {
        // Tạo Implicit Intent với custom action
        Intent intent = new Intent("com.example.ACTION_DETAIL");

        // Thêm extras
        intent.putExtra("title", "Thông Tin Chi Tiết");
        intent.putExtra("content",
                "Đây là nội dung được truyền từ MainActivity thông qua Implicit Intent với custom action.");
        intent.putExtra("extra_data", "Dữ liệu bổ sung: " + System.currentTimeMillis());

        // Kiểm tra xem có Activity nào có thể xử lý Intent này không
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            overridePendingTransition(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_top);
        } else {
            android.widget.Toast.makeText(this,
                    "Không tìm thấy Activity để xử lý Intent này",
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== ANIMATIONS ====================
    private void applyFadeInAnimations() {
        imgAvatar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        txtName.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        txtEmail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    // ==================== MENU ====================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAbout) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Giới thiệu");
        builder.setMessage("Ứng dụng gợi ý phim - Nơi bạn tìm được bộ phim yêu thích nhất!");
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.create().show();
    }
}
