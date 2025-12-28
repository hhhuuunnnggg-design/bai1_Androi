package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * AttendanceDao
 * - Data Access Object cho Attendance
 */
@Dao
public interface AttendanceDao {
    @Query("SELECT * FROM attendances WHERE studentId = :studentId ORDER BY attendanceDate DESC, attendanceTime DESC")
    List<Attendance> getAttendancesByStudentId(String studentId);

    @Query("SELECT * FROM attendances WHERE studentId = :studentId AND scheduleId = :scheduleId AND attendanceDate = :date")
    Attendance getAttendanceByScheduleAndDate(String studentId, int scheduleId, String date);

    @Insert
    void insertAttendance(Attendance attendance);
}
