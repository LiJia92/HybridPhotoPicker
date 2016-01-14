package com.example.hybridphotopicker;

import android.webkit.WebView;

import com.example.hybridphotopicker.hybrid.JsCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MyBridge {

    private static WebViewActivity activity;

    public static void init(WebViewActivity activity) {
        MyBridge.activity = activity;
    }

    public static void send(WebView webView, JSONObject jsonObject, JsCallback jsCallback) {
        try {
            String method = jsonObject.getString("cmd");
            switch (method) {
                case "getToken":
                    String token = activity.getToken();
                    if (jsCallback != null) {
                        jsCallback.apply(token);
                    }
                    break;
                case "getPictures":
                    activity.getPictures(jsCallback);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsCallback.JsCallbackException e) {
            e.printStackTrace();
        }
    }

}
