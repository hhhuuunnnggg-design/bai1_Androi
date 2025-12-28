package com.example.myapplication.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AttendanceActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.Schedule;
import com.example.myapplication.utils.ScheduleTimeHelper;

import java.util.List;

/**
 * ScheduleAdapter
 * - Adapter cho RecyclerView hiển thị thời khóa biểu
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> schedules;
    private String studentId;
    private OnScheduleClickListener listener;

    public interface OnScheduleClickListener {
        void onScheduleClick(Schedule schedule);
    }

    public ScheduleAdapter(List<Schedule> schedules, String studentId) {
        this.schedules = schedules;
        this.studentId = studentId;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return schedules != null ? schedules.size() : 0;
    }

    public void updateSchedules(List<Schedule> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private TextView txtSubjectName;
        private TextView txtSubjectCode;
        private TextView txtDayTime;
        private TextView txtRoom;
        private TextView txtStatus;
        private Button btnAttendance;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSubjectName = itemView.findViewById(R.id.txtSubjectName);
            txtSubjectCode = itemView.findViewById(R.id.txtSubjectCode);
            txtDayTime = itemView.findViewById(R.id.txtDayTime);
            txtRoom = itemView.findViewById(R.id.txtRoom);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnAttendance = itemView.findViewById(R.id.btnAttendance);
        }

        public void bind(Schedule schedule) {
            txtSubjectName.setText(schedule.getSubjectName());
            txtSubjectCode.setText("Mã môn: " + schedule.getSubjectCode());

            String dayName = ScheduleTimeHelper.getDayName(schedule.getDayOfWeek());
            String time = ScheduleTimeHelper.getClassTimeString(schedule);
            txtDayTime.setText(dayName + ", " + time);
            txtRoom.setText(schedule.getRoom());

            // Kiểm tra thời gian học
            boolean isWithinTime = ScheduleTimeHelper.isWithinClassTime(schedule);

            if (isWithinTime) {
                txtStatus.setText("✅ Đang trong giờ học - Có thể điểm danh");
                txtStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.green_accent, null));
                btnAttendance.setEnabled(true);
                btnAttendance.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), AttendanceActivity.class);
                    intent.putExtra("schedule_id", schedule.getId());
                    intent.putExtra("subject_name", schedule.getSubjectName());
                    intent.putExtra("subject_code", schedule.getSubjectCode());
                    intent.putExtra("attendance_code", schedule.getAttendanceCode());
                    itemView.getContext().startActivity(intent);
                });
            } else {
                txtStatus.setText("⏰ Chưa đến giờ học");
                txtStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary, null));
                btnAttendance.setEnabled(false);
            }
        }
    }
}

