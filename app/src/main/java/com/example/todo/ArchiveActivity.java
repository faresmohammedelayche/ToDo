package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ArchiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_archive);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view -> GoToMain());
    }

    private void GoToMain() {
        Intent intent = new Intent(ArchiveActivity.this,MainActivity.class);
        startActivity(intent);
    }
}