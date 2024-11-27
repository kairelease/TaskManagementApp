package com.example.taskmanagement;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.taskmanagement.Model.ToDoModel;
import com.example.taskmanagement.Utils.DatabaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private EditText mEditText;
    private EditText mDescriptionEditText;
    private Button mSaveButton;

    private DatabaseHelper myDB;
    private boolean isUpdate = false;
    private int taskId = -1;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.editText);
        mDescriptionEditText = view.findViewById(R.id.editTextDescription);
        mSaveButton = view.findViewById(R.id.addButton);

        myDB = new DatabaseHelper(getContext());

        // Get arguments passed when fragment is created (for updating a task)
        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            taskId = bundle.getInt("ID", -1);
            String task = bundle.getString("task", "");
            String description = bundle.getString("task_description", "");

            if (!task.isEmpty()) {
                mEditText.setText(task);
            }
            if (description != null && !description.isEmpty()) {
                mDescriptionEditText.setText(description);
            }
            mSaveButton.setEnabled(true); // Enable button for update
        }

        // Enable or disable Save button based on input
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    mSaveButton.setEnabled(false);
                    mSaveButton.setBackgroundColor(Color.GRAY);
                } else {
                    mSaveButton.setEnabled(true);
                    mSaveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_200)); // Set the button color
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle Save button click
        mSaveButton.setOnClickListener(v -> {
            String text = mEditText.getText().toString().trim();
            String description = mDescriptionEditText.getText().toString().trim();

            if (isUpdate && taskId != -1) {
                // Update task in the database
                myDB.updateTask(taskId, text, description);
            } else {
                // Insert new task into the database
                ToDoModel item = new ToDoModel();
                item.setTask(text);
                item.setDescription(description);
                item.setStatus(0); // Default status as unchecked
                myDB.insertTask(item);
            }

            dismiss();
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}
