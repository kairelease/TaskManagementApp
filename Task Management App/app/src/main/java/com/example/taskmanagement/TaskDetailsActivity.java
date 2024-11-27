package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagement.Model.ToDoModel;
import com.example.taskmanagement.Utils.DatabaseHelper;

public class TaskDetailsActivity extends AppCompatActivity {

    private TextView tvTaskName;
    private EditText etDescription;
    private Button btnSave;

    private DatabaseHelper databaseHelper;
    private int taskId;
    private String taskName;
    private String taskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        tvTaskName = findViewById(R.id.tvTaskName);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        databaseHelper = new DatabaseHelper(this);

        // Retrieve task details passed from the previous activity
        taskId = getIntent().getIntExtra("task_id", -1); // Default to -1 if task_id is not found
        taskName = getIntent().getStringExtra("task_name");
        taskDescription = getIntent().getStringExtra("task_description");

        if (taskId == -1 || taskName == null) {
            // Handle error: task ID or name is missing
            Toast.makeText(this, "Invalid task details!", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity
            return;
        }

        tvTaskName.setText(taskName);

        // Show the passed description or fetch from the database if missing
        if (taskDescription != null) {
            etDescription.setText(taskDescription);
        } else {
            // Load from database if not passed
            String descriptionFromDB = databaseHelper.getDescription(taskId);
            etDescription.setText(descriptionFromDB);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newDescription = etDescription.getText().toString().trim();
                if (!newDescription.isEmpty()) {
                    // Update the description in the database
                    databaseHelper.updateDescription(taskId, newDescription);
                    Toast.makeText(TaskDetailsActivity.this, "Description saved!", Toast.LENGTH_SHORT).show();

                    // Inform MainActivity or the adapter to refresh the specific task
                    // Pass the updated task data back to MainActivity
                    Intent intent = new Intent();
                    intent.putExtra("updated_task_id", taskId);  // Pass the updated task ID
                    intent.putExtra("updated_description", newDescription);  // Pass the new description
                    setResult(RESULT_OK, intent);  // Send the result back to MainActivity
                    finish();  // Close the TaskDetailsActivity and return to the previous screen
                } else {
                    Toast.makeText(TaskDetailsActivity.this, "Please add a description!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
