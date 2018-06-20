package com.beesightsoft.training6.utils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.beesightsoft.training6.service.model.Comment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HttpUtils {
    final static String TAG = HttpUtils.class.getSimpleName();

    public static List<Comment> get(String url) {
        String response = new HttpAsyncTask(url).executeGetMethod();

        Gson gson = new GsonBuilder().create();

        Type type = new TypeToken<List<Comment>>() {
        }.getType();
        List<Comment> result = gson.fromJson(response, type);

        return result;
    }

    public static Comment post(String url, NameValuePair[] values) {
        String response = new HttpAsyncTask(url).executePostMethod(values);
        Gson gson = new GsonBuilder().create();
        Comment comment = gson.fromJson(response, Comment.class);
        return comment;
    }

    public static boolean delete(String url) {
        boolean response = new HttpAsyncTask(url).executeDeleteMethod();
        return response;
    }

    static class HttpAsyncTask extends AsyncTask<HttpEnum, String, String> {
        final String TAG = HttpUtils.class.getSimpleName();

        String url;
        HttpClient httpclient;

        Bundle bundle;

        HttpAsyncTask(String url) {
            this.url = url;
            this.bundle = new Bundle();
        }

        @Override
        protected void onPreExecute() {
            httpclient = new HttpClient();
        }

        @Override
        protected String doInBackground(HttpEnum... enums) {
            String result = "";
            HttpEnum httpEnum = HttpEnum.GET;
            if (enums.length != 0) {
                httpEnum = enums[0];
            }
            switch (httpEnum) {
                case GET:
                    result = getResultGetMethod();
                    break;
                case POST:
                    result = getResultPostMethod();
                    break;
                case DELETE:
                    result = getResultDeleteMethod();
                    break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "onPostExecute: done");
        }

        //todo execute GET, POST, PUT, DELETE Method
        String executeGetMethod() {
            String result = "";
            try {
                result = execute(HttpEnum.GET).get();
            } catch (Exception e) {
                Log.d(TAG, "executeGetMethod: " + e.getMessage());
            }
            return result;
        }

        String executePostMethod(NameValuePair[] values) {
            Gson gson = new GsonBuilder().create();
            String param_post = gson.toJson(values);
            bundle.putString("POST", param_post);

            String result = "";
            try {
                result = execute(HttpEnum.POST).get();
            } catch (Exception e) {
                Log.d(TAG, "executePostMethod: " + e.getMessage());
            }
            return result;
        }

        boolean executeDeleteMethod() {
            try {
                execute(HttpEnum.DELETE);
                return true;
            } catch (Exception e) {
                Log.d(TAG, "executeDeleteMethod: " + e.getMessage());
            }
            return false;
        }

        //todo response GET, POST, PUT, DELETE Method
        String getResultGetMethod() {
            GetMethod httpMethod = new GetMethod(url);
            String result = "";

            try {
                int statusCode = httpclient.executeMethod(httpMethod);

                if (statusCode != HttpStatus.SC_OK) {
                    Log.d(TAG, "Method failed: " + httpMethod.getStatusLine());
                }
                byte[] responseBody = httpMethod.getResponseBody();
                result = new String(responseBody);

            } catch (Exception e) {
                Log.d(TAG, "connect: " + e.getMessage());
            }
            httpMethod.releaseConnection();

            return result;
        }

        String getResultPostMethod() {
            PostMethod httpMethod = new PostMethod(url);

            String params_post = bundle.getString("POST", "");
            if (!params_post.isEmpty()) {
                Gson gson = new GsonBuilder().create();
                NameValuePair[] nameValuePairs = gson.fromJson(params_post, NameValuePair[].class);
                httpMethod.setRequestBody(nameValuePairs);
            }
            String result = "";

            try {
                int statusCode = httpclient.executeMethod(httpMethod);

                if (statusCode != HttpStatus.SC_OK) {
                    Log.d(TAG, "Method failed: " + httpMethod.getStatusLine());
                }
                byte[] responseBody = httpMethod.getResponseBody();
                result = new String(responseBody);
            } catch (Exception e) {
                Log.d(TAG, "connect: " + e.getMessage());
            }
            httpMethod.releaseConnection();

            return result;
        }

        String getResultDeleteMethod() {
            DeleteMethod httpMethod = new DeleteMethod(url);
            String result = "";

            try {
                int statusCode = httpclient.executeMethod(httpMethod);

                if (statusCode != HttpStatus.SC_OK) {
                    Log.d(TAG, "Method failed: " + httpMethod.getStatusLine());
                }
                byte[] responseBody = httpMethod.getResponseBody();
                result = new String(responseBody);
            } catch (Exception e) {
                Log.d(TAG, "connect: " + e.getMessage());
            }
            httpMethod.releaseConnection();

            return result;
        }
    }

    static enum HttpEnum {
        GET, POST, PUT, DELETE
    }
}
