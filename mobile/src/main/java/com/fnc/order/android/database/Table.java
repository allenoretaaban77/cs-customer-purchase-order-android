package com.fnc.order.android.database;

public enum Table {
    ORDERED("ordered"),
    ORDER("ordereditems"),
    RETURN("return"),
    PREPAREDLIST("preparedlist"),
    MENULIST("menulist"),
    USERSLIST("userslist"),
    STORELIST("storelists"),
    NOTIFICATIONS("notifications"),
    CHECKLIST("checklists"),
    CHECKLIST_BU("checklists_backup"),
    CUSTOMERLIST("customerlists"),
    A_ITEMLIST("aitemlist"),
    STAFFS("aStaffs"),
    STAFFS_INACTIVE("aStaffsInactive"),
    BRANCHLIST("abranchlist");

    private String name;

    Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
