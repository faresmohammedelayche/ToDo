package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private EditText titleItem, contentItem;
    private ImageView backButton;
    private Button saveButton, ignoreButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);

        titleItem = findViewById(R.id.title_item);
        contentItem = findViewById(R.id.content);
        saveButton = findViewById(R.id.btn_save);
        ignoreButton = findViewById(R.id.btn_ignore);
        backButton = findViewById(R.id.back);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        backButton.setOnClickListener(view -> goMain());
        ignoreButton.setOnClickListener(view -> goMain());
        saveButton.setOnClickListener(view -> saveTask());
    }

    private void saveTask() {
        String title = titleItem.getText().toString().trim();
        String content = contentItem.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String dateTime = String.format("%02d/%02d/%04d %02d:%02d", day, month, year, hour, minute);

        Map<String, Object> task = new HashMap<>();
        task.put("title", title);
        task.put("content", content);
        task.put("date", dateTime);
        task.put("timestamp", System.currentTimeMillis());
        task.put("isCompleted", false);
        task.put("isArchived", false);

        String userId = currentUser.getUid();

        DocumentReference taskRef = firestore.collection("users")
                .document(userId)
                .collection("Tasks")
                .document();

        taskRef.set(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddTaskActivity.this, "Task saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddTaskActivity.this, "Error saving task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void goMain() {
        Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
