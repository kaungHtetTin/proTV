package com.protv.mm.adapters;

import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.protv.mm.R;
import com.protv.mm.SplashScreenActivity;
import com.protv.mm.VIPRegisterActivity;
import com.protv.mm.models.MenuModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.Holder> {

    private final Activity c;
    private final ArrayList<MenuModel> data;
    private final LayoutInflater mInflater;


    public MenuAdapter(Activity c, ArrayList<MenuModel> data){
        this.data=data;
        this.c=c;
        this.mInflater= LayoutInflater.from(c);


    }


    @NonNull
    @Override
    public MenuAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_menu,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.Holder holder, int position) {
        try{
            MenuModel model=data.get(position);
            holder.tv.setText(model.getTitle());
            holder.iv.setImageResource(model.getSrc());

        }catch (Exception ignored){

        }


    }

    public class Holder extends RecyclerView.ViewHolder{


        TextView tv;
        ImageView iv;

        public Holder(View view){
            super(view);

            tv=view.findViewById(R.id.tv_menu);
            iv=view.findViewById(R.id.iv_menu);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    switch (getAbsoluteAdapterPosition()){
                        case 0:

                            Intent intent=new Intent(c, VIPRegisterActivity.class);
                            c.startActivity(intent);
                            break;
                        case 1:
                            if(SplashScreenActivity.updateAVAILABLE){
                                updateDialog();
                            }else{
                                Toast.makeText(c,"No Update Available",Toast.LENGTH_SHORT).show();
                            }
                            break;

                        case 2:
                            AboutDia();
                            break;
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


    private void AboutDia(){

        final AlertDialog dialog = new AlertDialog.Builder(c).create();
        View parent_view = LayoutInflater.from(c).inflate(R.layout.dialog_two_buttons, null);
        dialog.setView(parent_view);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        ImageView iv=(ImageView) dialog.findViewById(R.id.icon);
        TextView tv_title = (TextView) dialog.findViewById(R.id.title);
        TextView tv_message = (TextView) dialog.findViewById(R.id.message);
        Button call = (Button) dialog.findViewById(R.id.bt2);
        Button cancel= (Button) dialog.findViewById(R.id.bt1);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        iv.setImageResource(R.drawable.about_icon);
        tv_title.setText("About");
        tv_message.setText(getDataText("about.txt"));

    }


    public String getDataText(String DataName){

        String readResult="";

        try
        {
            InputStream readText=getApplicationContext().getAssets().open(DataName);
            int size=readText.available();
            byte[] b=new byte[size];
            readText.read(b);
            readText.close();

            readResult=new String(b);
        }
        catch (IOException e)
        {

        }
        return readResult;
    }

    private void updateDialog(){
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
        tv_title.setText("Version Update");
        tv_message.setText("New version update is available");
        cancel.setText("GET ON NOT Playstore");

        if (SplashScreenActivity.updateFORCE){
            dialog.setCancelable(false);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SplashScreenActivity.updateLINK));
                c.startActivity(browserIntent);
            }
        });


        call.setText("GET ON PLAYSTORE");
        call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+c.getPackageName()));
                c.startActivity(browserIntent);

            }}
        );
    }

}
