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

import java.util.ArrayList;
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
        db.beginTransaction();
        ContentValues value = MenulistQueryBuilder.prepareMenulistInsertValues(ml, context);
        db.insertWithOnConflict(Table.MENULIST.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.setTransactionSuccessful();
        db.endTransaction();
//        db.close();
    }

    public void updateMenulistCountViaId(String recid, Integer count){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MenulistKey.RECORD_COUNT.getKey(), count);
        db.updateWithOnConflict(Table.MENULIST.getName(), cv, "customerID = ?",
                new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public ArrayList<String> getAllMenulistAlpha() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT " + MenulistKey.ALPHA_CHAR.getKey() +
            " FROM " + Table.MENULIST.getName() +
            " GROUP BY " + MenulistKey.ALPHA_CHAR.getKey() +
            " ORDER BY " + MenulistKey.ALPHA_CHAR.getKey() + " ASC";
        Cursor c = db.rawQuery(strQry, null);
        ArrayList<String> stringAlpha = new ArrayList<String>();
        while (c.moveToNext()) {
            if (Character.isLetter(c.getString(0).charAt(0))) {
                stringAlpha.add(c.getString(0));
            }
        }
        c.close();
        db.close();

        // get other characters
        db = getReadableDatabase();
        c = db.rawQuery(strQry, null);
        while (c.moveToNext()) {
            if (!Character.isLetter(c.getString(0).charAt(0))) {
                stringAlpha.add(c.getString(0));
            }
        }
        c.close();
        db.close();

        return stringAlpha;
    }

    public LinkedList<MenuList> searchCustomer(String searchStr) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.MENULIST.getName()
                + " WHERE " + MenulistKey.CUSTOMER_ID.getKey() + " = ?";
        Cursor c = db.rawQuery(strQry, new String[] { searchStr });
        LinkedList<MenuList> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setMenulist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<MenuList> getAllMenulist(Boolean isAlpha, String stringSearch) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "";
        if (isAlpha) {
            strQry = "SELECT *" +
                    " FROM " + Table.MENULIST.getName() +
                    " WHERE " + MenulistKey.ALPHA_CHAR.getKey() + " = ?" +
                    " ORDER BY " + MenulistKey.CUSTOMER_NAME.getKey() + " ASC";
        } else {
            strQry = "SELECT *" +
                    " FROM " + Table.MENULIST.getName() +
                    " WHERE " + MenulistKey.CUSTOMER_NAME.getKey() + " LIKE ?" +
                    " ORDER BY " + MenulistKey.CUSTOMER_NAME.getKey() + " ASC";
        }
        Cursor c = db.rawQuery(strQry, new String[] { stringSearch });
        LinkedList<MenuList> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setMenulist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<MenuList> searchMenuFilterMultiple(String strCol, String[] strMultiple) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.MENULIST.getName() + " WHERE " + strCol;
        Cursor c = db.rawQuery(strQry, strMultiple);
        LinkedList<MenuList> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setMenulist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private MenuList setMenulist(Cursor c) {
        MenuList menulist = new MenuList(
            c.getString(c.getColumnIndex(MenulistKey.CUSTOMER_ID.getKey())),
            c.getString(c.getColumnIndex(MenulistKey.CUSTOMER_INTEG_ID.getKey())),
            c.getString(c.getColumnIndex(MenulistKey.CUSTOMER_NAME.getKey())),
            c.getString(c.getColumnIndex(MenulistKey.REMARKS.getKey())),
            c.getInt(c.getColumnIndex(MenulistKey.RECORD_COUNT.getKey())),
            c.getString(c.getColumnIndex(MenulistKey.ALPHA_CHAR.getKey()))
        );
        return menulist;
    }

}
