package com.idaymay.dzt.common.utils.web;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 请求工具封装
 * @author littlehui
 * @date 2021/11/15 14:43
 * @version 1.0
 */
public class OkHttpUtil {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    static Long TIMEOUT = 15L;

    private static OkHttpClient client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)//是否开启缓存
            .connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))//连接池
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build();

    public static String post(String url, String json) throws IOException {
        return postJson(url, json, null);
    }

    public static String postJson(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder builder = new Request.Builder().addHeader("Connection","close");
        if (headers != null && headers.size() > 0) {
            for (String headerKey : headers.keySet()) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        builder.url(url).post(body);
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            if (response == null || response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }


    public static String get(String getUrl) throws IOException {
        return get(getUrl, null);
    }

    public static String get(String getUrl, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(getUrl).get();
        if (headers != null && headers.size() > 0) {
            for (String headerKey : headers.keySet()) {
                if (headers.get(headerKey) != null) {
                    builder.addHeader(headerKey, headers.get(headerKey));
                }
            }
        }
        builder.addHeader("Connection", "close");
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            if (response == null || response.body() == null) {
                return null;
            }
            return response.body().string();
        }
    }


    public static String postFormData(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {

        FormBody.Builder formBodyBuilder = buildFormRequestBody(params, null);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.post(formBodyBuilder.build());
        if (headers != null && headers.size() > 0) {
            for (String headerKey : headers.keySet()) {
                requestBuilder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        requestBuilder.addHeader("Content-type", "application/x-www-form-urlencoded");
        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            if (response == null || response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }

    private static FormBody.Builder buildFormRequestBody(Map<String, Object> req, String[] skipBuilds) throws Exception {
        Map<String, Object> params = buildRequestParams(req, skipBuilds);
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> builder.add(k, String.valueOf(v)));
        return builder;
    }

    private static Map<String, Object> buildRequestParams(Map<String, Object> params, String[] skipBuilds) throws Exception {
        if (skipBuilds != null) {
            for (String skipField : skipBuilds) {
                params.remove(skipField);
            }
        }
        return params;
    }
}
