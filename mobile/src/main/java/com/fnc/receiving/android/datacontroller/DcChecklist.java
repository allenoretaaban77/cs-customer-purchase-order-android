package com.fnc.receiving.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.receiving.android.database.DBHelper;
import com.fnc.receiving.android.database.Table;
import com.fnc.receiving.android.database.aChecklistQueryBuilder;
import com.fnc.receiving.android.enumeration.aChecklistKey;
import com.fnc.receiving.android.model.aChecklist;

import java.util.LinkedList;

public class DcChecklist extends DBHelper {

    private Context context;
    private static DcChecklist instance;

    public static DcChecklist getInstance(Context context){
        if(instance == null){
            instance = new DcChecklist(context);
        }
        return instance;
    }

    public DcChecklist(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyChecklist(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.CHECKLIST.getName(), null,null);
        db.close();
    }

    public void insertChecklist(aChecklist cl) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = aChecklistQueryBuilder.prepareChecklistInsertValues(cl, context);
        db.insertWithOnConflict(Table.CHECKLIST.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteChecklistViaId(String identityId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.CHECKLIST.getName(), "identityId = ?", new String[] { identityId });
        db.close();
    }

    public LinkedList<aChecklist> getChecklist() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.CHECKLIST.getName();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<aChecklist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setChecklist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private aChecklist setChecklist(Cursor c) {
        aChecklist cl = new aChecklist(
            c.getInt(c.getColumnIndex(aChecklistKey.RECID.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.BRANCH_CODE.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.CUSTOMER_NAME.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.DELIVERY_DATE.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.ORDER_TYPE.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.INCLUDED_PO.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.DRIVER.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.PLATE_NO.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.CREATED_BY.getKey())),
            c.getInt(c.getColumnIndex(aChecklistKey.DR_RECID.getKey())),
            c.getInt(c.getColumnIndex(aChecklistKey.DR_NUMBER.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.INVOICE_NUMBER.getKey())),
            c.getInt(c.getColumnIndex(aChecklistKey.PAGE_NO.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.STATUS.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.STATUS_LBL.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.REMARKS.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.FOOD_SERVICE.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.CREATED_BY_NAME.getKey())),
            c.getInt(c.getColumnIndex(aChecklistKey.IS_SENT.getKey())),
            c.getString(c.getColumnIndex(aChecklistKey.JSON_SENT.getKey()))
        );
        return cl;
    }
}
