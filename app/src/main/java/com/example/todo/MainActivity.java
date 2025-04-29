package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.sidesheet.SideSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ImageView menu = findViewById(R.id.menu);
        ImageView add = findViewById(R.id.add);

        menu.setOnClickListener(view -> openSideSheet());
        add.setOnClickListener(view -> GoToAdd());

        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

               loadTasks();
    }

    private void GoToAdd() {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        startActivity(intent);
    }

    private void openSideSheet() {
        SideSheetDialog sideSheetDialog = new SideSheetDialog(this);
        sideSheetDialog.setContentView(R.layout.side_sheet_menu);
        sideSheetDialog.setCanceledOnTouchOutside(true);
        sideSheetDialog.setSheetEdge(Gravity.START);
        sideSheetDialog.show();

        sideSheetDialog.findViewById(R.id.archive).setOnClickListener(view -> goToArchive());
    }

    private void goToArchive() {
        Intent intent = new Intent(MainActivity.this,ArchiveActivity.class);
        startActivity(intent);
    }

    private void loadTasks() {
        if (currentUser == null) return;

        CollectionReference taskRef = firestore.collection("users")
                .document(currentUser.getUid())
                .collection("Tasks");

        taskRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }

            taskList.clear();

            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    Task task = doc.toObject(Task.class);
                    task.setId(doc.getId());

                    if (!task.isArchived()) {
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }
        });
    }

}
