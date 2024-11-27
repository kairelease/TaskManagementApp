package com.example.taskmanagement.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.taskmanagement.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TODO_DATABASE";
    private static final String TABLE_NAME = "TODO_TABLE";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TASK";
    private static final String COL_3 = "STATUS";
    private static final String COL_4 = "DESCRIPTION";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK TEXT, STATUS INTEGER, DESCRIPTION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN DESCRIPTION TEXT");
        }
    }

    public void insertTask(ToDoModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, model.getTask());
        contentValues.put(COL_3, 0); // Default status is unchecked
        contentValues.put(COL_4, model.getDescription()); // Add description

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public void updateTask(int id, String task, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, task);
        contentValues.put(COL_4, description);

        db.update(TABLE_NAME, contentValues, "ID=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3, status);

        db.update(TABLE_NAME, contentValues, "ID=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateDescription(int id, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4, description);

        db.update(TABLE_NAME, contentValues, "ID=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Get the description of a task by ID
    public String getDescription(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String description = "";

        try (Cursor cursor = db.query(TABLE_NAME, new String[]{COL_4}, "ID=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                description = cursor.getString(cursor.getColumnIndex(COL_4));
            }
        }
        db.close();
        return description;
    }

    // Retrieve all tasks, including their descriptions
    public List<ToDoModel> getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Use the constructor with parameters to create the ToDoModel object
                ToDoModel toDoModel = new ToDoModel(
                        cursor.getInt(cursor.getColumnIndex(COL_1)),
                        cursor.getString(cursor.getColumnIndex(COL_2)),
                        cursor.getInt(cursor.getColumnIndex(COL_3)),
                        cursor.getString(cursor.getColumnIndex(COL_4))
                );
                taskList.add(toDoModel);
            }
            cursor.close();
        }
        db.close();  // Close the database connection after retrieving all tasks
        return taskList;
    }
}
