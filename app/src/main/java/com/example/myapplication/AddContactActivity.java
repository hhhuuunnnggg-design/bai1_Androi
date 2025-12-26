package com.example.myapplication;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Contact;
import com.example.myapplication.database.ContactDao;

/**
 * AddContactActivity
 * - Màn hình thêm/sửa liên hệ
 */
public class AddContactActivity extends AppCompatActivity {

    // ==================== VIEW COMPONENTS ====================
    private EditText edtName;
    private EditText edtPhone;
    private Button btnSave;
    private TextView txtStatus;

    // ==================== DATA ====================
    private AppDatabase database;
    private ContactDao contactDao;
    private Contact contactToEdit;
    private boolean isEditMode = false;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initializeViews();
        initializeDatabase();
        checkEditMode();
        setupListeners();
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);
        txtStatus = findViewById(R.id.txtStatus);
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        contactDao = database.contactDao();
    }

    private void checkEditMode() {
        int contactId = getIntent().getIntExtra("contact_id", -1);
        if (contactId != -1) {
            isEditMode = true;
            contactToEdit = contactDao.getContactById(contactId);
            if (contactToEdit != null) {
                edtName.setText(contactToEdit.getName());
                edtPhone.setText(contactToEdit.getPhone());
                btnSave.setText("💾 Cập Nhật");
                txtStatus.setText("Chế độ chỉnh sửa");
            }
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveContact());
    }

    // ==================== OPERATIONS ====================
    private void saveContact() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập tên");
            edtName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }

        btnSave.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_in));

        if (isEditMode && contactToEdit != null) {
            // Cập nhật liên hệ
            contactToEdit.setName(name);
            contactToEdit.setPhone(phone);
            contactDao.updateContact(contactToEdit);
            txtStatus.setText("Đã cập nhật liên hệ!");
            Toast.makeText(this, "Đã cập nhật liên hệ", Toast.LENGTH_SHORT).show();
        } else {
            // Thêm liên hệ mới
            Contact newContact = new Contact(name, phone);
            contactDao.insertContact(newContact);
            txtStatus.setText("Đã thêm liên hệ mới!");
            Toast.makeText(this, "Đã thêm liên hệ", Toast.LENGTH_SHORT).show();
        }

        // Xóa nội dung và quay lại sau 1 giây
        edtName.setText("");
        edtPhone.setText("");
        edtName.requestFocus();

        // Quay lại danh sách sau 1.5 giây
        new android.os.Handler().postDelayed(() -> {
            finish();
        }, 1500);
    }
}

