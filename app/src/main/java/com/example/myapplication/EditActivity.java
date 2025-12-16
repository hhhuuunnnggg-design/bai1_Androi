package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utils.EmailValidator;
import com.example.myapplication.utils.ImageUtils;
import com.example.myapplication.utils.PermissionHelper;
import com.example.myapplication.utils.UserDataManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * EditActivity
 * - Màn hình chỉnh sửa thông tin người dùng
 * - Cho phép sửa tên, email, avatar
 * - Avatar có thể chọn từ Gallery hoặc Camera
 */
public class EditActivity extends AppCompatActivity {

    // ==================== VIEW COMPONENTS, Member variables ====================
    private ImageView imgAvatarEdit;
    private EditText edtName;
    private EditText edtEmail;
    private Button btnSave;
    private Button btnChangeAvatar;

    // ==================== DATA ====================
    private UserDataManager dataManager;
    private String currentAvatarBase64 = "";

    // ==================== ACTIVITY RESULT LAUNCHERS ====================
    private ActivityResultLauncher<Intent> galleryLauncher; //Mở thư viện ảnh
    private ActivityResultLauncher<Intent> cameraLauncher; // mở camera
    private ActivityResultLauncher<String> cameraPermissionLauncher; // xin quyền mở camera
    private ActivityResultLauncher<String> storagePermissionLauncher; //Xin quyền đọc bộ nhớ / ảnh

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initializeViews();
        initializeDataManager();
        setupActivityResultLaunchers();
        loadUserData();
        setupListeners();

        // Animation cho avatar
        imgAvatarEdit.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.scale_in));
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        imgAvatarEdit = findViewById(R.id.imgAvatarEdit);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
    }

    private void initializeDataManager() {
        dataManager = new UserDataManager(this);
    }

    // ==================== ACTIVITY RESULT ====================
    private void setupActivityResultLaunchers() {
        setupGalleryLauncher();
        setupCameraLauncher();
        setupPermissionLaunchers();
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleImageFromGallery(imageUri);
                        }
                    }
                });
    }

    private void setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap bitmap = (Bitmap) extras.get("data");
                            if (bitmap != null) {
                                handleImageFromCamera(bitmap);
                            }
                        }
                    }
                });
    }

    private void setupPermissionLaunchers() {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        showPermissionDeniedMessage();
                    }
                });

        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        showPermissionDeniedMessage();
                    }
                });
    }

    // ==================== DATA HANDLING ====================
    private void loadUserData() {
        edtName.setText(dataManager.getName());
        edtEmail.setText(dataManager.getEmail());

        Bitmap avatarBitmap = dataManager.getAvatarBitmap();
        if (avatarBitmap != null) {
            imgAvatarEdit.setImageBitmap(avatarBitmap);
            currentAvatarBase64 = dataManager.getAvatar();
        }
    }

    // ==================== EVENTS ====================
    private void setupListeners() {
        btnChangeAvatar.setOnClickListener(v -> showImagePickerDialog());
        btnSave.setOnClickListener(v -> {
            btnSave.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.scale_in));
            saveUserData();
        });
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_photo);

        String[] options = {
                getString(R.string.gallery),
                getString(R.string.camera)
        };

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkStoragePermissionAndOpenGallery();
            } else {
                checkCameraPermissionAndOpen();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // ==================== PERMISSIONS ====================
    private void checkStoragePermissionAndOpenGallery() {
        if (PermissionHelper.needsStoragePermission()) {
            if (PermissionHelper.hasStoragePermission(this)) {
                openGallery();
            } else {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            openGallery();
        }
    }

    private void checkCameraPermissionAndOpen() {
        if (PermissionHelper.hasCameraPermission(this)) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void showPermissionDeniedMessage() {
        Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
    }

    // ==================== OPEN INTENT ====================
    // mo anh trong thu vien
    private void openGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== IMAGE HANDLING ====================
    private void handleImageFromGallery(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap scaledBitmap = ImageUtils.scaleBitmap(bitmap, 500, 500);
            displayImage(scaledBitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImageFromCamera(Bitmap bitmap) {
        Bitmap scaledBitmap = ImageUtils.scaleBitmap(bitmap, 500, 500);
        displayImage(scaledBitmap);
    }

    private void displayImage(Bitmap bitmap) {
        imgAvatarEdit.setImageBitmap(bitmap);
        currentAvatarBase64 = ImageUtils.bitmapToBase64(bitmap);
        imgAvatarEdit.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.scale_in));
    }

    // ==================== SAVE DATA ====================
    private void saveUserData() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (!validateInput(name, email)) {
            return;
        }

        dataManager.saveUserData(name, email, currentAvatarBase64);

        Toast.makeText(this, R.string.saved_success, Toast.LENGTH_SHORT).show();

        finish();
        overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    private boolean validateInput(String name, String email) {
        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập tên");
            edtName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return false;
        }

        if (!EmailValidator.isValid(email)) {
            edtEmail.setError("Email không hợp lệ. Vui lòng nhập đúng định dạng email");
            edtEmail.requestFocus();
            return false;
        }

        return true;
    }
}
