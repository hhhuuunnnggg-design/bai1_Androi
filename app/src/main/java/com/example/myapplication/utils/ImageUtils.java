package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * ImageUtils
 * - Utility class để xử lý ảnh (Bitmap, Base64, Scale)
 */
public class ImageUtils {

    /**
     * Chuyển Bitmap sang Base64 để lưu
     *
     * @param bitmap Bitmap cần chuyển đổi
     * @return chuỗi Base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return "";
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * Chuyển Base64 về Bitmap
     *
     * @param base64String chuỗi Base64
     * @return Bitmap hoặc null nếu lỗi
     */
    public static Bitmap base64ToBitmap(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(
                    decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Scale ảnh để tiết kiệm bộ nhớ
     *
     * @param bitmap    Bitmap gốc
     * @param maxWidth  chiều rộng tối đa
     * @param maxHeight chiều cao tối đa
     * @return Bitmap đã được scale
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = Math.min(
                (float) maxWidth / width,
                (float) maxHeight / height);

        int newWidth = Math.round(scale * width);
        int newHeight = Math.round(scale * height);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
