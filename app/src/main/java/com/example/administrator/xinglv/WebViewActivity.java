package com.example.administrator.xinglv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.example.administrator.xinglv.tools.IntentTools;
import com.example.administrator.xinglv.tools.Tools;

import java.util.Date;

public class WebViewActivity extends AppCompatActivity {
   // private String url = "http://172.16.4.208:8080/company/login.html";
   private String url = "http://10.0.0.8:8080/company/login.html";
    //private String url = "http://10.0.0.8:8080/company/blue.html?userId=3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
//
//        TitleView titleView = (TitleView) findViewById(R.id.webView_title);
//
//        if (getIntent().getStringExtra("title") != null) {
//            titleView.setTitle(getIntent().getStringExtra("title"));
//            titleView.setVisibility(View.VISIBLE);
//        }
        WebView webView = (WebView) findViewById(R.id.webView);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webView.setInitialScale(1);
        webView.setWebChromeClient(new WebChromeClient());
        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置字符编码
        settings.setDefaultTextEncodingName("utf-8");
        settings.setSupportZoom(true);
        //启用内置缩放装置
        settings.setLoadsImagesAutomatically(true);
        settings.setBuiltInZoomControls(false); // 显示放大缩小 controler

       settings.setLoadWithOverviewMode(true); //屏幕适配
        settings.setUseWideViewPort(true);

        settings.setUseWideViewPort(true);  //为图片添加放大缩小功能

        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        //添加调用接口
      webView.addJavascriptInterface(new DemoJavaScriptInterface(), "android");
        //webView.loadUrl("http://www.baidu.com");
        if (getIntent().getStringExtra("Url")!=null){
            webView.loadUrl(getIntent().getStringExtra("Url"));
        }else{
            webView.loadUrl(url);
        }
        //webView.loadUrl("http://192.168.10.13/playCake.html");
    }

    final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {

        }

        /**
         * @JavascriptInterface必须有
         */
        @JavascriptInterface
        public String clickOnAndroid() {
            Log.i("webview", "=============");
            // IntentTools.toLoginActivity(WebViewActivity.this);
            //IntentTools.toWebViewActivity(WebViewActivity.this, "http://192.168.10.11/Content/diandian/playCake.html.html?ran=" + new Date().getTime() + "&shopkey=" );
            return "http://document.tigether.com/document/01-App/3/image/android201601231608272783-2.jpg";
        }

        @JavascriptInterface
        public void thisFinish() {
            WebViewActivity.this.finish();
        }
//
//        @JavascriptInterface
//        public void toShopCar() {
//            IntentTools.toFrametActivity(WebViewActivity.this, 1);
//        }

        @JavascriptInterface
        public void toMainActivity(String id) {
            Intent i = new Intent(WebViewActivity.this, MainActivity.class);
            i.putExtra("UserId", id);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

//        @JavascriptInterface
//        public String getUserKey() {
//            if (MyApplication.user == null) {
//                IntentTools.toLoginActivity(WebViewActivity.this);
//                return "";
//            } else {
//                return MyApplication.user.getKey();
//            }
//        }

    }

}
