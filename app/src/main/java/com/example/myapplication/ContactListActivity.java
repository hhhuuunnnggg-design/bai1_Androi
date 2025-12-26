package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ContactAdapter;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.Contact;
import com.example.myapplication.database.ContactDao;

import java.util.List;

/**
 * ContactListActivity
 * - Màn hình hiển thị danh sách liên hệ
 * - Hỗ trợ thêm, sửa, xóa liên hệ
 */
public class ContactListActivity extends AppCompatActivity {

    // ==================== VIEW COMPONENTS ====================
    private RecyclerView recyclerViewContacts;
    private TextView txtEmpty;
    private Button btnAdd;

    // ==================== DATA ====================
    private AppDatabase database;
    private ContactDao contactDao;
    private ContactAdapter adapter;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initializeViews();
        initializeDatabase();
        setupRecyclerView();
        setupListeners();
        loadContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        txtEmpty = findViewById(R.id.txtEmpty);
        btnAdd = findViewById(R.id.btnAdd);
    }

    private void initializeDatabase() {
        database = AppDatabase.getInstance(this);
        contactDao = database.contactDao();
    }

    private void setupRecyclerView() {
        adapter = new ContactAdapter();
        adapter.setOnContactClickListener(new ContactAdapter.OnContactClickListener() {
            @Override
            public void onEditClick(Contact contact) {
                editContact(contact);
            }

            @Override
            public void onDeleteClick(Contact contact) {
                deleteContact(contact);
            }
        });

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContacts.setAdapter(adapter);
    }

    private void setupListeners() {
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ContactListActivity.this, AddContactActivity.class);
            startActivity(intent);
        });
    }

    // ==================== OPERATIONS ====================
    private void loadContacts() {
        List<Contact> contacts = contactDao.getAllContacts();
        adapter.setContacts(contacts);

        // Hiển thị/ẩn empty state
        if (contacts.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            recyclerViewContacts.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            recyclerViewContacts.setVisibility(View.VISIBLE);
        }
    }

    private void editContact(Contact contact) {
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("contact_id", contact.getId());
        startActivity(intent);
    }

    private void deleteContact(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa liên hệ")
                .setMessage("Bạn có chắc muốn xóa " + contact.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    contactDao.deleteContact(contact);
                    Toast.makeText(this, "Đã xóa liên hệ", Toast.LENGTH_SHORT).show();
                    loadContacts();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

