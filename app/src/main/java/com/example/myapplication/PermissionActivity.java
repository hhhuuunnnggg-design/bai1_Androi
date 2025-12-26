package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.utils.PermissionManager;

import java.util.List;

/**
 * PermissionActivity
 * - Màn hình quản lý và xin runtime permissions
 * - Xin nhiều quyền cùng lúc
 * - Hiển thị hướng dẫn khi bị từ chối
 */
public class PermissionActivity extends AppCompatActivity {

    // ==================== CONSTANTS ====================
    private static final int PERMISSION_REQUEST_CODE = 100;

    // ==================== VIEW COMPONENTS ====================
    private TextView txtCameraStatus;
    private TextView txtNotificationStatus;
    private TextView txtStorageStatus;
    private TextView txtStatus;
    private Button btnRequestAll;
    private LinearLayout layoutStorage;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        initializeViews();
        setupListeners();
        updatePermissionStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionStatus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            handlePermissionResult(permissions, grantResults);
        }
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        txtCameraStatus = findViewById(R.id.txtCameraStatus);
        txtNotificationStatus = findViewById(R.id.txtNotificationStatus);
        txtStorageStatus = findViewById(R.id.txtStorageStatus);
        txtStatus = findViewById(R.id.txtStatus);
        btnRequestAll = findViewById(R.id.btnRequestAll);
        layoutStorage = findViewById(R.id.layoutStorage);

        // Ẩn Storage permission cho Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            layoutStorage.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnRequestAll.setOnClickListener(v -> requestAllPermissions());
    }

    // ==================== PERMISSION OPERATIONS ====================
    private void requestAllPermissions() {
        btnRequestAll.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.scale_in));

        List<String> missingPermissions = PermissionManager.getMissingPermissions(this);

        if (missingPermissions.isEmpty()) {
            txtStatus.setText("Tất cả quyền đã được cấp!");
            Toast.makeText(this, "Tất cả quyền đã được cấp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển List sang Array
        String[] permissionsArray = missingPermissions.toArray(new String[0]);

        // Xin tất cả quyền cùng lúc
        ActivityCompat.requestPermissions(this, permissionsArray, PERMISSION_REQUEST_CODE);
        txtStatus.setText("Đang xin quyền...");
    }

    private void handlePermissionResult(String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new java.util.ArrayList<>();
        List<String> grantedPermissions = new java.util.ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i]);
            } else {
                deniedPermissions.add(permissions[i]);
            }
        }

        // Cập nhật trạng thái
        updatePermissionStatus();

        // Hiển thị kết quả
        if (deniedPermissions.isEmpty()) {
            txtStatus.setText("✅ Đã cấp tất cả quyền!");
            Toast.makeText(this, "Đã cấp tất cả quyền", Toast.LENGTH_SHORT).show();
        } else {
            txtStatus.setText("⚠️ Một số quyền bị từ chối");
            
            // Kiểm tra xem có quyền nào bị từ chối vĩnh viễn không
            boolean shouldShowRationale = false;
            for (String permission : deniedPermissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    shouldShowRationale = true;
                    break;
                }
            }

            if (!shouldShowRationale) {
                // Quyền bị từ chối vĩnh viễn, hiển thị hướng dẫn
                showPermissionDeniedDialog(deniedPermissions);
            } else {
                // Quyền bị từ chối nhưng có thể xin lại
                showPermissionRationale(deniedPermissions);
            }
        }
    }

    private void showPermissionDeniedDialog(List<String> deniedPermissions) {
        StringBuilder message = new StringBuilder("Các quyền sau bị từ chối:\n\n");
        for (String permission : deniedPermissions) {
            message.append("• ").append(PermissionManager.getPermissionDisplayName(permission)).append("\n");
        }
        message.append("\nVui lòng cấp quyền thủ công trong Cài đặt.");

        new AlertDialog.Builder(this)
                .setTitle("Quyền bị từ chối")
                .setMessage(message.toString())
                .setPositiveButton("Mở Cài đặt", (dialog, which) -> openAppSettings())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showPermissionRationale(List<String> deniedPermissions) {
        StringBuilder message = new StringBuilder("Ứng dụng cần các quyền sau để hoạt động:\n\n");
        for (String permission : deniedPermissions) {
            message.append("• ").append(PermissionManager.getPermissionDisplayName(permission)).append("\n");
        }
        message.append("\nVui lòng cấp quyền để sử dụng đầy đủ tính năng.");

        new AlertDialog.Builder(this)
                .setTitle("Cần quyền")
                .setMessage(message.toString())
                .setPositiveButton("Xin lại", (dialog, which) -> requestAllPermissions())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    // ==================== UPDATE UI ====================
    private void updatePermissionStatus() {
        // Camera
        if (PermissionManager.hasCameraPermission(this)) {
            txtCameraStatus.setText("✅ Đã cấp");
            txtCameraStatus.setTextColor(getResources().getColor(R.color.green_accent, null));
        } else {
            txtCameraStatus.setText("❌ Chưa cấp");
            txtCameraStatus.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }

        // Notification
        if (PermissionManager.hasNotificationPermission(this)) {
            txtNotificationStatus.setText("✅ Đã cấp");
            txtNotificationStatus.setTextColor(getResources().getColor(R.color.green_accent, null));
        } else {
            txtNotificationStatus.setText("❌ Chưa cấp");
            txtNotificationStatus.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }

        // Storage
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (PermissionManager.hasStoragePermission(this)) {
                txtStorageStatus.setText("✅ Đã cấp");
                txtStorageStatus.setTextColor(getResources().getColor(R.color.green_accent, null));
            } else {
                txtStorageStatus.setText("❌ Chưa cấp");
                txtStorageStatus.setTextColor(getResources().getColor(R.color.text_secondary, null));
            }
        }

        // Cập nhật trạng thái tổng
        if (PermissionManager.hasAllPermissions(this)) {
            txtStatus.setText("✅ Tất cả quyền đã được cấp");
        } else {
            List<String> missing = PermissionManager.getMissingPermissions(this);
            txtStatus.setText("Còn " + missing.size() + " quyền chưa được cấp");
        }
    }
}

