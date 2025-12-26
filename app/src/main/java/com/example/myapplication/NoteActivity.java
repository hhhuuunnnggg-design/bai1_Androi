package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.utils.FileExportHelper;
import com.example.myapplication.utils.NoteManager;

/**
 * NoteActivity
 * - Ứng dụng ghi chú đơn giản
 * - Đọc/ghi file vào internal storage
 * - Hỗ trợ lưu theo thời gian và xuất ra external storage
 */
public class NoteActivity extends AppCompatActivity {

    // ==================== CONSTANTS ====================
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;

    // ==================== VIEW COMPONENTS ====================
    private EditText edtNote;
    private Button btnSave;
    private Button btnLoad;
    private Button btnSaveWithTime;
    private Button btnExport;
    private TextView txtStatus;

    // ==================== DATA ====================
    private NoteManager noteManager;
    private FileExportHelper exportHelper;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initializeViews();
        initializeManagers();
        setupListeners();
        loadNote();
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        edtNote = findViewById(R.id.edtNote);
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);
        btnSaveWithTime = findViewById(R.id.btnSaveWithTime);
        btnExport = findViewById(R.id.btnExport);
        txtStatus = findViewById(R.id.txtStatus);
    }

    private void initializeManagers() {
        noteManager = new NoteManager(this);
        exportHelper = new FileExportHelper(this);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveNote());
        btnLoad.setOnClickListener(v -> loadNote());
        btnSaveWithTime.setOnClickListener(v -> saveNoteWithTimestamp());
        btnExport.setOnClickListener(v -> exportNote());
    }

    // ==================== NOTE OPERATIONS ====================
    private void saveNote() {
        String content = edtNote.getText().toString().trim();
        
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung ghi chú", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));

        if (noteManager.saveNote(content)) {
            txtStatus.setText("Đã lưu thành công!");
            Toast.makeText(this, "Đã lưu ghi chú", Toast.LENGTH_SHORT).show();
        } else {
            txtStatus.setText("Lỗi khi lưu!");
            Toast.makeText(this, "Lỗi khi lưu ghi chú", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNote() {
        btnLoad.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));

        String content = noteManager.loadNote();
        
        if (content.isEmpty()) {
            edtNote.setText("");
            txtStatus.setText("Chưa có ghi chú nào được lưu");
            Toast.makeText(this, "Chưa có ghi chú nào", Toast.LENGTH_SHORT).show();
        } else {
            edtNote.setText(content);
            txtStatus.setText("Đã tải ghi chú thành công!");
            Toast.makeText(this, "Đã tải ghi chú", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNoteWithTimestamp() {
        String content = edtNote.getText().toString().trim();
        
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung ghi chú", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveWithTime.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));

        String filename = noteManager.saveNoteWithTimestamp(content);
        
        if (filename != null) {
            txtStatus.setText("Đã lưu với timestamp: " + filename);
            Toast.makeText(this, "Đã lưu: " + filename, Toast.LENGTH_LONG).show();
        } else {
            txtStatus.setText("Lỗi khi lưu!");
            Toast.makeText(this, "Lỗi khi lưu ghi chú", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportNote() {
        String content = edtNote.getText().toString().trim();
        
        if (content.isEmpty()) {
            Toast.makeText(this, "Không có nội dung để xuất", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra permission cho Android 6.0+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ không cần WRITE_EXTERNAL_STORAGE
            performExport();
        } else {
            // Android < 13 cần permission
            if (checkStoragePermission()) {
                performExport();
            } else {
                requestStoragePermission();
            }
        }
    }

    private void performExport() {
        btnExport.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));

        // Lưu nội dung hiện tại vào file tạm
        String tempFilename = "export_note.txt";
        if (noteManager.saveNote(tempFilename, edtNote.getText().toString())) {
            // Xuất file ra external storage
            String exportFilename = "note_" + System.currentTimeMillis() + ".txt";
            if (exportHelper.exportToExternalStorage(tempFilename, exportFilename)) {
                txtStatus.setText("Đã xuất file ra: " + exportHelper.getDownloadsPath());
            }
        } else {
            Toast.makeText(this, "Lỗi khi chuẩn bị file để xuất", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== PERMISSIONS ====================
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performExport();
            } else {
                Toast.makeText(this, "Cần quyền để xuất file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

