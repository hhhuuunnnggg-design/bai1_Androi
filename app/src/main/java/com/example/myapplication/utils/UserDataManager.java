package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

/**
 * UserDataManager
 * - Quản lý dữ liệu người dùng (lưu/load từ SharedPreferences)
 */
public class UserDataManager {
//Member variables
    private static final String PREFS_NAME = "UserProfile";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AVATAR = "avatar";

    private final SharedPreferences prefs;

    public UserDataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu tên người dùng
     */
    public void saveName(String name) {
        prefs.edit().putString(KEY_NAME, name).apply();
    }

    /**
     * Lưu email người dùng
     */
    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    /**
     * Lưu avatar (dạng Base64)
     */
    public void saveAvatar(String avatarBase64) {
        prefs.edit().putString(KEY_AVATAR, avatarBase64).apply();
    }

    /**
     * Lưu tất cả thông tin người dùng
     */
    public void saveUserData(String name, String email, String avatarBase64) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_AVATAR, avatarBase64);
        editor.apply();
    }

    /**
     * Lấy tên người dùng
     */
    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    /**
     * Lấy email người dùng
     */
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    /**
     * Lấy avatar (dạng Base64)
     */
    public String getAvatar() {
        return prefs.getString(KEY_AVATAR, "");
    }

    /**
     * Lấy avatar dạng Bitmap
     */
    public Bitmap getAvatarBitmap() {
        String avatarBase64 = getAvatar();
        if (avatarBase64.isEmpty()) {
            return null;
        }
        return ImageUtils.base64ToBitmap(avatarBase64);
    }
}
