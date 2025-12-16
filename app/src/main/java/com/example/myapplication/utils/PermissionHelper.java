package com.example.myapplication.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

/**
 * PermissionHelper
 * - Utility class để kiểm tra và xử lý permissions
 */
public class PermissionHelper {

    /**
     * Kiểm tra quyền Camera, Đã được cấp quyền camera chưa?
     */
    public static boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Kiểm tra quyền đọc Storage (cho Android < 13), Hiện tại có được phép đọc ảnh không?
     */
    public static boolean hasStoragePermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ không cần READ_EXTERNAL_STORAGE
            return true;
        }
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Kiểm tra có cần xin quyền Storage không (Android < 13), Có cần phải xin quyền storage không?
     */
    public static boolean needsStoragePermission() {
        return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU;
    }
}
