package com.fnc.order.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.OrderlistQueryBuilder;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.enumeration.OrderedKey;
import com.fnc.order.android.model.Order;
import com.fnc.order.android.model.aBranchlist;

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

    public void updateOrderlist(String recid, OrderKey column, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(column.getKey(), value);
        db.updateWithOnConflict(Table.ORDER.getName(), cv, "item_recid = ?", new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void updateSetAllQuantity(String value, String args) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put( OrderKey.QUANTITY.getKey(), value);
        db.updateWithOnConflict(Table.ORDER.getName(), cv, args, null, SQLiteDatabase.CONFLICT_IGNORE);
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

    public LinkedList<Order> searchOrderFilterMultiple(String strCol, String[] strMultiple, String orderby) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.ORDER.getName() + " WHERE " + strCol + orderby;
        Cursor c = db.rawQuery(strQry, strMultiple);
        LinkedList<Order> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setOrderlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<Order> getOrderlist() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT *" +
            " FROM " + Table.ORDER.getName()
            + " ORDER BY " + OrderKey.ITEM_NAME.getKey() + " ASC";
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
        order.setFree(c.getString(c.getColumnIndex(OrderKey.FREE.getKey())));
        order.setItemRecid(c.getString(c.getColumnIndex(OrderKey.ITEM_RECID.getKey())));
        order.setItemName(c.getString(c.getColumnIndex(OrderKey.ITEM_NAME.getKey())));
        order.setUnitName(c.getString(c.getColumnIndex(OrderKey.UNIT_NAME.getKey())));
        order.setRemarks(c.getString(c.getColumnIndex(OrderKey.REMARKS.getKey())));
        order.setOldSku(c.getString(c.getColumnIndex(OrderKey.OLD_SKU.getKey())));
        order.setSellingPrice(c.getString(c.getColumnIndex(OrderKey.SELLING_PRICE.getKey())));
        order.setTotal(c.getString(c.getColumnIndex(OrderKey.TOTAL.getKey())));
        order.setIsChecked(c.getInt(c.getColumnIndex(OrderKey.IS_CHECKED.getKey())));
        order.setIsError(c.getInt(c.getColumnIndex(OrderKey.IS_ERROR.getKey())));
        order.setIsLocked(c.getInt(c.getColumnIndex(OrderKey.IS_LOCKED.getKey())));
        return order;
    }
}
