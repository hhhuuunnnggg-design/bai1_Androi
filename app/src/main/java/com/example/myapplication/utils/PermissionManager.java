package com.example.myapplication.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionManager
 * - Utility class để quản lý runtime permissions
 * - Kiểm tra và xin nhiều quyền cùng lúc
 */
public class PermissionManager {

    /**
     * Lấy danh sách các quyền cần xin
     */
    public static List<String> getRequiredPermissions(Context context) {
        List<String> permissions = new ArrayList<>();

        // Camera permission (luôn cần)
        permissions.add(Manifest.permission.CAMERA);

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Storage permission (chỉ Android < 13)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        return permissions;
    }

    /**
     * Lấy danh sách các quyền chưa được cấp
     */
    public static List<String> getMissingPermissions(Context context) {
        List<String> missingPermissions = new ArrayList<>();
        List<String> requiredPermissions = getRequiredPermissions(context);

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        return missingPermissions;
    }

    /**
     * Kiểm tra quyền Camera
     */
    public static boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Kiểm tra quyền Notification
     */
    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Android < 13 không cần permission
    }

    /**
     * Kiểm tra quyền Storage
     */
    public static boolean hasStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true; // Android 13+ không cần READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Kiểm tra tất cả quyền đã được cấp chưa
     */
    public static boolean hasAllPermissions(Context context) {
        return getMissingPermissions(context).isEmpty();
    }

    /**
     * Lấy tên hiển thị của quyền
     */
    public static String getPermissionDisplayName(String permission) {
        if (permission.equals(Manifest.permission.CAMERA)) {
            return "Camera";
        } else if (permission.equals(Manifest.permission.POST_NOTIFICATIONS)) {
            return "Thông Báo";
        } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return "Bộ Nhớ";
        }
        return permission;
    }

    /**
     * Lấy hướng dẫn cấp quyền thủ công
     */
    public static String getPermissionGuide(String permission) {
        if (permission.equals(Manifest.permission.CAMERA)) {
            return "Vào Cài đặt > Ứng dụng > " + "Thông Tin Cá Nhân" + " > Quyền > Camera > Cho phép";
        } else if (permission.equals(Manifest.permission.POST_NOTIFICATIONS)) {
            return "Vào Cài đặt > Ứng dụng > " + "Thông Tin Cá Nhân" + " > Quyền > Thông báo > Cho phép";
        } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return "Vào Cài đặt > Ứng dụng > " + "Thông Tin Cá Nhân" + " > Quyền > Bộ nhớ > Cho phép";
        }
        return "Vào Cài đặt > Ứng dụng để cấp quyền thủ công";
    }
}


