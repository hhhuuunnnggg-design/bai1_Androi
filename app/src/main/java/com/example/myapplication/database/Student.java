package com.example.myapplication.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Student Entity
 * - Thông tin sinh viên
 */
@Entity(tableName = "students")
public class Student {
    @PrimaryKey
    @NonNull
    private String studentId; // Mã số sinh viên

    private String password;
    private String name;
    private String email;

    public Student() {
    }

    public Student(String studentId, String password, String name, String email) {
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
