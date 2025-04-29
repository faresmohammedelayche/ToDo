package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditTaskActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private DatePicker datePicker;
    private TimePicker timePicker;

    private ImageView back;

    private Button btnSave, btnIgnore;

    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // ربط العناصر بالواجهة
        titleEditText = findViewById(R.id.title_item);
        contentEditText = findViewById(R.id.content);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        back = findViewById(R.id.back);
        btnSave = findViewById(R.id.btn_save);
        btnIgnore = findViewById(R.id.btn_ignore);

        // إعداد TimePicker للعمل بصيغة 24 ساعة
        timePicker.setIs24HourView(true);

        // جلب بيانات المهمة من Intent
        taskId = getIntent().getStringExtra("taskId");
        String taskTitle = getIntent().getStringExtra("taskTitle");
        String taskContent = getIntent().getStringExtra("taskContent");

        // عرض البيانات في EditText
        titleEditText.setText(taskTitle);
        contentEditText.setText(taskContent);

        // زر "حفظ"
        btnSave.setOnClickListener(v -> updateTask());

        // زر "تجاهل"
        btnIgnore.setOnClickListener(v -> finish());

        back.setOnClickListener(view -> GoBack());
    }

    private void GoBack() {
        Intent intent = new Intent(EditTaskActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void updateTask() {
        String newTitle = titleEditText.getText().toString().trim();
        String newContent = contentEditText.getText().toString().trim();

        // التحقق من المدخلات
        if (newTitle.isEmpty()) {
            titleEditText.setError("Please enter title");
            titleEditText.requestFocus();
            return;
        }

        // استخراج التاريخ والوقت
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth(); // 0-based
        int year = datePicker.getYear();

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        long timestamp = calendar.getTimeInMillis();

        // تحديث المهمة في Firebase
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("title", newTitle);
        updatedData.put("content", newContent);
        updatedData.put("timestamp", timestamp);

        firestore.collection("users")
                .document(uid)
                .collection("Tasks")
                .document(taskId)
                .update(updatedData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(EditTaskActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(EditTaskActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
