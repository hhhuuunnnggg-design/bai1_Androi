package com.example.myapplication.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * NoteManager
 * - Utility class để quản lý đọc/ghi file ghi chú
 * - Lưu vào internal storage (không cần permission)
 */
public class NoteManager {

    private static final String DEFAULT_FILENAME = "note.txt";
    private static final String NOTE_DIRECTORY = "notes";
    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    private static final String FILE_EXTENSION = ".txt";

    private final Context context;
    private final File notesDir;

    public NoteManager(Context context) {
        this.context = context;
        this.notesDir = new File(context.getFilesDir(), NOTE_DIRECTORY);
        // Tạo thư mục nếu chưa tồn tại
        if (!notesDir.exists()) {
            notesDir.mkdirs();
        }
    }

    /**
     * Lưu ghi chú vào file mặc định
     */
    public boolean saveNote(String content) {
        return saveNote(DEFAULT_FILENAME, content);
    }

    /**
     * Lưu ghi chú vào file với tên cụ thể
     */
    public boolean saveNote(String filename, String content) {
        try {
            File file = new File(context.getFilesDir(), filename);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lưu ghi chú với timestamp (mỗi note là 1 file)
     */
    public String saveNoteWithTimestamp(String content) {
        String timestamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                .format(new Date());
        String filename = "note_" + timestamp + FILE_EXTENSION;
        
        File file = new File(notesDir, filename);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Đọc ghi chú từ file mặc định
     */
    public String loadNote() {
        return loadNote(DEFAULT_FILENAME);
    }

    /**
     * Đọc ghi chú từ file với tên cụ thể
     */
    public String loadNote(String filename) {
        try {
            File file = new File(context.getFilesDir(), filename);
            if (!file.exists()) {
                return "";
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            reader.close();
            fis.close();

            // Xóa dòng trống cuối cùng nếu có
            String result = content.toString();
            if (result.endsWith("\n")) {
                result = result.substring(0, result.length() - 1);
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Lấy danh sách tất cả các file note đã lưu
     */
    public List<String> getAllNoteFiles() {
        List<String> files = new ArrayList<>();
        
        // Thêm file mặc định nếu tồn tại
        File defaultFile = new File(context.getFilesDir(), DEFAULT_FILENAME);
        if (defaultFile.exists()) {
            files.add(DEFAULT_FILENAME);
        }

        // Thêm các file trong thư mục notes
        File[] noteFiles = notesDir.listFiles();
        if (noteFiles != null) {
            for (File file : noteFiles) {
                if (file.isFile() && file.getName().endsWith(FILE_EXTENSION)) {
                    files.add(NOTE_DIRECTORY + "/" + file.getName());
                }
            }
        }

        return files;
    }

    /**
     * Lấy đường dẫn file trong internal storage
     */
    public String getInternalFilePath(String filename) {
        File file = new File(context.getFilesDir(), filename);
        return file.getAbsolutePath();
    }

    /**
     * Kiểm tra file có tồn tại không
     */
    public boolean fileExists(String filename) {
        File file = new File(context.getFilesDir(), filename);
        return file.exists();
    }

    /**
     * Xóa file
     */
    public boolean deleteFile(String filename) {
        File file = new File(context.getFilesDir(), filename);
        return file.exists() && file.delete();
    }
}

