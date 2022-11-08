package com.protv.mm.app;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.protv.mm.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class AppHandler {

    public static int downloadThreadNum=0;
    public static String START_AD_ID="210398681";


    public static void setPhotoFromRealUrl(ImageView iv, String url){
        Picasso.get()
                .load(url)
                .centerInside()
                .fit()
                .error(R.drawable.ic_launcher)
                .into(iv, new Callback() {
                    @Override
                    public void onSuccess() {}
                    @Override
                    public void onError(Exception e) {

                    }
                });
    }



    public static  String viewCountFormat(int i){
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if(i==0){
            return "No View";

        }else if(i==1){
            return "1 View";
        }else if(i>=1000&&i<1000000){
            double j=(double) i/1000;

            return  decimalFormat.format(j)+"k Views";
        }else if(i>=1000000){
            return decimalFormat.format((double)i/1000000) +"M Views";
        }else{
            return  i+" Views";
        }
    }

    public static  String commentFormat(int i){
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if(i==0){
            return "No Comment";

        }else if(i==1){
            return "1 Comment";
        }else if(i>=1000&&i<1000000){
            double j=(double) i/1000;

            return  decimalFormat.format(j)+"k Comments";
        }else if(i>=1000000){
            return decimalFormat.format((double)i/1000000) +"M Comments";
        }else{
            return  i+" Comments";
        }
    }


    public static String reactFormat(int a){
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if((double) a >=1000&& (double) a <1000000){
            double j= (double) a /1000;

            return  decimalFormat.format(j)+"k";
        }else if((double) a >=1000000){
            return decimalFormat.format((double) a /1000000 )+"M";
        }else{
            return  a +"";
        }
    }



    public static String formatTime( long time){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        Date resultdate = new Date(time);
        return sdf.format(resultdate);

    }

    public static  byte[] getFileByte(String title, String dir){
        byte [] buffer;

        try {
            InputStream is=new BufferedInputStream(new FileInputStream(dir+"/"+title));
            int size=is.available();
            buffer=new byte[size];
            is.read(buffer);
            is.close();

            return  buffer;

        }catch (Exception e){
            return null;
        }
    }

    public static String [] month={"Jan","Feb","March","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public static boolean isOnline(Activity c) {
        ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(c).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
