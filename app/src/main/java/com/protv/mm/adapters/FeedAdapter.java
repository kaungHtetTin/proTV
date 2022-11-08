package com.protv.mm.adapters;


import static com.protv.mm.SplashScreenActivity.isVIP;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.protv.mm.R;
import com.protv.mm.VIPRegisterActivity;
import com.protv.mm.VideoDetailActivity;
import com.protv.mm.app.AppHandler;
import com.protv.mm.models.FeedModel;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.Holder> {

    private final Activity c;
    private final ArrayList<FeedModel> data;
    private final LayoutInflater mInflater;
    final SharedPreferences sharedPreferences;
    final String currentUserName;
    private String unityGameID = "4802790";
    private Boolean testMode = false;
    private String InterID = "Interstitial_Android";
    boolean videoDetailActivity;


    public FeedAdapter(Activity c, ArrayList<FeedModel> data,boolean videoDetailActivity){
        this.data=data;
        this.c=c;
        this.mInflater= LayoutInflater.from(c);
        this.videoDetailActivity=videoDetailActivity;
        sharedPreferences=c.getSharedPreferences("GeneralData", Context.MODE_PRIVATE);
        currentUserName=sharedPreferences.getString("userName",null);

        // Initialize the SDK:
        if(!isVIP)UnityAds.initialize(c, unityGameID, testMode);

    }


    @NonNull
    @Override
    public FeedAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_feed,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.Holder holder, int position) {
        try{
            FeedModel model= data.get(position);
            AppHandler.setPhotoFromRealUrl(holder.iv,model.getThumbnail());
            holder.bname.setText(model.getTitle());
            boolean vip=model.isVip();
            if(vip){
                if(isVIP) holder.iv_lock.setVisibility(View.GONE);
                else holder.iv_lock.setVisibility(View.VISIBLE);
            }else{
                holder.iv_lock.setVisibility(View.GONE);
            }

        }catch (Exception ignored){

        }


    }

    public class Holder extends RecyclerView.ViewHolder{

        CardView card;
        TextView bname;
        ImageView iv,iv_lock;

        public Holder(View view){
            super(view);

            iv=view.findViewById(R.id.image);
            bname=view.findViewById(R.id.title);
            card=view.findViewById(R.id.card);
            iv_lock=view.findViewById(R.id.iv_lock);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(!isVIP){ //vip member
                        if(data.get(getAbsoluteAdapterPosition()).isVip()){ // vip video

                            vipDialog();
                        }else{
                            LoadInterstitialAds(getAbsoluteAdapterPosition());
                        }
                    }
                    else {
                        goTo(getAbsoluteAdapterPosition());
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    private void LoadInterstitialAds(int position) {
        Log.e("StartAds ","start");
        IUnityAdsListener InterListener = new IUnityAdsListener() {

            public void onUnityAdsReady(String adUnitId) {
                // Implement functionality for an ad being ready to show.
                Toast.makeText(c," ads loaded",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onUnityAdsStart(String adUnitId) {
                // Implement functionality for a user starting to watch an ad.
            }

            @Override
            public void onUnityAdsFinish(String adUnitId, UnityAds.FinishState finishState) {
                // Implement conditional logic for each ad completion status:
                goTo(position);
                Toast.makeText(c," ads complete",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
                // Implement functionality for a Unity Ads service error occurring.
                Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
                goTo(position);
            }
        };

        UnityAds.setListener(InterListener);
        UnityAds.load(InterID);
        DisplayInterstitialAd(position);
    }

    public void DisplayInterstitialAd(int position) {
        if (UnityAds.isReady(InterID)) {
            UnityAds.show(c, InterID);
        } else {
            goTo(position);
            Toast.makeText(c, "ads not loaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void goTo( int position){
        FeedModel model=data.get(position);
        Intent i=new Intent(c, VideoDetailActivity.class);
        i.putExtra("thumbnail",model.getThumbnail());
        i.putExtra("title",model.getTitle());
        i.putExtra("des",model.getDes());
        i.putExtra("link",model.getD_url());
        i.putExtra("id","123");

        c.startActivity(i);
        if(videoDetailActivity)c.finish();
    }

    private void vipDialog(){
        final AlertDialog dialog = new AlertDialog.Builder(c).create();
        View parent_view = LayoutInflater.from(c).inflate(R.layout.dialog_two_buttons, null);
        dialog.setView(parent_view);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        ImageView iv=(ImageView) dialog.findViewById(R.id.icon);
        TextView tv_title = (TextView) dialog.findViewById(R.id.title);
        TextView tv_message = (TextView) dialog.findViewById(R.id.message);
        Button call = (Button) dialog.findViewById(R.id.bt2);
        Button cancel= (Button) dialog.findViewById(R.id.bt1);
        iv.setImageResource(R.drawable.ic_update);
        tv_title.setText("VIP ဝယ်မယ်");
        tv_message.setText("ဗီဒီယိုကြည့်ရှုန်ရန် VIP ဝယ်ဖို့လိုအပ်ပါသည်");
        cancel.setText("မဝယ်ဘူး");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        call.setText("ဝယ်မယ်");
        call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                c.startActivity(new Intent(c,VIPRegisterActivity.class));
            }}
        );
    }

}
