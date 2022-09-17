package com.rothfarb.milon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    SwipeRefreshLayout swipeRefresher;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        swipeRefresher = findViewById(R.id.swiperefresh);

        //settings of the webview
        WebSettings settings = webView.getSettings();

        //enable JavaScript
        settings.setJavaScriptEnabled(true);

        //disable safe browsing for enabling http referer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(false);
        }

        //enable zoom in/out
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        //disable settings for security reasons
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setSaveFormData(false);

        webView.setWebViewClient(new WebViewClient() {

            //overring loading url method
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                String domain = "https://milon.madrasafree.com";

                Uri uri = Uri.parse(url);
                Set<String> paramNames = uri.getQueryParameterNames();

                if (url.startsWith(domain)) {
                    //loading urls from the domain in the webview
                    if(paramNames.size() == 0){
                        url += "?app=android";
                    }else{
                        url += "&app=android";
                    }
                    view.loadUrl(url);
                } else {
                    //loading urls outside the domain in the default browser of the device
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {

                // Inject CSS when page is done loading
                injectCSS();
                super.onPageFinished(view, url);
            }
        });

        //initialize the webview
        webView.loadUrl("https://milon.madrasafree.com?app=android");
        swipeRefresher.setOnRefreshListener(
                () -> {
                    webView.reload();
                    swipeRefresher.setRefreshing(false);
                }
        );

    }

    //overiding back button pressing
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        swipeRefresher.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                () -> {
                    if (webView.getScrollY() == 0)
                        swipeRefresher.setEnabled(true);
                    else
                        swipeRefresher.setEnabled(false);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        swipeRefresher.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
    }

    private void injectCSS() {
        try {
            InputStream inputStream = getAssets().open("style.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}