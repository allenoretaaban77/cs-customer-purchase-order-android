package com.fnc.order.android.utilities;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.enumeration.API;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VolleyInteractor {

    private RequestQueue requestQueue;

    public VolleyCallback callback;
    public void registerCallback(VolleyCallback callback) {
        this.callback = callback;
    }

    public void login(final Context ctx, final HashMap<String, String> params) {
        params.put("logdb", ServerConstants.LOGDB);

        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.POST,
                        ServerConstants.SERVER_URL + API.LOGIN.getApi(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "login");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "login");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderLogin();
                    }
                    public Map<String, String> getParams(){
                        return params;
                    }

                };
                requestQueue = Volley.newRequestQueue(ctx);
                int socketTimeout = 10000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
                requestQueue.getCache().clear();
                requestQueue.add(strRequest);
            }
        }).start();
    }

    public void updateEmployeeId(final Context ctx, final HashMap<String, String> params,
                         final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.POST,
                        ServerConstants.SERVER_URL + API.POST_UPDATE_EMPLOYEE.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "updateemployeeid");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "updateemployeeid");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderLogin();
                    }
                    public Map<String, String> getParams(){
                        return params;
                    }

                };
                requestQueue = Volley.newRequestQueue(ctx);
                int socketTimeout = 10000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
                requestQueue.getCache().clear();
                requestQueue.add(strRequest);
            }
        }).start();
    }

    public void validate(final Context ctx, final HashMap<String, String> params,
                           final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.GET,
                        ServerConstants.SERVER_URL + API.GET_VERIFIED.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "validate");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "validate");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderLogin();
                    }
                    public Map<String, String> getParams(){
                        return params;
                    }

                };
                requestQueue = Volley.newRequestQueue(ctx);
                int socketTimeout = 10000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
                requestQueue.getCache().clear();
                requestQueue.add(strRequest);
            }
        }).start();
    }

    public void getDrivers(final Context ctx, final HashMap<String, String> params,
                             final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.GET,
                        ServerConstants.SERVER_URL + API.GET_EMPLOYEES.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "searchemployee");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "searchemployee");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderOrder();
                    }
                    public Map<String, String> getParams(){
                        return params;
                    }

                };
                requestQueue = Volley.newRequestQueue(ctx);
                int socketTimeout = 10000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
                requestQueue.getCache().clear();
                requestQueue.add(strRequest);
            }
        }).start();
    }

    public void getCustomers(final Context ctx, final HashMap<String, String> params,
                             final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.GET,
                        ServerConstants.SERVER_URL + API.GET_CUSTOMERS.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "searchcustomer");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "searchcustomer");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderOrder();
                    }
                    public Map<String, String> getParams(){
                        return params;
                    }

                };
                requestQueue = Volley.newRequestQueue(ctx);
                int socketTimeout = 10000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
                requestQueue.getCache().clear();
                requestQueue.add(strRequest);
            }
        }).start();
    }

    public void getItemlist(final Context ctx, final HashMap<String, String> params,
                         final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.GET,
                        ServerConstants.SERVER_URL + API.GET_ITEMLIST.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "searchitem");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "searchitem");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderOrder();
                    }
                    public Map<String, String> getParams(){
                        return params;
                    }

                };
                requestQueue = Volley.newRequestQueue(ctx);
                int socketTimeout = 10000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
                requestQueue.getCache().clear();
                requestQueue.add(strRequest);
            }
        }).start();
    }

    public void postOrders(final Context ctx, final String param) {
        new Thread(new Runnable(){
            public void run(){
                String urlStr = ServerConstants.SERVER_URL + API.POST_ORDER.getApi();
                StringRequest strRequest = new StringRequest( Request.Method.POST, urlStr,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "postchecklist");
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                if(callback != null) {
                                    callback.onRequestFail(volleyError, "postchecklist");
                                }
                            }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderOrder();
                    }
                    @Override
                    public byte[] getBody() throws AuthFailureError  {
                        String str = param;
                        return str.getBytes();
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }

                };
                requestQueue = Volley.newRequestQueue(ctx);
                int socketTimeout = 10000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
                requestQueue.getCache().clear();
                requestQueue.add(strRequest);
            }
        }).start();
    }

    public void postLoadState(final Context ctx, final HashMap<String, String> params,
                                    final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.POST,
                        ServerConstants.SERVER_URL + API.POST_LOADSTATE.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "postloadstate");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "postloadstate");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderOrder();
                    }
                    public Map<String, String> getParams(){
                        return params;
                    }

                };
                requestQueue = Volley.newRequestQueue(ctx);
                int socketTimeout = 10000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
                requestQueue.getCache().clear();
                requestQueue.add(strRequest);
            }
        }).start();
    }
}
