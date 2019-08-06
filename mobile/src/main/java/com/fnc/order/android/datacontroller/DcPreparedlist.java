package com.fnc.order.android.datacontroller;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.PreparedlistQueryBuilder;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.enumeration.PreparedlistKey;
import com.fnc.order.android.model.Preparedlist;

import java.util.LinkedList;

public class DcPreparedlist extends DBHelper {

    private Context context;
    private static DcPreparedlist instance;

    public static DcPreparedlist getInstance(Context context){
        if(instance == null){
            instance = new DcPreparedlist(context);
        }
        return instance;
    }

    public DcPreparedlist(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyPreparedlist(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.PREPAREDLIST.getName(), null,null);
        db.close();
    }

    public void insertPreparedlist(Preparedlist p){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = PreparedlistQueryBuilder.preparePreparedlistInsertValues(p, context);
        db.insertWithOnConflict(Table.PREPAREDLIST.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public LinkedList<Preparedlist> getAllPreparedByStoreAndRemarks(String name, String remarks) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT *" +
                " FROM " + Table.PREPAREDLIST.getName()
                + " WHERE " + PreparedlistKey.CUSTOMER_NAME.getKey() + " = '" + name + "'"
//                + " AND " + PreparedlistKey.CUSTOMER_ID.getKey() + " = '" + customerid + "'"
                + " AND " + PreparedlistKey.REMARKS.getKey() + " = '" + remarks + "'";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Preparedlist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setPreparedlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<Preparedlist> getAllPreparedByStoreAndRemarksAndRecID(String name, String remarks, String customerid) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT *" +
                " FROM " + Table.PREPAREDLIST.getName()
                + " WHERE " + PreparedlistKey.CUSTOMER_NAME.getKey() + " = '" + name + "'"
                + " AND " + PreparedlistKey.CHECKLIST_RECID.getKey() + " = '" + customerid + "'"
                + " AND " + PreparedlistKey.REMARKS.getKey() + " = '" + remarks + "'";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Preparedlist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setPreparedlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<Preparedlist> getAllPreparedlist() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.PREPAREDLIST.getName();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Preparedlist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setPreparedlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private Preparedlist setPreparedlist(Cursor c) {
        Preparedlist preparedlist = new Preparedlist();
        preparedlist.setRecid(c.getInt(c.getColumnIndex(PreparedlistKey.RECID.getKey())));
        preparedlist.setChecklistRecid(c.getInt(c.getColumnIndex(PreparedlistKey.CHECKLIST_RECID.getKey())));
        preparedlist.setCustomerId(c.getString(c.getColumnIndex(PreparedlistKey.CUSTOMER_ID.getKey())));
        preparedlist.setCustomerName(c.getString(c.getColumnIndex(PreparedlistKey.CUSTOMER_NAME.getKey())));
        preparedlist.setDept(c.getString(c.getColumnIndex(PreparedlistKey.DEPT.getKey())));
        preparedlist.setRemarks(c.getString(c.getColumnIndex(PreparedlistKey.REMARKS.getKey())));
        preparedlist.setInorder(c.getInt(c.getColumnIndex(PreparedlistKey.INORDER.getKey())));
        preparedlist.setStatus(c.getInt(c.getColumnIndex(PreparedlistKey.STATUS.getKey())));
        preparedlist.setJson(c.getString(c.getColumnIndex(PreparedlistKey.JSON.getKey())));
        return preparedlist;
    }

}
