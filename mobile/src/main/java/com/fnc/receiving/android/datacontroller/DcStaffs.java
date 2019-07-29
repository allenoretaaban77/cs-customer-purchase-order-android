package com.fnc.receiving.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.receiving.android.database.DBHelper;
import com.fnc.receiving.android.database.Table;
import com.fnc.receiving.android.database.aStaffsQueryBuilder;
import com.fnc.receiving.android.enumeration.aItemsKey;
import com.fnc.receiving.android.enumeration.aStaffsKey;
import com.fnc.receiving.android.model.aItems;
import com.fnc.receiving.android.model.aStaffs;
import com.fnc.receiving.android.model.aUsers;

import java.util.LinkedList;

public class DcStaffs extends DBHelper {

    private Context context;
    private static DcStaffs instance;

    public static DcStaffs getInstance(Context context) {
        if (instance == null) {
            instance = new DcStaffs(context);
        }
        return instance;
    }

    public DcStaffs(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyStaffslist() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.STAFFS.getName(), null, null);
        db.close();
    }

    public void insertStaffs(aStaffs sl) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = aStaffsQueryBuilder.prepareStaffsInsertValues(sl, context);
        db.insertWithOnConflict(Table.STAFFS.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteStaffsViaId(String identityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.STAFFS.getName(), "empId = ?", new String[]{identityId});
        db.close();
    }

    public LinkedList<aStaffs> getStaffs() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.STAFFS.getName();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<aStaffs> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setStaffs(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<aStaffs> checkStaff(String strUn, String strPw) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.STAFFS.getName()
                + " WHERE " + aStaffsKey.EMPNO.getKey() + " = '" + strUn
//                + " OR " + aStaffsKey.EMAIL.getKey() + " = '" + strUn
                + "' AND " + aStaffsKey.PASS.getKey() + " = '" + strPw
                + "'";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<aStaffs> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setStaffs(c));
        }
        c.close();
        db.close();
        return list;
    }

    private aStaffs setStaffs(Cursor c) {
        aStaffs sl = new aStaffs(
            c.getInt(c.getColumnIndex(aStaffsKey.EMPID.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.EMPNO.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.EMAIL.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.NAME.getKey())),
            c.getInt(c.getColumnIndex(aStaffsKey.BRANCH.getKey())),
            c.getInt(c.getColumnIndex(aStaffsKey.JOBTITLE.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.PASS.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.ACTIVE.getKey()))
        );
        return sl;
    }
}