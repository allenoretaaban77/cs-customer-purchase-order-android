package com.fnc.order.android.enumeration;

public enum API {

//    LOGIN("Login"),
//    GET_USERS("getUsers"),
//    GET_ITEMKLISt("getItemlist"),
    LOGIN("api/Login"),
    GET_USERS("api/getCoUser"),
    GET_ITEMSRB("api/getcoItemSRB"),
    GET_CUSTOMERS("api/getcoCustomerList"),
    GET_ITEMLIST("api/getcoItemlist"),
    GET_ITEMLIST_SRP("api/postcoItemsrp"),
    GET_EMPLOYEES("api/getEmployeeByPosition"),
    GET_VERIFIED("api/getVerified_linkOldEmployeeNo"),
    POST_UPDATE_EMPLOYEE("api/updateRefEmployeeID"),
    POST_ORDER("api/PostCOrder"),
    POST_UPDATE_PASSWORD("api/updateLocalPassword"),
    GET_DEVICE_PROFILE("api/getCoDeviceProfile"),
    GET_ADMIN_GROUPINGS("api/getCoAdminGroupings"),
    POST_BRANCH_SIGNUP("api/postCoBranchSignUp"),
    POST_ADMIN_GROUPINGS_SET("api/postcoAdminGroupingSet"),
    POST_BRANCH_IMEI("api/postBranchImei"),
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
