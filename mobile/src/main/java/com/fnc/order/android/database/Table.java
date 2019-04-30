package com.fnc.order.android.database;

public enum Table {
    ORDERED("ordered"),
    ORDER("ordereditems"),
    RETURN("return"),
    PREPAREDLIST("preparedlist"),
    MENULIST("menulist"),
    STORELIST("storelists"),
    NOTIFICATIONS("notifications"),
    CHECKLIST("checklists"),
    CHECKLIST_BU("checklists_backup"),
    CUSTOMERLIST("customerlists");

    private String name;

    Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
