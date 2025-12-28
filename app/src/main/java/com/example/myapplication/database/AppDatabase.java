package com.example.myapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * AppDatabase
 * - Room Database chính của ứng dụng
 * - Quản lý bảng contacts, students, schedules, attendances
 */
@Database(entities = {Contact.class, Student.class, Schedule.class, Attendance.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract ContactDao contactDao();
    public abstract StudentDao studentDao();
    public abstract ScheduleDao scheduleDao();
    public abstract AttendanceDao attendanceDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "app_database"
            ).allowMainThreadQueries() // Cho phép query trên main thread (đơn giản hóa)
              .fallbackToDestructiveMigration() // Xóa và tạo lại database khi version thay đổi
              .build();
        }
        return instance;
    }
}

