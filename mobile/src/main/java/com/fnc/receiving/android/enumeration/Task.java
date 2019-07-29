package com.fnc.receiving.android.enumeration;

public enum Task {

    SERVER_TIME("servertime");

    private String task;

    Task(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }
}
