package com.fnc.receiving.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import com.fnc.receiving.android.enumeration.SharedKey;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedData {
    private static SharedData instance;
    private SharedPreferences sharedPreferences;

    public static SharedData getInstance(Context context) {
        if (instance == null) {
            instance = new SharedData(context);
        }
        return instance;
    }

    private SharedData(Context context) {
        sharedPreferences = context.getSharedPreferences("FNCOrderSharedData", Context.MODE_PRIVATE);
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public void saveStringSet(String key, Set<String> value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putStringSet(key, value);
        prefsEditor.apply();
    }

    public void saveBoolean(String key, boolean isTrue) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(key, isTrue);
        prefsEditor.apply();
    }

    public boolean getBoolean(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key,false);
        }
        return false;
    }

    public String getData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return null;
    }

    public Set<String> getStringSet(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getStringSet(key, null);
        }
        return null;
    }

    public boolean isPrefExists(String key) {
        return sharedPreferences.contains(key);
    }

    public void removeSinglePref(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    /*public void removeAllPref() {
        String fr = this.getData(SharedKey.FIRST_RUN.getKey());
        sharedPreferences.edit().clear().apply();

        if(fr == null || fr.equals("")){
        }else{
            this.saveData(SharedKey.FIRST_RUN.getKey(), fr);
        }
    }*/

    public void removeAllPref() {
        String fr = this.getData(SharedKey.FIRST_RUN.getKey());
        sharedPreferences.edit().clear().apply();

        sharedPreferences.edit()
                .remove("auth_token");
    }
}


