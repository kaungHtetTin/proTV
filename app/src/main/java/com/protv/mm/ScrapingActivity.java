package com.protv.mm;

import static com.protv.mm.SplashScreenActivity.isVIP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.protv.mm.services.DownloadService;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class ScrapingActivity extends AppCompatActivity {

    WebView wv;
    FloatingActionButton fab;
    SwipeRefreshLayout swipe;

    private boolean isRedirected;
    String Current_url,address,check;

    private String unityGameID = "4802790";
    private Boolean testMode = false;
    private String InterID = "Interstitial_Android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scraping);

        Current_url= Objects.requireNonNull(getIntent().getExtras()).getString("link");
      //  getSupportActionBar().hide();
        check=Current_url;

        swipe=findViewById(R.id.swipe);
        wv=findViewById(R.id.wv);
        fab=findViewById(R.id.fab_download);
        fab.setVisibility(View.GONE);

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

       // new WebScraperTask().execute(wv.getUrl());
        startWebView(wv,Current_url);

        if(!isVIP) UnityAds.initialize(this, unityGameID, testMode);

    }


    private void startWebView(WebView wv, String url){
        wv.setWebViewClient(new WebViewClient(){

            public boolean shouldOverrideUrlLoading(WebView view,String url){
                Current_url=url;
                view.loadUrl(url);
                isRedirected=true;

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isRedirected=false;
                new WebScraperTask().execute(url);
            }
            public void onLoadResource(WebView view, String url){
                if(!isRedirected){
                    swipe.setRefreshing(true);
                   // new WebScraperTask().execute(wv.getUrl());
                }
            }

            public void onPageFinished(WebView view,String url){
                try{
                    isRedirected=true;
                    swipe.setRefreshing(false);

                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        wv.loadUrl(url);
        swipe.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {

        fab.setVisibility(View.GONE);
        if(wv.canGoBack() && !check.equals(address)){
            wv.goBack();

        }else {
            super.onBackPressed();
        }

    }


    public class WebScraperTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.e("Start ","Start scraping");
            try {
                Document doc= Jsoup.connect(strings[0]).get();
                return  process(doc);
            }catch (IOException e){
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null){
                fab.setVisibility(View.VISIBLE);
                Log.e("resutlDownloadUrl ",s);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       if(DownloadService.downloadThreadNum<3){
                           LoadInterstitialAds();
                           Intent intent=new Intent(ScrapingActivity.this, DownloadService.class);
                           intent.putExtra("filename",generateName()+".mp4");
                           intent.putExtra("intentMessage","downloadVideo");
                           intent.putExtra("downloadUrl",s);
                           Toast.makeText(getApplicationContext(),"Start Downloading ",Toast.LENGTH_SHORT).show();
                           startService(intent);
                       }else{
                           Toast.makeText(getApplicationContext(),"Please wait for current downloads",Toast.LENGTH_SHORT).show();
                       }
                    }
                });


            }else{
                fab.setVisibility(View.GONE);
            }

        }
    }


    public String process(Document doc){

        String result=null;

        Element videoDiv=doc.getElementById("video-player-bg");
        if(videoDiv!=null){
            Elements script= videoDiv.select("script");
            String[] arrOfStr = doc.select("body").toString().split("html5player.");
            for (int i=1;i<arrOfStr.length;i++){
                if(arrOfStr[i].startsWith("setVideoUrlLow")){
                    Log.e("url Low ",getLink(arrOfStr[i]));
                }
                if(arrOfStr[i].startsWith("setVideoUrlHigh")){
                    Log.e("url High ",getLink(arrOfStr[i]));
                    result=getLink(arrOfStr[i]);
                }
            }

        }
        return result;
    }

    public String getLink(String s){
        s=s.substring(s.indexOf("'")+1,s.lastIndexOf("'"));
        return s;
    }

    private String generateName(){
        Character charArr1[]={'A','C','E','G','I','K','M','O','P','R'};
        Character charArr2[]={'B','D','F','H','J','L','N','P','Q','S'};

        String result="";
        long timestamp=System.currentTimeMillis()/1000;
        String timeStr=timestamp+"";
        for(int i=0;i<timeStr.length();i++){
            int index=Integer.parseInt(timeStr.charAt(i)+"");
            if(i>3){
                if(timestamp%2==0){
                    result+=charArr1[index];
                }else{
                    result+=charArr2[index];
                }
            }

        }

        return result;
    }

    private void LoadInterstitialAds() {
        Log.e("StartAds ","start");
        IUnityAdsListener InterListener = new IUnityAdsListener() {

            public void onUnityAdsReady(String adUnitId) {
                // Implement functionality for an ad being ready to show.
                Toast.makeText(getApplicationContext()," ads loaded",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onUnityAdsStart(String adUnitId) {
                // Implement functionality for a user starting to watch an ad.
            }

            @Override
            public void onUnityAdsFinish(String adUnitId, UnityAds.FinishState finishState) {
                // Implement conditional logic for each ad completion status:

            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
                // Implement functionality for a Unity Ads service error occurring.

            }
        };

        UnityAds.setListener(InterListener);
        UnityAds.load(InterID);
        DisplayInterstitialAd();
    }

    public void DisplayInterstitialAd() {
        if (UnityAds.isReady(InterID)) {
            UnityAds.show(this, InterID);
        }
    }

}