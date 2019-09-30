package com.fnc.order.android.datacontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fnc.order.android.database.DBHelper;
import com.fnc.order.android.database.Table;
import com.fnc.order.android.database.aItemlistQueryBuilder;
import com.fnc.order.android.enumeration.OrderedKey;
import com.fnc.order.android.enumeration.aItemlistKey;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.model.aItemlist;

import java.util.LinkedList;

public class DcAitemlist extends DBHelper {

    private Context context;
    private static DcAitemlist instance;

    public static DcAitemlist getInstance(Context context) {
        if(instance == null){
            instance = new DcAitemlist(context);
        }
        return instance;
    }

    public DcAitemlist(Context context) {
        super(context);
        this.context = context;
    }

    public void emptyaItemlist() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table.A_ITEMLIST.getName(), null,null);
        db.close();
    }

    public void insertaItemlist(aItemlist ail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = aItemlistQueryBuilder.prepareaItemlistInsertValues(ail, context);
        db.insertWithOnConflict(Table.A_ITEMLIST.getName(), null, value, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public LinkedList<aItemlist> getaItemlist() {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.A_ITEMLIST.getName() + " ORDER BY "
            + aItemlistKey.ITEMNAME.getKey() + " DESC";
        Cursor c = db.rawQuery(strQry, null);
        LinkedList<aItemlist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setaItemlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    public LinkedList<aItemlist> getFilteraItemlist(String searchStr) {
        SQLiteDatabase db = getReadableDatabase();
        String strQry = "SELECT * FROM " + Table.A_ITEMLIST.getName()
            + " WHERE " + aItemlistKey.ITEMNAME.getKey() + " LIKE ? "
            + " ORDER BY " + aItemlistKey.ITEMNAME.getKey() + " DESC";
        Cursor c = db.rawQuery(strQry, new String[] { searchStr });
        LinkedList<aItemlist> list = new LinkedList<>();
        while (c.moveToNext()) {
            list.add(setaItemlist(c));
        }
        c.close();
        db.close();
        return list;
    }

    private aItemlist setaItemlist(Cursor c) {
        return new aItemlist(
            c.getString(c.getColumnIndex(aItemlistKey.INTEGRATION_RECID.getKey())),
            c.getInt(c.getColumnIndex(aItemlistKey.RECID.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.OLD_SKU.getKey())),
            c.getInt(c.getColumnIndex(aItemlistKey.BASEUNIT_RECID.getKey())),
            c.getDouble(c.getColumnIndex(aItemlistKey.BASEUNIT_QTY.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.ITEMNO.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.ITEMNAME.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.ITEMNAME_WUNIT.getKey())),
            c.getDouble(c.getColumnIndex(aItemlistKey.QUANTITY_INUNIT.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.DEPT.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.UNIT.getKey())),
            c.getInt(c.getColumnIndex(aItemlistKey.TBLUNIT_RECID.getKey())),
            c.getInt(c.getColumnIndex(aItemlistKey.UNIT_TOCONVERT.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.BARCODENO.getKey())),
            c.getInt(c.getColumnIndex(aItemlistKey.F_BASE.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.D_ITEMDEPARTMENT_CODE.getKey())),
            c.getDouble(c.getColumnIndex(aItemlistKey.SELLING_PRICE.getKey())),
            c.getDouble(c.getColumnIndex(aItemlistKey.COST_PRICE.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.TAXCODE.getKey())),
            c.getInt(c.getColumnIndex(aItemlistKey.EXPENSE_ACCT.getKey())),
            c.getInt(c.getColumnIndex(aItemlistKey.INCOME_ACCT.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.DATA_VISIBILITY.getKey())),
            c.getString(c.getColumnIndex(aItemlistKey.BARCODENO1.getKey()))
        );
    }
}
