package com.manuzid.systeminfowidget.preferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.manuzid.systeminfowidget.GeneralUtils;
import com.manuzid.systeminfowidget.R;

/**
 * Created by Emanuel Zienecker on 05.11.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
@SuppressLint("SetJavaScriptEnabled")
public class PrivacyPolicyActivity extends Activity {
    private static final String DESTINATION_URL = "http://systeminfowidget.manuzid.de/privacy.html";

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_abstract_web_view);
        webView = findViewById(R.id.systemInfoWebView);

        if (!GeneralUtils.isInternetEnabled((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PrivacyPolicyActivity.this);
            builder.setTitle(R.string.no_internet_title);
            builder.setMessage(R.string.no_internet_message);
            builder.setPositiveButton("OK", null);
            builder.show();
        }

        webView.loadUrl(DESTINATION_URL);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }
}
