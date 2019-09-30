package com.fnc.order.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.database.aStaffsQueryBuilder;
import com.fnc.order.android.enumeration.aStaffsKey;
import com.fnc.order.android.model.aStaffs;

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

    public void setInActive(aStaffs sl) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = aStaffsQueryBuilder.prepareStaffsInsertValues(sl, context);
        db.insertWithOnConflict(Table.STAFFS_INACTIVE.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void setActive(aStaffs sl) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.STAFFS_INACTIVE.getName(), "empId = ?", new String[]{ String.valueOf(sl.getEmpId()) });
        db.close();
    }

    public LinkedList<aStaffs> checkIfActive(Integer intEmpId) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.STAFFS_INACTIVE.getName() +
            " WHERE " + aStaffsKey.EMPID.getKey() + " = ?";
        Cursor c = db.rawQuery(strQry, new String[] { String.valueOf(intEmpId) } );
        LinkedList<aStaffs> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setStaffs(c));
        }
        c.close();
        db.close();
        return list;
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

    public LinkedList<aStaffs> getStaffswihtOrder(String strName) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.STAFFS.getName() +
                " WHERE " + aStaffsKey.REFEMPNO.getKey() + " != '-2' " +
                " AND " + aStaffsKey.NAME.getKey() + " LIKE ? " +
                " ORDER BY " + aStaffsKey.NAME.getKey() + " ASC";
        Cursor c = db.rawQuery(strQry, new String[] { strName });
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
                + " WHERE " + aStaffsKey.EMPNO.getKey() + " = ?"
                + " AND " + aStaffsKey.PASS.getKey() + " = ?";
        Cursor c = db.rawQuery(strQry, new String[] { strUn, strPw });
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
            c.getString(c.getColumnIndex(aStaffsKey.REFEMPNO.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.EMPNO.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.EMAIL.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.NAME.getKey())),
            c.getInt(c.getColumnIndex(aStaffsKey.BRANCH.getKey())),
            c.getInt(c.getColumnIndex(aStaffsKey.JOBTITLE.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.PASS.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.ACTIVE.getKey())),
            c.getString(c.getColumnIndex(aStaffsKey.ISMOBILEADMIN.getKey()))
        );
        return sl;
    }
}
