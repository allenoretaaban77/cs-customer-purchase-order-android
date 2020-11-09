package com.fnc.order.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import com.fnc.order.android.enumeration.MenulistKey;
import com.fnc.order.android.enumeration.OrderKey;
import com.fnc.order.android.enumeration.OrderedKey;
import com.fnc.order.android.enumeration.UserslistKey;
import com.fnc.order.android.enumeration.aBranchlistKey;
import com.fnc.order.android.enumeration.aItemlistKey;
import com.fnc.order.android.enumeration.aStaffsKey;
import com.fnc.order.android.model.Ordered;
import com.fnc.order.android.utilities.Helper;

import java.io.File;
import java.util.LinkedList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    public static final String DBPath = Environment.getExternalStorageDirectory().toString()
            + File.separator + "Android"
            + File.separator + "data"
            + File.separator;

    public DBHelper(Context context) {
        super(context, Helper.getProjectPath(context) + DbConstants.DB_NAME, null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTables(Table.ORDER, setOrderFields()));
        db.execSQL(createTables(Table.ORDERED, setOrderedFields()));
        db.execSQL(createTables(Table.MENULIST, setMenulistFields()));
        db.execSQL(createTables(Table.USERSLIST, setUserlistFields()));
        db.execSQL(createTables(Table.A_ITEMLIST, setaItemlistFields()));
        db.execSQL(createTables(Table.STAFFS, setStaffsFields()));
        db.execSQL(createTables(Table.STAFFS_INACTIVE, setStaffsFields()));
        db.execSQL(createTables(Table.BRANCHLIST, setBranchlistFields()));
        Log.i(TAG,"Database created path : " + db.getPath());
    }

    protected String createTables(Table table,LinkedList<?> list) {
        return QueryBuilder.createTables(table,list);
    }

    protected LinkedList<OrderKey> setOrderFields() {
        LinkedList<OrderKey> fields = new LinkedList<>();
        fields.add(OrderKey.QUANTITY);
        fields.add(OrderKey.FREE);
        fields.add(OrderKey.ITEM_RECID);
        fields.add(OrderKey.ITEM_NAME);
        fields.add(OrderKey.UNIT_NAME);
        fields.add(OrderKey.REMARKS);
        fields.add(OrderKey.OLD_SKU);
        fields.add(OrderKey.SELLING_PRICE);
        fields.add(OrderKey.TOTAL);
        fields.add(OrderKey.IS_CHECKED);
        fields.add(OrderKey.IS_ERROR);
        fields.add(OrderKey.IS_LOCKED);
        return fields;
    }

    protected LinkedList<OrderedKey> setOrderedFields() {
        LinkedList<OrderedKey> fields = new LinkedList<>();
        fields.add(OrderedKey.CUSTOMER_INTEG_RECID);
        fields.add(OrderedKey.CUSTOMER_RECID);
        fields.add(OrderedKey.CUSTOMER_NAME);
        fields.add(OrderedKey.DELIVERY_DATE);
        fields.add(OrderedKey.CREATED_BY);
        fields.add(OrderedKey.REMARKS);
        fields.add(OrderedKey.REF_EMPLOYEE_NO);
        fields.add(OrderedKey.JSON);
        fields.add(OrderedKey.JSON_COMPLETE);
        fields.add(OrderedKey.GRAND_TOTAL);
        fields.add(OrderedKey.GRAND_TOTAL_CNT);
        fields.add(OrderedKey.DATETIME);
        fields.add(OrderedKey.STATUS);
        fields.add(OrderedKey.REF_RECID);
        fields.add(OrderedKey.DELIVERY_DATE_DEFAULT);
        return fields;
    }

    protected LinkedList<MenulistKey> setMenulistFields() {
        LinkedList<MenulistKey> fields = new LinkedList<>();
        fields.add(MenulistKey.CUSTOMER_ID);
        fields.add(MenulistKey.CUSTOMER_INTEG_ID);
        fields.add(MenulistKey.CUSTOMER_NAME);
        fields.add(MenulistKey.REMARKS);
        fields.add(MenulistKey.RECORD_COUNT);
        fields.add(MenulistKey.ALPHA_CHAR);
        fields.add(MenulistKey.INVOICE);
        return fields;
    }

    protected LinkedList<UserslistKey> setUserlistFields() {
        LinkedList<UserslistKey> fields = new LinkedList<>();
        fields.add(UserslistKey.IDENTITYID);
        fields.add(UserslistKey.EMAIL);
        fields.add(UserslistKey.PASSWORD);
        fields.add(UserslistKey.FIRSTNAME);
        fields.add(UserslistKey.MIDDLENAME);
        fields.add(UserslistKey.LASTNAME);
        fields.add(UserslistKey.DATEOFBIRTH);
        fields.add(UserslistKey.VERIFIED);
        fields.add(UserslistKey.COMPANY_UNIQIE);
        fields.add(UserslistKey.REF_EMPLOYEE_NO);
        fields.add(UserslistKey.TEMPO_ID);
        return fields;
    }

    protected LinkedList<aItemlistKey> setaItemlistFields() {
        LinkedList<aItemlistKey> fields = new LinkedList<>();
        fields.add(aItemlistKey.INTEGRATION_RECID);
        fields.add(aItemlistKey.RECID);
        fields.add(aItemlistKey.OLD_SKU);
        fields.add(aItemlistKey.BASEUNIT_RECID);
        fields.add(aItemlistKey.BASEUNIT_QTY);
        fields.add(aItemlistKey.ITEMNO);
        fields.add(aItemlistKey.ITEMNAME);
        fields.add(aItemlistKey.ITEMNAME_WUNIT);
        fields.add(aItemlistKey.QUANTITY_INUNIT);
        fields.add(aItemlistKey.DEPT);
        fields.add(aItemlistKey.UNIT);
        fields.add(aItemlistKey.TBLUNIT_RECID);
        fields.add(aItemlistKey.UNIT_TOCONVERT);
        fields.add(aItemlistKey.BARCODENO);
        fields.add(aItemlistKey.F_BASE);
        fields.add(aItemlistKey.D_ITEMDEPARTMENT_CODE);
        fields.add(aItemlistKey.SELLING_PRICE);
        fields.add(aItemlistKey.COST_PRICE);
        fields.add(aItemlistKey.TAXCODE);
        fields.add(aItemlistKey.EXPENSE_ACCT);
        fields.add(aItemlistKey.INCOME_ACCT);
        fields.add(aItemlistKey.DATA_VISIBILITY);
        fields.add(aItemlistKey.BARCODENO1);
        return fields;
    }

    protected LinkedList<aStaffsKey> setStaffsFields() {
        LinkedList<aStaffsKey> fields = new LinkedList<>();
        fields.add(aStaffsKey.EMPID);
        fields.add(aStaffsKey.REFEMPNO);
        fields.add(aStaffsKey.EMPNO);
        fields.add(aStaffsKey.EMAIL);
        fields.add(aStaffsKey.NAME);
        fields.add(aStaffsKey.BRANCH);
        fields.add(aStaffsKey.JOBTITLE);
        fields.add(aStaffsKey.PASS);
        fields.add(aStaffsKey.ACTIVE);
        fields.add(aStaffsKey.ISMOBILEADMIN);
        return fields;
    }

    protected LinkedList<aBranchlistKey> setBranchlistFields() {
        LinkedList<aBranchlistKey> fields = new LinkedList<>();
        fields.add(aBranchlistKey.BRANCHID);
        fields.add(aBranchlistKey.BRANCHCODE);
        fields.add(aBranchlistKey.DEVICEID);
        fields.add(aBranchlistKey.DESCRIPTION);
        fields.add(aBranchlistKey.DEVICEID1);
        fields.add(aBranchlistKey.ACTIVE);
        fields.add(aBranchlistKey.CUSTOMERID);
        fields.add(aBranchlistKey.OLDBRANCHID);
        fields.add(aBranchlistKey.OLDCUSTOMERID);
        return fields;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion + 1;
        switch (version) {
            case 2:
            case 3:
            case 4:
                try {
                    db.execSQL("ALTER TABLE " + Table.ORDERED.getName() + " ADD COLUMN " + OrderedKey.GRAND_TOTAL_CNT.getKey() + " TEXT ");
                    db.execSQL("UPDATE " + Table.ORDERED.getName() + " SET " + OrderedKey.GRAND_TOTAL_CNT.getKey() + " = '0'");
                    db.execSQL("ALTER TABLE " + Table.ORDERED.getName() + " ADD COLUMN " + OrderedKey.DELIVERY_DATE_DEFAULT.getKey() + " TEXT ");
                    db.execSQL("UPDATE " + Table.ORDERED.getName() + " SET " + OrderedKey.DELIVERY_DATE_DEFAULT.getKey() + " = '0'");
                } catch (Exception e) { }
            case 5:
                try {
                    db.execSQL("ALTER TABLE " + Table.MENULIST.getName() + " ADD COLUMN " + MenulistKey.INVOICE.getKey() + " TEXT ");
                    db.execSQL("UPDATE " + Table.MENULIST.getName() + " SET " + MenulistKey.INVOICE.getKey() + " = ''");
                } catch (Exception e) { }
                break;
        }
    }
}
