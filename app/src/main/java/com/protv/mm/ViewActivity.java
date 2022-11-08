package com.protv.mm;

import static com.protv.mm.SplashScreenActivity.isVIP;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.protv.mm.app.AppHandler;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

public class ViewActivity extends AppCompatActivity {

    private String unityGameID = "4802790";
    private Boolean testMode = false;
    private String InterID = "Interstitial_Android";

    String link,title;
    Boolean showAd=true;
    LinearLayout loading_l;
    View loading_v;
    TextView loading_tv;
    VideoView vv;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_view);

        vv=(VideoView) findViewById(R.id.vv);

        loading_l=(LinearLayout)findViewById(R.id.loading_l);
        loading_v=(View)findViewById(R.id.loading_v);
        loading_tv=(TextView) findViewById(R.id.loading_tv);
        link=getIntent().getStringExtra("link");
        title=getIntent().getStringExtra("title");
        showAd=getIntent().getBooleanExtra("showAd",true);
        vv.setMediaController(new MediaController(this));


        vv.setVideoURI(Uri.parse(link));

        start();

        if(!isVIP){
            // NOTE always use test ads during development and testing
            //StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG);
            StartAppSDK.init(this, AppHandler.START_AD_ID, true);
            UnityAds.initialize(this, unityGameID, testMode);
            LoadInterstitialAds();
        }

    }


    public void open(String vlink){
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(vlink)));
        //	showToast("Opening in YouTube");
    }
    void loading(){
        loading_v.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.loading_anim));
    }
    void stop(){
        try{
            loading_v.clearAnimation();
        }catch(Exception e){

        }
        loading_l.setVisibility(View.VISIBLE);
        loading_tv.setText("Video is finshed...\nTap to Replay");
        loading_v.setBackgroundResource(R.drawable.y_replay);


        loading_l.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1)
            {
                start();
                // TODO: Implement this method
            }
        });
    }
    void error(){
        loading_l.setVisibility(View.VISIBLE);
        loading_v.setBackgroundResource(R.drawable.y_retry);
        loading_tv.setText("We found Error while playing video!\nTap to Try again");

        try{
            loading_v.clearAnimation();
        }catch(Exception e){

        }
        loading_l.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1)
            {
                start();
                // TODO: Implement this method
            }
        });
    }
    void start(){
        loading_l.setVisibility(View.VISIBLE);
        loading_v.setBackgroundResource(R.drawable.y_loading);
        loading_tv.setText("Video is Loading...");
        loading();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // mp.setLooping(true);
                loading_l.setVisibility(View.GONE);
                if(!isVIP)StartAppAd.showAd(ViewActivity.this);

            }
        });
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // mp.setLooping(true);
                stop();
                if(!isVIP)StartAppAd.showAd(ViewActivity.this);
            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener(){

            @Override
            public boolean onError(MediaPlayer p1, int p2, int p3)
            {
                error();
                // TODO: Implement this method
                return false;
            }
        });

        vv.start();
    }

    // to show intestitial ads
    public void DisplayInterstitialAd() {
        if (UnityAds.isReady(InterID)) {
            UnityAds.show(this, InterID);
        }else{
            finish();
            Toast.makeText(getApplicationContext(),"ads not loaded",Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadInterstitialAds() {

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
                Toast.makeText(getApplicationContext()," ads complete",Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
                // Implement functionality for a Unity Ads service error occurring.
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        UnityAds.setListener(InterListener);
        UnityAds.load(InterID);

    }
    @Override
    public void onBackPressed()
    {
        if(!isVIP)DisplayInterstitialAd();
        else super.onBackPressed();
    }
}