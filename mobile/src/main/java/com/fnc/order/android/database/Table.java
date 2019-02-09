package com.fnc.order.android.database;

public enum Table {
    ORDER("order"),
    RETURN("return"),
    PREPAREDLIST("preparedlist"),
    MENULIST("menulist"),
    STORELIST("storelists"),
    NOTIFICATIONS("notifications"),
    CHECKLIST("checklists"),
    CHECKLIST_BU("checklists_backup");

    private String name;

    Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
