package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * ScheduleDao
 * - Data Access Object cho Schedule
 */
@Dao
public interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE studentId = :studentId ORDER BY dayOfWeek ASC, startHour ASC, startMinute ASC")
    List<Schedule> getSchedulesByStudentId(String studentId);

    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    Schedule getScheduleById(int scheduleId);

    @Query("SELECT * FROM schedules WHERE studentId = :studentId AND dayOfWeek = :dayOfWeek")
    List<Schedule> getSchedulesByDay(String studentId, String dayOfWeek);

    @Insert
    void insertSchedule(Schedule schedule);

    @Update
    void updateSchedule(Schedule schedule);
}
