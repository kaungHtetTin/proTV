package com.protv.mm;

import static com.protv.mm.SplashScreenActivity.isVIP;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.protv.mm.adapters.FeedAdapter;
import com.protv.mm.app.AppHandler;
import com.protv.mm.app.JSONGetter;
import com.protv.mm.app.MediaFireFetcher;
import com.protv.mm.fragments.FragmentOne;
import com.protv.mm.services.DownloadService;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoDetailActivity extends AppCompatActivity implements MaxRewardedAdListener {


    private SwipeRefreshLayout mSwipeLayout;
    RecyclerView recyclerViewFeed;
    FeedAdapter adapter;

    Toolbar tb;
    String title,des,link,thumnail,id;
    CollapsingToolbarLayout ctl;
    TextView bname,wname,support_tv;
    ImageView title_bg_img,title_img;
    LinearLayout download_l,read_l;
    String download_link = "";
    Executor postExecutor;

    private MaxRewardedAd rewardedAd;
    private int           retryAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_detail);
        thumnail=getIntent().getExtras().get("thumbnail").toString();
        title=getIntent().getExtras().get("title").toString();
        des=getIntent().getExtras().get("des").toString();
        link=getIntent().getExtras().get("link").toString();
        id=getIntent().getExtras().get("id").toString();


        postExecutor= ContextCompat.getMainExecutor(this);
        setUpView();

        if(!isVIP){
            // Make sure to set the mediation provider value to "max" to ensure proper functionality
            AppLovinSdk.getInstance( this).setMediationProvider( "max" );
            AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
                @Override
                public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
                {
                    // AppLovin SDK is initialized, start loading ads
                }
            } );
            createRewardedAd();
        }
    }

    void createRewardedAd()
    {
        rewardedAd = MaxRewardedAd.getInstance( "1776e721007e965e", this );
        rewardedAd.setListener( this );
        rewardedAd.loadAd();
    }

    private void setUpView(){
        recyclerViewFeed=findViewById(R.id.recyclerview);
        tb=(Toolbar)findViewById(R.id.nnl_toolbar);
        ctl=(CollapsingToolbarLayout)findViewById(R.id.nnl_ctl);
        tb.collapseActionView();
        ctl.setTitleEnabled(false);
        download_l=(LinearLayout)findViewById(R.id.download_l);
        read_l=(LinearLayout)findViewById(R.id.read_l);
        support_tv=(TextView) findViewById(R.id.support_tv);
        bname=(TextView)findViewById(R.id.bname);
        wname=(TextView)findViewById(R.id.wname);
        title_bg_img=(ImageView)findViewById(R.id.title_bg_img);
        title_img=(ImageView)findViewById(R.id.title_img);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);


        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        support_tv.setText(des);
        support_tv.setSelected(true);
        getSupportActionBar().setTitle(getString(R.string.title));
        getSupportActionBar().setSubtitle(title);

        bname.setText(title);
        wname.setText(getString(R.string.app_name));

        AppHandler.setPhotoFromRealUrl(title_bg_img, thumnail);
        AppHandler.setPhotoFromRealUrl(title_img, thumnail);

        GridLayoutManager gm=new GridLayoutManager(this,2);
        recyclerViewFeed.setLayoutManager(gm);
        adapter=new FeedAdapter(this, FragmentOne.feeds,true);
        recyclerViewFeed.setAdapter(adapter);


        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(false);
            }
        });

        mSwipeLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2);


        download_l.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View p1) {
                    if(AppHandler.isOnline(VideoDetailActivity.this)){
                        if(isPermissionGranted()){
                            if(DownloadService.downloadThreadNum<3)fetchDownLoadUrl(link,false);
                            else   Toast.makeText(getApplicationContext(),"Please wait for current downloads",Toast.LENGTH_SHORT).show();
                        }else{
                            takePermission();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Please check your internet connection!",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        read_l.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View p1) {

                    if(AppHandler.isOnline(VideoDetailActivity.this)){
                        fetchDownLoadUrl(link,true);
                    }else{
                        Toast.makeText(getApplicationContext(),"Please check your internet connection!",Toast.LENGTH_SHORT).show();
                    }
                }

        });

    }

    private void fetchDownLoadUrl(String link,boolean play){

        if(!play){
            String message="Do you want to download \""+title+"\"?";
            //String message= Html.fromHtml(resultJson).toString();
            alertToDownload(message);
            return;
        }

        mSwipeLayout.setRefreshing(true);
        new MediaFireFetcher(link, new MediaFireFetcher.Response() {
            @Override
            public void onResponse(String response) {
                postExecutor.execute(new Runnable() {
                    @Override
                    public void run() {

                        mSwipeLayout.setRefreshing(false);

                        Log.e("MediaFire Response ",response);

                        download_link=response;
                        if(response!=null){
                            if(play){

                                Intent i=new Intent(VideoDetailActivity.this,ViewActivity.class);
                                i.putExtra("title",title);
                                i.putExtra("link",download_link);
                                startActivity(i);

                            }else{
                                String message="Do you want to download \""+title+"\"?"+"\n\n MediaFire link : "+link;
                                //String message= Html.fromHtml(resultJson).toString();
                                alertToDownload(message);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Fetching Err ",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            @Override
            public void onError(String msg) {
                Log.e("Err ",msg);
            }
        }).start();
    }


    private boolean isPermissionGranted(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return  true;
        }else{
            int  writeExternalStorage= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return  writeExternalStorage== PackageManager.PERMISSION_GRANTED;
        }

    }

    private void takePermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            if(requestCode==101){
                boolean writeExternalStorage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(writeExternalStorage){

                }else {
                    takePermission();
                }
            }
        }
    }




    void alertToDownload(String message){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View parent_view = LayoutInflater.from(this).inflate(R.layout.dialog_download, null);
        dialog.setView(parent_view);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        ImageView iv=(ImageView) dialog.findViewById(R.id.icon);
        TextView tv_title = (TextView) dialog.findViewById(R.id.title);
        TextView tv_message = (TextView) dialog.findViewById(R.id.message);
        final Button d = (Button) dialog.findViewById(R.id.bt2);
        final Button p= (Button) dialog.findViewById(R.id.bt1);

        iv.setImageResource(R.drawable.ic_download);

        tv_title.setText("Download Movies");
        tv_message.setText(message);


        d.setText("Download");
        d.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                dialog.dismiss();
                if(isVIP){
                    downloadVideo();
                }else{
                    if( rewardedAd.isReady() ){
                        rewardedAd.showAd();
                    }else{
                        downloadVideo();
                    }
                }

            }
        });
        p.setText("Cancel");
        p.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {

                dialog.dismiss();
            }
        });
    }


    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd)
    {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error)
    {
        // Rewarded ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                rewardedAd.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error)
    {
        // Rewarded ad failed to display. We recommend loading the next ad
        rewardedAd.loadAd();
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {}

    @Override
    public void onAdClicked(final MaxAd maxAd) {

    }

    @Override
    public void onAdHidden(final MaxAd maxAd)
    {
        // rewarded ad is hidden. Pre-load the next ad
        rewardedAd.loadAd();
    }

    @Override
    public void onRewardedVideoStarted(final MaxAd maxAd) {}

    @Override
    public void onRewardedVideoCompleted(final MaxAd maxAd) {}

    @Override
    public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward)
    {
        // Rewarded ad was displayed and user should receive the reward
        downloadVideo();

    }


    private void downloadVideo(){
//        Intent intent=new Intent(VideoDetailActivity.this, DownloadService.class);
//        String checkTitle=title.replace("/"," ");
//        intent.putExtra("filename",checkTitle+".mp4");
//        intent.putExtra("intentMessage","downloadVideo");
//        intent.putExtra("downloadUrl",download_link);
//        Toast.makeText(getApplicationContext(),"Start Downloading",Toast.LENGTH_SHORT).show();
//        startService(intent);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }
}