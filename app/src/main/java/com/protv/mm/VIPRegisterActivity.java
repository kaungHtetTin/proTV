package com.protv.mm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.protv.mm.app.JSONGetter;
import com.protv.mm.app.Routing;
import com.protv.mm.interfaces.WebAppInterface;

import org.json.JSONObject;

import java.util.concurrent.Executor;

public class VIPRegisterActivity extends AppCompatActivity {



    WebView wv;
    SwipeRefreshLayout swipe;
    Executor postExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipregister);
        postExecutor= ContextCompat.getMainExecutor(this);

        setUpView();
    }


    private void setUpView(){
        wv = findViewById(R.id.detailWeb);
        swipe=findViewById(R.id.swipe);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });

        setUpWebView();
    }

    private void setUpWebView(){
        wv.setWebViewClient(new WebViewClient());
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath());
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        wv.addJavascriptInterface(new WebAppInterface(this), "Android");
        wv.loadUrl(Routing.VIP_REGISTER);

        swipe.setRefreshing(true);

        wv.setWebViewClient(new WebViewClient(){

            public boolean shouldOverrideUrlLoading(WebView view,String url){


                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }
            public void onLoadResource(WebView view, String url){

            }

            public void onPageFinished(WebView view,String url){
                try{

                    swipe.setRefreshing(false);

                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }

        });
    }


}