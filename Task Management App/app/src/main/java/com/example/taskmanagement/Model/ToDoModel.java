package com.example.taskmanagement.Model;

public class ToDoModel {

    private String task;
    private int id, status;
    private String description;

    // Default constructor
    public ToDoModel() {
    }

    // Constructor with parameters for easy initialization
    public ToDoModel(int id, String task, int status, String description) {
        this.id = id;
        this.task = task;
        this.status = status;
        this.description = description;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
