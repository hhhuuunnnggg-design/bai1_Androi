package com.example.myapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Contact Entity
 * - Bảng contacts trong database
 * - id: PRIMARY KEY AUTOINCREMENT
 * - name: Tên liên hệ
 * - phone: Số điện thoại
 */
@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String phone;

    public Contact() {
    }

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

