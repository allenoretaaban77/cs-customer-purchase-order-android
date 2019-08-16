package com.fnc.order.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.OrderedlistQueryBuilder;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.enumeration.OrderedKey;
import com.fnc.order.android.model.Ordered;

import java.util.LinkedList;

public class DcOrdered extends DBHelper {

    private Context context;
    private static DcOrdered instance;

    public static DcOrdered getInstance(Context context) {
        if(instance == null){
            instance = new DcOrdered(context);
        }
        return instance;
    }

    public DcOrdered(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyOrderedlist() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.ORDERED.getName(), null,null);
        db.close();
    }

    public void insertOrderedlist(Ordered od) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = OrderedlistQueryBuilder.prepareOrderedlistInsertValues(od, context);
        db.insertWithOnConflict(Table.ORDERED.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void updateStatusViaRecId(String recid, Integer intx){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OrderedKey.STATUS.getKey(), intx);
        db.updateWithOnConflict(Table.ORDERED.getName(), cv,
                OrderedKey.CUSTOMER_RECID.getKey()+ " = ?",
                new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void updateRefRecIdViaRecId(String recid, Integer intx){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OrderedKey.STATUS.getKey(), intx);
        db.updateWithOnConflict(Table.ORDERED.getName(), cv,
                OrderedKey.CUSTOMER_RECID.getKey()+ " = ?",
                new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public LinkedList<Ordered> getOrderedlist() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.ORDERED.getName() + " ORDER BY datetime DESC";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Ordered> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setOrderedlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<Ordered> getOrderedlistCheckStatus() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.ORDERED.getName() + " WHERE "
                + OrderedKey.STATUS.getKey() + " = 0 ORDER BY datetime DESC";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Ordered> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setOrderedlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private Ordered setOrderedlist(Cursor c) {
        Ordered od = new Ordered();
        od.setCustomerIntegRecid(c.getString(c.getColumnIndex(OrderedKey.CUSTOMER_INTEG_RECID.getKey())));
        od.setCustomerRecid(c.getString(c.getColumnIndex(OrderedKey.CUSTOMER_RECID.getKey())));
        od.setCustomerName(c.getString(c.getColumnIndex(OrderedKey.CUSTOMER_NAME.getKey())));
        od.setDeliveryDate(c.getString(c.getColumnIndex(OrderedKey.DELIVERY_DATE.getKey())));
        od.setCreatedBy(c.getString(c.getColumnIndex(OrderedKey.CREATED_BY.getKey())));
        od.setRemarks(c.getString(c.getColumnIndex(OrderedKey.REMARKS.getKey())));
        od.setReferenceEmployeeNo(c.getString(c.getColumnIndex(OrderedKey.REF_EMPLOYEE_NO.getKey())));
        od.setJson(c.getString(c.getColumnIndex(OrderedKey.JSON.getKey())));
        od.setJsonComplete(c.getString(c.getColumnIndex(OrderedKey.JSON_COMPLETE.getKey())));
        od.setGrandtotal(c.getString(c.getColumnIndex(OrderedKey.GRAND_TOTAL.getKey())));
        od.setDateTime(c.getString(c.getColumnIndex(OrderedKey.DATETIME.getKey())));
        od.setStatus(c.getInt(c.getColumnIndex(OrderedKey.STATUS.getKey())));
        od.setReferenceRecid(c.getString(c.getColumnIndex(OrderedKey.REF_RECID.getKey())));
        return od;
    }
}
