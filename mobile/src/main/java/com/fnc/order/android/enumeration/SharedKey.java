package com.fnc.order.android.enumeration;

public enum SharedKey {

    SERVER_URL("server_url"),
    DOMAIN_SERVER_URL("domain_server_url"),
    LOCAL_SERVER_URL("local_server_url"),
    DATABASE("ref_database_01"),
    REF_DATABASE("ref_database_compare"),
    REF_ADMIN_USER("ref_admin_user"),
    REF_ADMIN_PASSWORD("ref_admin_password"),
    REF_ADMIN_FULLNAME("ref_admin_fullname"),
    DATABASE_OLD("ref_old_database"),
    SKU_VALIDATION("old_sku_validation"),
    PRELOAD_ITEMS("preload_items"),
    SAVE_PRODUCT_ITEMS("saved_product_items"),
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
    DEV_USERNAME("usernamne"),
    DEV_PASSWORD("password"),
    SEARCHED_ITEMS("searched_items"),
    IMEI_ID("imei_identification_01"),
    BRANCH_ID("branch_id"),
    DUMMY_BRANCH_ID("dummy_branch_id"),
    BRANCH_CODE("branch_code"),
    BRANCH_DESCRIPTION("branch_description"),
    CURRENT_PAGE("current_page"),
    IDENTITY_ID("identityId"),
    EMP_NO("reference_employee_no"),
    EMP_ID("employee_id"),
    REF_EMP_NO("reference_employee_no_x"),
    EMP_NAME("employee_name"),
    EMP_POSITION("employee_position"),
    REF_JOBTITLES("ref_jobtitles"),
    EMP_ISMOBILEADMIN("employee_ismobileadmin");

    private String key;
    SharedKey(String key) { this.key = key; }
    public String getKey() { return key; }
}