package com.fnc.order.android.database;

import android.content.ContentValues;

import com.fnc.order.android.enumeration.OrderKey;

import java.util.LinkedList;

public class QueryBuilder {
    private static final String TAG = QueryBuilder.class.getSimpleName();

    public static String createTables(Table table, LinkedList<?> list) {
        StringBuilder fields = new StringBuilder();
        String query = "CREATE TABLE "+ table.getName() +" ('id' INTEGER PRIMARY KEY AUTOINCREMENT, [fields])";
        for(Object field : list) {
            fields.append("'" + getFieldName(field) + "' " + getDataType(field) + ",");
        }
        String mFields = fields.substring(0,fields.length()-1);
        return query.replace("[fields]",mFields);
    }

    private static String getFieldName(Object field) {
        String fieldName = null;
        if(field instanceof OrderKey)
            fieldName = ((OrderKey)field).getKey();
        return fieldName;
    }

    private static String getDataType(Object field) {
        String dataType = null;
        if(field instanceof OrderKey)
            dataType = ((OrderKey)field).getDataType();
        return dataType;
    }

    public static String assembleFields(LinkedList<?> list) {
        StringBuilder fields = new StringBuilder();

        for(Object obj : list) {
            fields.append(getFieldName(obj) + ",");
        }

        return String.valueOf(fields.substring(0,fields.length()-1));
    }

    public static String buildInsert(String fields, Table table, ContentValues values, String conCol, String uniqueVal) {
        return "INSERT INTO "+ table.getName() +"("+fields+")" +
                " SELECT * FROM (SELECT "+values+") as tmp WHERE " +
                " NOT EXISTS(SELECT * FROM "+table.getName()+" WHERE "+conCol+" = '"+uniqueVal+"')";
    }

    public static String selectAll(Table table, String conditionCol, String value) {
        return "Select * from "+table.getName() +
                " WHERE "+ conditionCol +"='"+ value+"' order by id desc" ;
    }

    public static String selectAll(Table table) {
        return "Select * from " + table.getName() + " order by id desc";
    }
}
