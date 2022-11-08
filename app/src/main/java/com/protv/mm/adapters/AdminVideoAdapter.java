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
import com.protv.mm.app.MyDialog;
import com.protv.mm.app.MyHttp;
import com.protv.mm.app.Routing;
import com.protv.mm.models.FeedModel;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.ArrayList;

public class AdminVideoAdapter extends RecyclerView.Adapter<AdminVideoAdapter.Holder> {

    private final Activity c;
    private final ArrayList<FeedModel> data;
    private final LayoutInflater mInflater;
    final SharedPreferences sharedPreferences;
    final String currentUserName;
    private String unityGameID = "4574870";
    private Boolean testMode = false;
    private String InterID = "Interstitial_Android";


    public AdminVideoAdapter(Activity c, ArrayList<FeedModel> data){
        this.data=data;
        this.c=c;
        this.mInflater= LayoutInflater.from(c);

        sharedPreferences=c.getSharedPreferences("GeneralData", Context.MODE_PRIVATE);
        currentUserName=sharedPreferences.getString("userName",null);



    }


    @NonNull
    @Override
    public AdminVideoAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_admin_video,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminVideoAdapter.Holder holder, int position) {
        try{
            FeedModel model=data.get(position);
            holder.tv_title.setText(model.getTitle());
            holder.tv_description.setText(model.getDes());
            AppHandler.setPhotoFromRealUrl(holder.iv_thumbnail,model.getThumbnail());

        }catch (Exception ignored){

        }


    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView tv_title,tv_description;
        ImageView iv_thumbnail,iv_more;

        public Holder(View view){
            super(view);
            tv_title=view.findViewById(R.id.tv_title);
            tv_description=view.findViewById(R.id.tv_description);
            iv_thumbnail=view.findViewById(R.id.iv_thumbnail);
            iv_more=view.findViewById(R.id.iv_more);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MyDialog myDialog=new MyDialog(c, "Delete Video!", "Do you really want to delete this video", new MyDialog.ConfirmClick() {
                        @Override
                        public void onConfirmClick() {
                            FeedModel model=data.get(getAbsoluteAdapterPosition());
                            deleteVideo(model.getId());
                            data.remove(getAbsoluteAdapterPosition());
                            notifyDataSetChanged();
                        }
                    });
                    myDialog.showMyDialog();
                    return false;
                }
            });

        }
    }

    private void deleteVideo(String id){
        new Thread(() -> {
            MyHttp myHttp=new MyHttp(MyHttp.RequesMethod.POST,new MyHttp.Response() {
                @Override
                public void onResponse(String response) {}
                @Override
                public void onError(String msg) {
                    Log.e("CategoryErr ",msg);
                }
            }).url(Routing.VIDEO)
                    .field("video_id",id)
                    .field("action","delete");

            myHttp.runTask();
        }).start();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
