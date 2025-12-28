package com.example.myapplication.utils;

import com.example.myapplication.database.Schedule;

import java.util.Calendar;

/**
 * ScheduleTimeHelper
 * - Utility class để kiểm tra thời gian học
 * - Xác định sinh viên có thể điểm danh hay không
 */
public class ScheduleTimeHelper {

    /**
     * Kiểm tra xem có đang trong thời gian học không
     */
    public static boolean isWithinClassTime(Schedule schedule) {
        Calendar now = Calendar.getInstance();
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        // Lấy thứ trong tuần hiện tại (Calendar.DAY_OF_WEEK: 1=CN, 2=T2, ..., 7=T7)
        int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        int scheduleDay = convertDayOfWeek(schedule.getDayOfWeek());

        // Kiểm tra thứ trong tuần
        if (currentDayOfWeek != scheduleDay) {
            return false;
        }

        // Thiết lập thời gian bắt đầu và kết thúc
        startTime.set(Calendar.HOUR_OF_DAY, schedule.getStartHour());
        startTime.set(Calendar.MINUTE, schedule.getStartMinute());
        startTime.set(Calendar.SECOND, 0);

        endTime.set(Calendar.HOUR_OF_DAY, schedule.getEndHour());
        endTime.set(Calendar.MINUTE, schedule.getEndMinute());
        endTime.set(Calendar.SECOND, 0);

        // Kiểm tra thời gian hiện tại có nằm trong khoảng thời gian học không
        long currentTime = now.getTimeInMillis();
        long startTimeMillis = startTime.getTimeInMillis();
        long endTimeMillis = endTime.getTimeInMillis();

        // Cho phép điểm danh trong khoảng thời gian học (có thể mở rộng 5 phút trước và
        // sau)
        long buffer = 5 * 60 * 1000; // 5 phút
        return currentTime >= (startTimeMillis - buffer) && currentTime <= (endTimeMillis + buffer);
    }

    /**
     * Chuyển đổi thứ trong tuần từ String sang Calendar format
     * "2" -> Calendar.MONDAY, "3" -> Calendar.TUESDAY, ...
     */
    private static int convertDayOfWeek(String dayOfWeek) {
        try {
            int day = Integer.parseInt(dayOfWeek);
            // Calendar: 1=CN, 2=T2, 3=T3, ..., 7=T7
            // Schedule: "2"=T2, "3"=T3, ..., "7"=T7, "8"=CN
            if (day == 8) {
                return Calendar.SUNDAY; // 1
            } else {
                return day + 1; // "2" -> 3 (Calendar.MONDAY)
            }
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Lấy thời gian học dạng String
     */
    public static String getClassTimeString(Schedule schedule) {
        return String.format("%02d:%02d - %02d:%02d",
                schedule.getStartHour(), schedule.getStartMinute(),
                schedule.getEndHour(), schedule.getEndMinute());
    }

    /**
     * Lấy tên thứ trong tuần
     */
    public static String getDayName(String dayOfWeek) {
        switch (dayOfWeek) {
            case "2":
                return "Thứ Hai";
            case "3":
                return "Thứ Ba";
            case "4":
                return "Thứ Tư";
            case "5":
                return "Thứ Năm";
            case "6":
                return "Thứ Sáu";
            case "7":
                return "Thứ Bảy";
            case "8":
                return "Chủ Nhật";
            default:
                return "Không xác định";
        }
    }
}
