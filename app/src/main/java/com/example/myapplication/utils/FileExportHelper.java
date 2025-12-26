package com.example.myapplication.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * FileExportHelper
 * - Utility class để xuất file ra external storage
 * - Xử lý permissions và kiểm tra storage availability
 */
public class FileExportHelper {

    private final Context context;

    public FileExportHelper(Context context) {
        this.context = context;
    }

    /**
     * Xuất file từ internal storage ra external storage (Downloads)
     */
    public boolean exportToExternalStorage(String internalFilename, String externalFilename) {
        // Kiểm tra external storage có sẵn không
        if (!isExternalStorageWritable()) {
            Toast.makeText(context, "Bộ nhớ ngoài không khả dụng", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            // File nguồn (internal storage)
            File sourceFile = new File(context.getFilesDir(), internalFilename);
            if (!sourceFile.exists()) {
                Toast.makeText(context, "File không tồn tại", Toast.LENGTH_SHORT).show();
                return false;
            }

            // File đích (external storage - Downloads folder)
            File downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            
            // Tạo thư mục nếu chưa tồn tại
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File destFile = new File(downloadsDir, externalFilename);

            // Copy file
            copyFile(sourceFile, destFile);

            Toast.makeText(context, 
                    "Đã xuất file thành công!\n" + destFile.getAbsolutePath(), 
                    Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi xuất file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Copy file từ source sang destination
     */
    private void copyFile(File sourceFile, File destFile) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(sourceFile).getChannel();
            destChannel = new FileOutputStream(destFile).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destChannel != null) {
                destChannel.close();
            }
        }
    }

    /**
     * Kiểm tra external storage có thể ghi không
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Lấy đường dẫn Downloads folder
     */
    public String getDownloadsPath() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        return downloadsDir.getAbsolutePath();
    }
}

