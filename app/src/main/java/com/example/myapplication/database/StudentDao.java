package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * StudentDao
 * - Data Access Object cho Student
 */
@Dao
public interface StudentDao {
    @Query("SELECT * FROM students WHERE studentId = :studentId AND password = :password")
    Student login(String studentId, String password);

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    Student getStudentById(String studentId);

    @Insert
    void insertStudent(Student student);
}
