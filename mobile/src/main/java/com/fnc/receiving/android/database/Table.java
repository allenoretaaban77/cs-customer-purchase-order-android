package com.fnc.receiving.android.database;

public enum Table {
    CHECKLIST("aChecklist"),
    ITEMS("aItems"),
    USERS("aUsers"),
    STAFFS("aStaffs");

    private String name;

    Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
