package com.fnc.receiving.android.enumeration;

public enum API {

    LOGIN("api/Login"),
    GET_DEVICE_PROFILE("api/getDeviceProfile"),
    GET_PRE_REQUISITE("api/getPreReq"),
    GET_USERS("api/getSimplePOSUser"),
    GET_CUSTOMERS("api/getcoCustomerList"),
    GET_ITEMLIST("api/getcoItemlist"),
    GET_EMPLOYEES("api/getEmployeeByPosition"),
    GET_VERIFIED("api/getVerified_linkOldEmployeeNo"),
    POST_UPDATE_EMPLOYEE("api/updateRefEmployeeID"),
    POST_ORDER("api/PostCOrder"),

    POST_LOADSTATE("api/PostChecklistLoadedState"),
    IDENTITY_ID("identityId"),
    EMPLOYEE_ID("reference_employee_no"),
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
