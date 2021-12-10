package com.rothfarb.milon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    String version = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);


        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //settings of the webview
        WebSettings settings = webView.getSettings();

        //enable JavaScript
        settings.setJavaScriptEnabled(true);

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


        webView.setWebViewClient(new WebViewClient(){

            //overring loading url method
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                String domain = "https://rothfarb.info/ronen/arabic";

                if (url.startsWith(domain)){
                    //loading urls from the domain in the webview
                    view.loadUrl(url);
                    return true;
                } else{
                    //loading urls outside the domain in the default browser of the device
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
            }

        });

        //initialize the webview
        webView.loadUrl("https://rothfarb.info/ronen/arabic/");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miAbout:
                showInfo();
                return false;
            case R.id.miContact:
                String text = "גרסה: " + version + "(אנא השאירו שורה זו כדי שנדע באיזו גרסה אתם משתמשים) ";
                sendEmail("arabic4hebs@gmail.com", getString(R.string.email_subject), text);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //open the email app and define the email address and title
    public void sendEmail(String email, String title, String text){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Log.d("email_error", e.getMessage());
        }
    }
    //open dialog message with app info
    public void showInfo(){
        String version = "";
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AlertDialog alert = new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.logo).create();
        alert.setTitle("מילון ערבית מדוברת גרסה " + version);
        alert.setMessage("אפליקציה לאנדרואיד של מילון ערבית מדוברת לדוברי עברית");
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "סבבה", (dialog, which) -> dialog.dismiss());
        alert.show();
    }
}