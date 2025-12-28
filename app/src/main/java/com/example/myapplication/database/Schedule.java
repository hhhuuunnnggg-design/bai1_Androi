package com.example.myapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Schedule Entity
 * - Thời khóa biểu của sinh viên
 */
@Entity(tableName = "schedules")
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String studentId; // Mã số sinh viên
    private String subjectName; // Tên môn học
    private String subjectCode; // Mã môn học
    private String dayOfWeek; // Thứ trong tuần (2-7, CN=8)
    private int startHour; // Giờ bắt đầu (0-23)
    private int startMinute; // Phút bắt đầu (0-59)
    private int endHour; // Giờ kết thúc
    private int endMinute; // Phút kết thúc
    private String room; // Phòng học
    private String attendanceCode; // Mã điểm danh (do giảng viên cung cấp)

    public Schedule() {
    }

    public Schedule(String studentId, String subjectName, String subjectCode,
            String dayOfWeek, int startHour, int startMinute,
            int endHour, int endMinute, String room) {
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.room = room;
        this.attendanceCode = "";
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getAttendanceCode() {
        return attendanceCode;
    }

    public void setAttendanceCode(String attendanceCode) {
        this.attendanceCode = attendanceCode;
    }
}
