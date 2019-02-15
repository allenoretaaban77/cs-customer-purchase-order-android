package com.fnc.order.android.enumeration;

public enum SharedKey {

    SERVER_URL("server_url"),
    DOMAIN_SERVER_URL("domain_server_url"),
    LOCAL_SERVER_URL("local_server_url"),
    WAITING_FOR_POST("waiting_for_post"),
    WAITING_STORE("waiting_store"),
    WAITING_RECID("waiting_recid"),
    FIRST_RUN("first_run"),
    CURRENT_REMARKS("current_remarks"),
    CURRENT_STORE("current_store_name"),
    CURRENT_JSON_POST("current_json_post"),
    CURRENT_DATE("current_date"),
    CURRENT_INCLUDEDPO("current_includedpo"),
    CURRENT_CUSTOMER_ID("current_customerid"),
    CURRENT_CUSTOMER_INTEGRATION_ID("customer_integ_recid"),
    DATA_CHECKLIST("dtchecklist"),
    DATA_STORELIST("dtstorelist"),
    SEARCHED_ITEMS("searched_items");

    private String key;
    SharedKey(String key) { this.key = key; }
    public String getKey() { return key; }
}