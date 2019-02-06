package com.fnc.order.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.MenulistQueryBuilder;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.model.MenuList;

import java.util.LinkedList;

public class DcMenulist extends DBHelper {

    private Context context;
    private static DcMenulist instance;

    public static DcMenulist getInstance(Context context){
        if(instance == null){
            instance = new DcMenulist(context);
        }
        return instance;
    }

    public DcMenulist(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyMenulist(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.MENULIST.getName(), null,null);
        db.close();
    }

    public void insertMenulist(MenuList ml){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = MenulistQueryBuilder.prepareMenulistInsertValues(ml, context);
        db.insertWithOnConflict(Table.MENULIST.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void updateMenulistCountViaId(String recid, Integer count){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MenulistKey.RECORD_COUNT.getKey(), count);
        db.updateWithOnConflict(Table.MENULIST.getName(), cv, "customerID = ?",
                new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public LinkedList<MenuList> getAllMenulist() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT *" +
                " FROM " + Table.MENULIST.getName()
                + " ORDER BY " + MenulistKey.CUSTOMER_NAME.getKey() + " ASC";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<MenuList> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setMenulist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private MenuList setMenulist(Cursor c) {
        MenuList menulist = new MenuList();
        menulist.setCustomerName(c.getString(c.getColumnIndex(MenulistKey.CUSTOMER_NAME.getKey())));
        menulist.setRecordCount(c.getInt(c.getColumnIndex(MenulistKey.RECORD_COUNT.getKey())));
        menulist.setRemarks(c.getString(c.getColumnIndex(MenulistKey.REMARKS.getKey())));
        menulist.setCustomerID(c.getString(c.getColumnIndex(MenulistKey.CUSTOMER_ID.getKey())));
        return menulist;
    }

}
