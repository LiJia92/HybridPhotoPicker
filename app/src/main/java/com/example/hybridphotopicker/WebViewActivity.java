package com.example.hybridphotopicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.hybridphotopicker.hybrid.InjectedChromeClient;
import com.example.hybridphotopicker.hybrid.JsCallback;
import com.lling.photopicker.PhotoPickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * WebView Activity
 * Created by Lijia on 2015-12-25.
 */
public class WebViewActivity extends Activity {

    private WebView webView;

    public static JsCallback onceCallback; // 单次回调

    private static final int PICK_PHOTO_REQUEST = 1; // 选择图片请求码
    private static final int MAX_PHOTO_NUM = 9; // 最大选择数量
    private static boolean showCamera = true; // 是否打开相机

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyBridge.init(this);
        webView = new WebView(this);
        // 切换到内容视图
        setContentView(webView);
        // 获取WebView配置
        WebSettings ws = webView.getSettings();
        // 启用JavaScript
        ws.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new InjectedChromeClient("WebViewJavascriptBridge", MyBridge.class));
        // 载入assets目录下的一个页面
        webView.loadUrl("file:///android_asset/native-api.html");
    }

    public String getToken() {
        return "hello world";
    }

    public void getPictures(JsCallback jsCallback) {
        onceCallback = jsCallback;
        Intent intent = new Intent(this, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_MULTI);
        intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, MAX_PHOTO_NUM);
        startActivityForResult(intent, PICK_PHOTO_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_REQUEST) {
            if (resultCode == RESULT_OK) {
                // 拼接返回Json
                ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                JSONObject dataJson = new JSONObject();
                JSONObject images = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < result.size(); i++) {
                    File file = new File(result.get(i));
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        byte[] dataInByte = steamToByte(fis);
                        String msg = Base64.encodeToString(dataInByte, Base64.DEFAULT);
                        // 通过Base64方式返回的String会包含许多\n，需去除掉
                        msg = msg.replaceAll("\n", "");
                        jsonArray.put(msg);
                        fis.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    images.put("imgs", jsonArray);
                    dataJson.put("data", images);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 回调
                try {
                    if (onceCallback != null) {
                        onceCallback.apply(dataJson.toString());
                    }
                } catch (JsCallback.JsCallbackException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static byte[] steamToByte(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        byte[] b = new byte[1024];
        while ((len = input.read(b, 0, b.length)) != -1) {
            baos.write(b, 0, len);
        }
        byte[] buffer = baos.toByteArray();
        return buffer;
    }
}
