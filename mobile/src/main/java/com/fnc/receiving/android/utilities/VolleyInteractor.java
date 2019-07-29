package com.fnc.receiving.android.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
//import com.android.volley.error.AuthFailureError;
//import com.android.volley.error.VolleyError;
//import com.android.volley.request.StringRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fnc.receiving.android.callback.VolleyCallback;
import com.fnc.receiving.android.constants.ServerConstants;
import com.fnc.receiving.android.enumeration.API;
import com.fnc.receiving.android.enumeration.SharedKey;
import com.github.yangweigbh.volleyx.VolleyX;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VolleyInteractor {

    private RequestQueue requestQueue;

    public VolleyCallback callback;
    public void registerCallback(VolleyCallback callback) {
        this.callback = callback;
    }

    public void getDeviceProfile(final Context ctx, final HashMap<String, String> params,
                                 final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.GET,
                        SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                                + API.GET_DEVICE_PROFILE.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "getdeviceprofile");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "getdeviceprofile");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderOdPos();
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

    public void getPreRequisite(final Context ctx, final HashMap<String, String> params,
                                final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.GET,
                        SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                                + API.GET_PRE_REQUISITE.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "getprerequisite");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "getprerequisite");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderOdPos();
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

    public void login(final Context ctx, final HashMap<String, String> params) {
        params.put("logdb", ServerConstants.LOGDB);

        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.POST,
                        SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                                + API.LOGIN.getApi(),
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

    public void getUsers(final Context ctx, final HashMap<String, String> params,
                             final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.GET,
                        SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                                + API.GET_USERS.getApi()+ "?" + strParams,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(callback != null) {
                                    callback.onRequestSuccess(response, "getusers");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(callback != null) {
                            callback.onRequestFail(volleyError, "getusers");
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return ServerConstants.getHeaderPO();
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
                        SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                                + API.POST_UPDATE_EMPLOYEE.getApi()+ "?" + strParams,
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
                        SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                                + API.GET_VERIFIED.getApi()+ "?" + strParams,
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

    public void getCustomers(final Context ctx, final HashMap<String, String> params,
                             final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                StringRequest strRequest = new StringRequest( Request.Method.GET,
                        SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                                + API.GET_CUSTOMERS.getApi()+ "?" + strParams,
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
                        SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                                + API.GET_ITEMLIST.getApi()+ "?" + strParams,
                        null, null) {
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
                VolleyX.init(ctx);
                VolleyX.from(strRequest).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            Log.d("getitemlist", "onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            callback.onRequestFail(ve, "searchitem");
                        }

                        @Override
                        public void onNext(String response) {
                            callback.onRequestSuccess(response, "searchitem");
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
            }
        }).start();
    }

    public void postOrders(final Context ctx, final String param) {
        new Thread(new Runnable(){
            public void run(){
                String urlStr = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                        + API.POST_ORDER.getApi();
                Log.i("DSX", urlStr + " | " + param);
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
}
