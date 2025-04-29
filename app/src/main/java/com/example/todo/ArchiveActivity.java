package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_archive);

        // زر الرجوع
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());  // finish أفضل من فتح MainActivity من جديد

        // إعداد RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        // Firebase
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadArchivedTasks();
    }

    private void loadArchivedTasks() {
        if (currentUser == null) return;

        CollectionReference taskRef = firestore
                .collection("users")
                .document(currentUser.getUid())
                .collection("Tasks");

        taskRef.whereEqualTo("isArchived", true)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    taskList.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Task task = doc.toObject(Task.class);
                        task.setId(doc.getId());
                        taskList.add(task);
                    }

                    taskAdapter.notifyDataSetChanged();
                });

    }
}
