package com.fnc.order.android.datacontroller;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fnc.order.android.R;
import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.ReturnlistQueryBuilder;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.enumeration.ReturnKey;
import com.fnc.order.android.model.Return;

import java.util.LinkedList;

public class DcReturn extends DBHelper {

    private Context context;
    private static DcReturn instance;

    public static DcReturn getInstance(Context context) {
        if(instance == null){
            instance = new DcReturn(context);
        }
        return instance;
    }

    public DcReturn(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyReturnlist() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.RETURN.getName(), null,null);
        db.close();
    }

    public void insertReturnlist(Return r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = ReturnlistQueryBuilder.prepareReturnlistInsertValues(r, context);
        db.insertWithOnConflict(Table.RETURN.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void deleteReturnItemViaId(String recid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.RETURN.getName(), "item_recid = ?", new String[] { recid });
        db.close();
    }

    public void updateReturnlistTallyString(String recid, String qtyString, Boolean isInitial) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ReturnKey.ITEM_RECID.getKey(), recid);
        cv.put(ReturnKey.QUANTITY.getKey(), qtyString);
        db.updateWithOnConflict(Table.RETURN.getName(), cv, "item_recid = ?",
                new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void updateReturnlistRemarksString(String recid, String remarksString, Boolean isInitial) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ReturnKey.ITEM_RECID.getKey(), recid);
        cv.put(ReturnKey.REMARKS.getKey(), remarksString);
        db.updateWithOnConflict(Table.RETURN.getName(), cv, "item_recid = ?",
                new String[] { recid }, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public LinkedList<Return> getReturnlistAsc() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT *" +
                " FROM " + Table.RETURN.getName() + " ORDER BY id ASC";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Return> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setReturnlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<Return> getReturnlist(Integer recid) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT *" +
                " FROM " + Table.RETURN.getName() +
                " WHERE " + ReturnKey.ITEM_RECID + " = " + recid.toString();
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Return> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setReturnlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<Return> getLastRecord() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.RETURN.getName() + " ORDER BY id DESC LIMIT 1";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<Return> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setReturnlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private Return setReturnlist(Cursor c) {
        Return returns = new Return();
        returns.setQuantity(c.getString(c.getColumnIndex(ReturnKey.QUANTITY.getKey())));
        returns.setItemRecid(c.getString(c.getColumnIndex(ReturnKey.ITEM_RECID.getKey())));
        returns.setItemName(c.getString(c.getColumnIndex(ReturnKey.ITEM_NAME.getKey())));
        returns.setUnitName(c.getString(c.getColumnIndex(ReturnKey.UNIT_NAME.getKey())));
        returns.setRemarks(c.getString(c.getColumnIndex(ReturnKey.REMARKS.getKey())));
        return returns;
    }
}
