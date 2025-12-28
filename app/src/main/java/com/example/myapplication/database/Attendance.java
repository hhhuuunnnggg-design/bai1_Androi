package com.example.myapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Attendance Entity
 * - Lịch sử điểm danh
 */
@Entity(tableName = "attendances")
public class Attendance {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String studentId; // Mã số sinh viên
    private int scheduleId; // ID của lịch học
    private String subjectCode; // Mã môn học
    private String attendanceDate; // Ngày điểm danh (yyyy-MM-dd)
    private String attendanceTime; // Giờ điểm danh (HH:mm:ss)
    private boolean isValid; // Điểm danh có hợp lệ không

    public Attendance() {
    }

    public Attendance(String studentId, int scheduleId, String subjectCode,
            String attendanceDate, String attendanceTime, boolean isValid) {
        this.studentId = studentId;
        this.scheduleId = scheduleId;
        this.subjectCode = subjectCode;
        this.attendanceDate = attendanceDate;
        this.attendanceTime = attendanceTime;
        this.isValid = isValid;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(String attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
