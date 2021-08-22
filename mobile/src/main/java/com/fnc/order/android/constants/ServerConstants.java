package com.fnc.order.android.constants;

import java.util.HashMap;

public class ServerConstants {

    public static final String DEFAULT_SERVER_URL = "http://apics.fncnathaniel.com/";
//    public static final String DEFAULT_SERVER_URL = "http://192.168.1.200:81/";
    public static final String DEFAULT_CN = "backoffice";
    public static final String DEFAULT_DBID = "BackofficeLive";
    public static final String LOGDB = "clientManagement";

    public static final HashMap<String,String> getHeaderLogin() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("api_key", "ginataang_munggo");
        headers.put("api_class", "login");
        return headers;
    }

    public static final HashMap<String,String> getHeaderOrder() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("api_key", "ginataang_munggo");
        headers.put("api_class", "custopo");
        return headers;
    }

    public static final HashMap<String,String> getHeaderConfig() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Api-Key", "ginataang_munggo");
        return headers;
    }
}
