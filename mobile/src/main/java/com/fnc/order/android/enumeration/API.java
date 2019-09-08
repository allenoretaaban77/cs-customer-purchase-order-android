package com.fnc.order.android.enumeration;

public enum API {

//    LOGIN("Login"),
//    GET_USERS("getUsers"),
//    GET_ITEMKLISt("getItemlist"),
    LOGIN("api/Login"),
    GET_USERS("api/getCoUser"),
    GET_CUSTOMERS("api/getcoCustomerList"),
    GET_ITEMLIST("api/getcoItemlist"),
    GET_EMPLOYEES("api/getEmployeeByPosition"),
    GET_VERIFIED("api/getVerified_linkOldEmployeeNo"),
    POST_UPDATE_EMPLOYEE("api/updateRefEmployeeID"),
    POST_ORDER("api/PostCOrder"),
    GET_DEVICE_PROFILE("api/getCoDeviceProfile"),
    GET_PRE_REQUISITE("api/getCoPreReq"),

    POST_LOADSTATE("api/PostChecklistLoadedState"),
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
