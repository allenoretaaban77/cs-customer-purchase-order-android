package com.fnc.order.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.OrderlistQueryBuilder;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.model.Order;

import java.util.LinkedList;

public class DcOrder extends DBHelper {

    private Context context;
    private static DcOrder instance;

    public static DcOrder getInstance(Context context) {
        if(instance == null){
            instance = new DcOrder(context);
        }
        return instance;
    }

    public DcOrder(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyOrderlist() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.ORDER.getName(), null,null);
        db.close();
    }

    public void insertOrderlist(Order o) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = OrderlistQueryBuilder.prepareOrderlistInsertValues(o, context);
        db.insertWithOnConflict(Table.ORDER.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteOrderItemViaId(String recid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.ORDER.getName(), "item_recid = ?", new String[] { recid });
        db.close();
    }

    public void updateOrderlistTallyString(String recid, String qtyString, Boolean isInitial) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OrderKey.ITEM_RECID.getKey(), recid);
        cv.put(OrderKey.QUANTITY.getKey(), qtyString);
        db.updateWithOnConflict(Table.ORDER.getName(), cv, "item_recid = ?",
                new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void updateOrderlistRemarksString(String recid, String remarksString, Boolean isInitial) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OrderKey.ITEM_RECID.getKey(), recid);
        cv.put(OrderKey.REMARKS.getKey(), remarksString);
        db.updateWithOnConflict(Table.ORDER.getName(), cv, "item_recid = ?",
                new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public LinkedList<Order> getOrderlistAsc() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT *" +
                " FROM " + Table.ORDER.getName() + " ORDER BY id ASC";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Order> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setOrderlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<Order> getOrderlist(Integer recid) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT *" +
                " FROM " + Table.ORDER.getName() +
                " WHERE " + OrderKey.ITEM_RECID + " = " + recid.toString();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Order> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setOrderlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private Order setOrderlist(Cursor c) {
        Order order = new Order();
        order.setQuantity(c.getString(c.getColumnIndex(OrderKey.QUANTITY.getKey())));
        order.setItemRecid(c.getString(c.getColumnIndex(OrderKey.ITEM_RECID.getKey())));
        order.setItemName(c.getString(c.getColumnIndex(OrderKey.ITEM_NAME.getKey())));
        order.setUnitName(c.getString(c.getColumnIndex(OrderKey.UNIT_NAME.getKey())));
        order.setRemarks(c.getString(c.getColumnIndex(OrderKey.REMARKS.getKey())));
        order.setOldSku(c.getString(c.getColumnIndex(OrderKey.OLD_SKU.getKey())));
        return order;
    }
}
