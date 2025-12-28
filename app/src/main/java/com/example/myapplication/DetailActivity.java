package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * DetailActivity
 * - Màn hình nhận Intent thông qua Intent Filter với custom action
 * - Hiển thị thông tin từ Intent extras
 */
public class DetailActivity extends AppCompatActivity {

    // ==================== CONSTANTS ====================
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_DATA = "extra_data";

    // ==================== VIEW COMPONENTS ====================
    private TextView txtTitle;
    private TextView txtContent;
    private TextView txtExtra;
    private TextView txtIntentAction;
    private TextView txtIntentData;

    // ==================== LIFECYCLE ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeViews();
        processIntent();
    }

    // ==================== INITIALIZATION ====================
    private void initializeViews() {
        txtTitle = findViewById(R.id.txtTitle);
        txtContent = findViewById(R.id.txtContent);
        txtExtra = findViewById(R.id.txtExtra);
        txtIntentAction = findViewById(R.id.txtIntentAction);
        txtIntentData = findViewById(R.id.txtIntentData);
    }

    // ==================== INTENT PROCESSING ====================
    private void processIntent() {
        Intent intent = getIntent();
        
        if (intent != null) {
            // Hiển thị thông tin Intent
            String action = intent.getAction();
            Uri data = intent.getData();
            
            txtIntentAction.setText("Action: " + (action != null ? action : "null"));
            txtIntentData.setText("Data: " + (data != null ? data.toString() : "null"));

            // Lấy dữ liệu từ extras
            String title = intent.getStringExtra(EXTRA_TITLE);
            String content = intent.getStringExtra(EXTRA_CONTENT);
            String extraData = intent.getStringExtra(EXTRA_DATA);

            // Hiển thị dữ liệu
            if (title != null && !title.isEmpty()) {
                txtTitle.setText(title);
            } else {
                txtTitle.setText("Không có tiêu đề");
            }

            if (content != null && !content.isEmpty()) {
                txtContent.setText(content);
            } else {
                txtContent.setText("Không có nội dung");
            }

            if (extraData != null && !extraData.isEmpty()) {
                txtExtra.setText(extraData);
            } else {
                txtExtra.setText("Không có thông tin bổ sung");
            }
        }
    }
}


