package com.example.taskmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagement.Adapter.ToDoAdapter;
import com.example.taskmanagement.Model.ToDoModel;
import com.example.taskmanagement.Utils.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    RecyclerView recyclerView;
    FloatingActionButton addButton;
    DatabaseHelper myDB;
    private List<ToDoModel> mList;
    private ToDoAdapter adapter;
    private static final int TASK_DETAIL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Edge-to-Edge UI handling (ensure EdgeToEdge class exists or adjust if necessary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InfoScreen.class);
                startActivity(intent);
            }
        });

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);

        // Initialize database and data list
        myDB = new DatabaseHelper(MainActivity.this);
        mList = new ArrayList<>();
        adapter = new ToDoAdapter(myDB, MainActivity.this);

        // Set up RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load tasks from the database and update the adapter
        mList = myDB.getAllTasks();
        Collections.reverse(mList);  // Reverse the list to show the most recent tasks first
        adapter.setTask(mList);

        // Set up the FloatingActionButton to add new tasks
        addButton.setOnClickListener(view -> {
            AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);  // Corrected typo
        });

        // Set up swipe actions for RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        super.onResume();
        // Refresh the task list when a dialog is closed
        mList = myDB.getAllTasks();
        Collections.reverse(mList);  // Reverse again to show the latest task on top
        adapter.setTask(mList);
        adapter.notifyDataSetChanged();  // Notify adapter to refresh the view
    }

    // Handle the result from TaskDetailsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TASK_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the updated task ID and description
            int updatedTaskId = data.getIntExtra("updated_task_id", -1);
            String updatedDescription = data.getStringExtra("updated_description");

            if (updatedTaskId != -1 && updatedDescription != null) {
                // Find the task in the list and update it
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getId() == updatedTaskId) {
                        ToDoModel updatedTask = mList.get(i);
                        updatedTask.setDescription(updatedDescription);  // Update the description
                        adapter.notifyItemChanged(i);  // Notify the adapter to refresh the item
                        break;
                    }
                }
            }
        }
    }
}
