package com.example.todo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get context from parent view
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        EditText content;
        CheckBox checkboxCompleted;
        ImageView imageView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_item);
            content = itemView.findViewById(R.id.description_item);
            checkboxCompleted = itemView.findViewById(R.id.checkboxCompleted);
            imageView = itemView.findViewById(R.id.item_menu);
        }

        public void bind(Task task) {
            title.setText(task.getTitle());
            content.setText(task.getContent());
            checkboxCompleted.setChecked(task.isCompleted());

            checkboxCompleted.setOnCheckedChangeListener(null);
            checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateTaskStatus(task, isChecked);
            });

            // Set click listener to edit the task
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditTaskActivity.class);
                intent.putExtra("taskId", task.getId());
                intent.putExtra("taskTitle", task.getTitle());
                intent.putExtra("taskContent", task.getContent());
                intent.putExtra("taskCompleted", task.isCompleted());
                intent.putExtra("taskArchived", task.isArchived());
                context.startActivity(intent);
            });
        }

        private void updateTaskStatus(Task task, boolean isCompleted) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                firestore.collection("users")
                        .document(currentUser.getUid())
                        .collection("Tasks")
                        .document(task.getId())
                        .update("isCompleted", isCompleted);
            }
        }
    }
}
