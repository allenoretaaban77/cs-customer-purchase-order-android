package com.fnc.order.android.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fnc.order.android.callback.VolleyCallback;
import com.fnc.order.android.constants.ServerConstants;
import com.fnc.order.android.enumeration.API;
import com.fnc.order.android.enumeration.SharedKey;
import com.github.yangweigbh.volleyx.VolleyX;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VolleyInteractor {

    private RequestQueue requestQueue;

    public VolleyCallback callback;
    public void registerCallback(VolleyCallback callback) {
        this.callback = callback;
    }

    public void login(final Context ctx, final HashMap<String, String> params, final String strParams) {
        new Thread( new Runnable(){
            public void run(){
                String url = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                    + API.LOGIN.getApi()+ "?" + strParams;
                Log.d("dsx", url);
                StringRequest strRequest = new StringRequest( Request.Method.POST, url,
                        null, null) {
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
                VolleyX.init(ctx);
                VolleyX.from(strRequest).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            Log.d("login", "postupdatepassword");
                        }
                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            callback.onRequestFail(ve, "login");
                        }
                        @Override
                        public void onNext(String response) {
                            Log.d("dsx", response);
                            callback.onRequestSuccess(response, "login");
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
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
                                Log.d("searchcustomer", "onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                VolleyError ve = new VolleyError();
                                callback.onRequestFail(ve, "searchcustomer");
                            }

                            @Override
                            public void onNext(String response) {
                                callback.onRequestSuccess(response, "searchcustomer");
                            }
                        });
                VolleyX.setRequestQueue(requestQueue);
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

    public void getDeviceProfile(final Context ctx, final HashMap<String, String> params,
                                 final String strParams, final String type) {
        new Thread(new Runnable(){
            public void run(){
                String url = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                        + API.GET_DEVICE_PROFILE.getApi()+ "?" + strParams;
                StringRequest strRequest = new StringRequest( Request.Method.GET, url,
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
                            Log.d("dsxoc", "getdeviceprofile");
                        }
                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            Log.d("dsxe getdeviceprofile", String.valueOf(ve.getMessage()));
                            callback.onRequestFail(ve, "getdeviceprofile");
                        }
                        @Override
                        public void onNext(String response) {
                            Log.d("dsx", response);
                            callback.onRequestSuccess(response, "getdeviceprofile|" + type);
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
            }
        }).start();
    }

    public void getPreRequisite(final Context ctx, final HashMap<String, String> params,
                                final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                String url = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                    + API.GET_PRE_REQUISITE.getApi()+ "?" + strParams;
                StringRequest strRequest = new StringRequest( Request.Method.GET, url,
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
                            Log.d("dsxoc", "getprerequisite");
                        }
                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            Log.d("dsxe getprerequisite", String.valueOf(ve.getMessage()));
                            callback.onRequestFail(ve, "getprerequisite");
                        }
                        @Override
                        public void onNext(String response) {
                            Log.d("dsx", response);
                            callback.onRequestSuccess(response, "getprerequisite");
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
            }
        }).start();
    }

    public void getAdminGroupings(final Context ctx, final HashMap<String, String> params,
                                 final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                String url = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                        + API.GET_ADMIN_GROUPINGS.getApi()+ "?" + strParams;
                StringRequest strRequest = new StringRequest( Request.Method.GET, url,
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
                            Log.d("dsxoc", "getadmingroupings");
                        }
                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            Log.d("dsxe getdeviceprofile", String.valueOf(ve.getMessage()));
                            callback.onRequestFail(ve, "getadmingroupings");
                        }
                        @Override
                        public void onNext(String response) {
                            Log.d("dsx", response);
                            callback.onRequestSuccess(response, "getadmingroupings");
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
            }
        }).start();
    }

    public void postBranchImei(final Context ctx, final HashMap<String, String> params,
                                 final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                String url = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                        + API.POST_BRANCH_IMEI.getApi()+ "?" + strParams;
                Log.d("dsx", url);
                StringRequest strRequest = new StringRequest( Request.Method.POST, url,
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
                            Log.d("dsxoc", "getpostbranchimei");
                        }
                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            Log.d("dsxe getpostbranchimei", String.valueOf(ve.getMessage()));
                            callback.onRequestFail(ve, "getpostbranchimei");
                        }
                        @Override
                        public void onNext(String response) {
                            Log.d("dsx", response);
                            callback.onRequestSuccess(response, "getpostbranchimei");
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
            }
        }).start();
    }

    public void postBranchSignUp(final Context ctx, final HashMap<String, String> params,
                                 final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                String url = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                        + API.POST_BRANCH_SIGNUP.getApi()+ "?" + strParams;
                Log.d("dsx", url);
                StringRequest strRequest = new StringRequest( Request.Method.POST, url,
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
                            Log.d("dsxoc", "getpostsignup");
                        }
                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            Log.d("dsxe getpostsignup", String.valueOf(ve.getMessage()));
                            callback.onRequestFail(ve, "getpostsignup");
                        }
                        @Override
                        public void onNext(String response) {
                            Log.d("dsx", response);
                            callback.onRequestSuccess(response, "getpostsignup");
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
            }
        }).start();
    }

    public void postAddJobTitle(final Context ctx, final HashMap<String, String> params,
                                 final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                String url = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                    + API.POST_ADMIN_GROUPINGS_SET.getApi()+ "?" + strParams;
                Log.d("dsx", url);
                StringRequest strRequest = new StringRequest( Request.Method.POST, url,
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
                            Log.d("dsxoc", "getpags");
                        }
                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            Log.d("dsxe getpags", String.valueOf(ve.getMessage()));
                            callback.onRequestFail(ve, "getpags");
                        }
                        @Override
                        public void onNext(String response) {
                            Log.d("dsx", response);
                            callback.onRequestSuccess(response, "getpags");
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
            }
        }).start();
    }

    public void postUpdatePassword(final Context ctx, final HashMap<String, String> params,
                               final String strParams) {
        new Thread(new Runnable(){
            public void run(){
                String url = SharedData.getInstance(ctx).getData(SharedKey.DOMAIN_SERVER_URL.getKey())
                    + API.POST_UPDATE_PASSWORD.getApi()+ "?" + strParams;
                Log.d("dsx", url);
                StringRequest strRequest = new StringRequest( Request.Method.POST, url,
                        null, null) {
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
                VolleyX.init(ctx);
                VolleyX.from(strRequest).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            Log.d("dsxoc", "postupdatepassword");
                        }
                        @Override
                        public void onError(Throwable e) {
                            VolleyError ve = new VolleyError();
                            Log.d("dsxe postupdatepassword", String.valueOf(ve.getMessage()));
                            callback.onRequestFail(ve, "postupdatepassword");
                        }
                        @Override
                        public void onNext(String response) {
                            Log.d("dsx", response);
                            callback.onRequestSuccess(response, "postupdatepassword");
                        }
                    });
                VolleyX.setRequestQueue(requestQueue);
            }
        }).start();
    }
}
