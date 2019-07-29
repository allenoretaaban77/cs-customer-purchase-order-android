package com.fnc.receiving.android.enumeration;

public enum CommonResponseKey {
    RESPONSE("response"),
    MESSAGE("message"),
    MESSAGES("messages"),
    ERROR_MESSAGE("error_messages"),
    ERROR_CODE("error_code"),
    RESPONSE_MESSAGE("response_message"),
    RESPONSE_CODE("response_code"),
    SUCCESS("success"),
    SUCCESS_MSG("success_messages"),
    SUCCESS_FLAG("success_flag"),
    ERRORS("errors"),
    PAGE_NUMBER("page_no"),
    COLOR("color"),
    SIZE("size"),
    ITEM_CODE("item_code"),
    LIMIT("limit");

    private String key;

    CommonResponseKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
