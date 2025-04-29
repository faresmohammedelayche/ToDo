package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
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

    private EditText titleItem, contentItem, dateItem;
    private ImageView saveButton, backButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);


        titleItem = findViewById(R.id.title_item);
        contentItem = findViewById(R.id.content);
        dateItem = findViewById(R.id.editTextDate);
        saveButton = findViewById(R.id.save);
        backButton = findViewById(R.id.back);


        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        backButton.setOnClickListener(view -> goMain());
        saveButton.setOnClickListener(view -> saveTask());
    }

    private void saveTask() {
        String title = titleItem.getText().toString().trim();
        String content = contentItem.getText().toString().trim();
        String date = dateItem.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> task = new HashMap<>();
        task.put("title", title);
        task.put("content", content);
        task.put("date", date);
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
