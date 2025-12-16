package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    // ==================== DATA ====================
    private UserDataManager dataManager;

    // ==================== LIFECYCLE ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // KHỞI TẠO Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    // ==================== ANIMATIONS ====================
    private void applyFadeInAnimations() {
        imgAvatar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        txtName.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        txtEmail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }
}
