package com.protv.mm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.protv.mm.app.AppHandler;
import com.protv.mm.app.JSONGetter;
import com.protv.mm.app.MyHttp;
import com.protv.mm.app.Routing;
import com.protv.mm.models.CategoryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView logo;
    public static String categories=null;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //vip registration
    boolean used;
    public static String userId;
    public static boolean isVIP=false;
    public static long EXPIRE_DATE=0;

    //version update
    public static boolean updateAVAILABLE=false;
    public static String updateLINK=null;
    public static boolean updateFORCE=false;

    FirebaseDatabase firebaseDatabase;
    private DatabaseReference db;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences=getSharedPreferences("GeneralData", Context.MODE_PRIVATE);
        used=sharedPreferences.getBoolean("used",false);
        editor=sharedPreferences.edit();

        firebaseDatabase=FirebaseDatabase.getInstance();
        db= firebaseDatabase.getReference();

        logo=(ImageView)findViewById(R.id.logo);
        logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.logo_bg));
        checkUpdate();

        if(used){
            userId=sharedPreferences.getString("userId",null);
            checkVIP();
        }else{
            userId=generateID();
            editor.putBoolean("used",true);
            editor.putString("userId",userId);
            editor.apply();
        }


        if(usingVPN()){
            Thread timer = new Thread() {
                @Override
                public void run() {
                    try{
                        sleep(2000);
                        fetchCategory();
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            };
            timer.start();

        }else{
            vpnForcingDialog();
        }

        if(!AppHandler.isOnline(this)) Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();

    }

    private void checkVIP(){
        db.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // user exists in the database
                    Log.e("userExist ","true");

                    EXPIRE_DATE =(long)dataSnapshot.child("expireDate").getValue();
                    if(EXPIRE_DATE>System.currentTimeMillis()){
                        isVIP=true;
                    }else{
                        isVIP=false;
                        db.child("Users").child(userId).removeValue();
                    }

                }else{
                    // user does not exist in the database
                    Log.e("userExist ","false");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkUpdate(){
        db.child("versions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    //versionUpdate
                    long versionLatest =(long)dataSnapshot.child("latest").getValue();
                    updateLINK=(String)dataSnapshot.child("linkUrl").getValue();
                    updateFORCE=(boolean) dataSnapshot.child("force").getValue();
                    if(getCurrentVersion()<versionLatest){
                         updateAVAILABLE=true;
                    }

                    Log.e("Current ",getCurrentVersion()+"");
                    Log.e("latest ",versionLatest+"");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fetchCategory(){

        new Thread(() -> {
            MyHttp myHttp=new MyHttp(MyHttp.RequesMethod.GET, new MyHttp.Response() {
                @Override
                public void onResponse(String response) {
                    Log.e("Category ",response );
                    categories=response;
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();

                }
                @Override
                public void onError(String msg) {
                    Log.e("CategoryErr ",msg);
                    fetchCategory();

                }
            }).url(Routing.CATEGORY);

            myHttp.runTask();
        }).start();
    }


    private String generateID(){
        Character charArr1[]={'A','C','E','G','I','K','M','O','P','R'};
        Character charArr2[]={'B','D','F','H','J','L','N','P','Q','S'};

        String result="";
        long timestamp=System.currentTimeMillis()/1000;
        String timeStr=timestamp+"";
        for(int i=0;i<timeStr.length();i++){
            int index=Integer.parseInt(timeStr.charAt(i)+"");
            if(i<3){
                if(timestamp%2==0){
                    result+=charArr1[index];
                }else{
                    result+=charArr2[index];
                }
            }else{
                result+=timeStr.charAt(i);
            }

        }

        return result;
    }

    private int getCurrentVersion(){
        PackageManager manager =getPackageManager();
        PackageInfo info;
        int currentVersion = 0;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            currentVersion = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersion;
    }

    private boolean usingVPN(){
        Log.e("VPN ","Start checking");
        boolean vpnInUse;
        ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(this).getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activeNetwork = cm.getActiveNetwork();
            NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
            vpnInUse= caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        }else{
            vpnInUse=false;
        }


        return vpnInUse;
    }


    private void vpnForcingDialog(){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View parent_view = LayoutInflater.from(this).inflate(R.layout.dialog_two_buttons, null);
        dialog.setView(parent_view);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        ImageView iv=(ImageView) dialog.findViewById(R.id.icon);
        TextView tv_title = (TextView) dialog.findViewById(R.id.title);
        TextView tv_message = (TextView) dialog.findViewById(R.id.message);
        Button call = (Button) dialog.findViewById(R.id.bt2);
        Button cancel= (Button) dialog.findViewById(R.id.bt1);
        iv.setImageResource(R.drawable.ic_update);
        tv_title.setText("VPN အသုံးပြုပါ");
        tv_message.setText("လူကြီးမင်း၏ အင်တာနက် connection သည်  VPN အသုံးပြုရန်လိုအပ်ပါသည်။ ကျေးဇူးပြု၍ VPN သုံးပြုပြီး ProTV သို့ပြန်လည် ဝင်ရောက်ပါ");

        cancel.setVisibility(View.GONE);

        dialog.setCancelable(false);

        call.setText("OK");
        call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                finish();

            }}
        );
    }

}