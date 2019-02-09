package com.fnc.order.android.enumeration;

public enum API {

//    LOGIN("Login"),
//    GET_USERS("getUsers"),
//    GET_ITEMKLISt("getItemlist"),
    LOGIN("api/Login"),
    GET_CUSTOMERS("api/getcoCustomerList"),
    GET_ITEMLIST("api/getcoItemlist"),
    GET_EMPLOYEES("api/getEmployeeByPosition"),
    POST_RETURNS("api/postItemCReturns"),

    POST_LOADSTATE("api/PostChecklistLoadedState"),
    IDENTITY_ID("identityId"),
    DATA_USER("dtuser"),
    DATA_COMPANY("dtcompany"),
    DATA_BUSINESS("dtbusiness"),
    DATA_CUSTOMER("dtcustomer"),
    DATA_CHECKLIST("dtchecklist");

    private String api;
    API(String api) {
        this.api = api;
    }
    public String getApi() {
        return api;
    }

}
