package com.fnc.order.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.database.aBranchlistQueryBuilder;
import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.model.aBranchlist;

import java.util.ArrayList;
import java.util.LinkedList;

public class DcBranchlist extends DBHelper {

    private Context context;
    private static DcBranchlist instance;

    public static DcBranchlist getInstance(Context context) {
        if (instance == null) {
            instance = new DcBranchlist(context);
        }
        return instance;
    }

    public DcBranchlist(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyBranchlist() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.BRANCHLIST.getName(), null, null);
        db.close();
    }

    public void insertBranches(aBranchlist bl) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = aBranchlistQueryBuilder.prepareaBranchlistInsertValues(bl, context);
        db.insertWithOnConflict(Table.BRANCHLIST.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void updateBranchlist(String refid, aBranchlistKey key, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(key.getKey(), value);
        db.updateWithOnConflict(Table.BRANCHLIST.getName(), cv, aBranchlistKey.DEVICEID.getKey() + " = ?",
                new String[] { refid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public LinkedList<aBranchlist> getBranchlist() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.BRANCHLIST.getName();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<aBranchlist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setBranchlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public ArrayList<String> getDescriptions() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT " + aBranchlistKey.BRANCHCODE.getKey()
                + " FROM " + Table.BRANCHLIST.getName()
                + " WHERE " + aBranchlistKey.BRANCHID.getKey() + " != ?"
                + " GROUP BY " + aBranchlistKey.BRANCHID.getKey()
                + " ORDER BY " + aBranchlistKey.DESCRIPTION.getKey() + " ASC";
        Cursor c = db.rawQuery(strQry, new String[] { "12345" });
        ArrayList<String> stringBranches = new ArrayList<String>();
        while (c.moveToNext()) {
            if (Character.isLetter(c.getString(0).charAt(0))) {
                stringBranches.add(c.getString(0));
            }
        }
        c.close();
        db.close();
        return stringBranches;
    }

    public LinkedList<aBranchlist> searchBranchFilterMultiple(String strCol, String[] strMultiple) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.BRANCHLIST.getName() + " WHERE " + strCol;
        Cursor c = db.rawQuery(strQry, strMultiple);
        LinkedList<aBranchlist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setBranchlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private aBranchlist setBranchlist(Cursor c) {
        aBranchlist bl = new aBranchlist(
            c.getInt(c.getColumnIndex(aBranchlistKey.BRANCHID.getKey())),
            c.getString(c.getColumnIndex(aBranchlistKey.BRANCHCODE.getKey())),
            c.getString(c.getColumnIndex(aBranchlistKey.DEVICEID.getKey())),
            c.getString(c.getColumnIndex(aBranchlistKey.DESCRIPTION.getKey())),
            c.getString(c.getColumnIndex(aBranchlistKey.DEVICEID1.getKey())),
            c.getString(c.getColumnIndex(aBranchlistKey.ACTIVE.getKey())),
            c.getString(c.getColumnIndex(aBranchlistKey.CUSTOMERID.getKey())),
            c.getString(c.getColumnIndex(aBranchlistKey.OLDBRANCHID.getKey())),
            c.getString(c.getColumnIndex(aBranchlistKey.OLDCUSTOMERID.getKey()))
        );
        return bl;
    }
}
