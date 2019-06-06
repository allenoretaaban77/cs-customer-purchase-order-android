package com.fnc.order.android.constants;

import java.util.HashMap;

public class ServerConstants {

    public static final String CERTIFICATE_PATH = "";
    public static final String SERVER_URL = "http://192.168.1.200:81/";
//    public static final String SERVER_URL = "http://beta.apics.fncnathaniel.com/";
//    public static final String SERVER_URL = "http://apics.fncnathaniel.com/";
    public static final String LOGDB = "clientManagement";
//    public static final String CN = "beta";
//    public static final String CN = "backoffice";
    public static final String CN = "massive";
    public static final String DRIVER = "DRVR";

    public static final boolean IS_HTTPS = false;

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

    public static final HashMap<String,String> getHeaderPO() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("api_key", "ginataang_munggo");
        headers.put("api_class", "simplePOS");
        return headers;
    }
}
