package com.protv.mm.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.protv.mm.R;
import com.protv.mm.models.VideoModel;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.Holder> {

    ArrayList<VideoModel> dataLists=new ArrayList<>();
    Activity c;
    private LayoutInflater mInflater;

    public VideoAdapter(ArrayList<VideoModel> dataLists, Activity c) {
        this.dataLists = dataLists;
        this.c = c;
        this.mInflater = LayoutInflater.from(c);
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=mInflater.inflate(R.layout.item_video,parent,false);
        return new Holder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        VideoModel model=dataLists.get(position);
        holder.tvVideoName.setText(model.getName());

        if(model.getThumbnail()!=null){
            holder.iv_videoThumb.setImageBitmap(model.getThumbnail());
        }else{
            Glide.with(c)
                    .load(model.getUri()) // or URI/path
                    .into(holder.iv_videoThumb);

        }

        //holder.tvDuration.setText(formatDuration(model.getDuration()));
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView tvVideoName,tvDuration;
        ImageView iv_videoThumb;

        public Holder(@NonNull View itemView) {
            super(itemView);
            iv_videoThumb=itemView.findViewById(R.id.image);
            tvVideoName=itemView.findViewById(R.id.title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VideoModel model= dataLists.get(getAbsoluteAdapterPosition());
                    Intent intent = new Intent(Intent.ACTION_VIEW, model.getUri());
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent.setDataAndType(model.getUri(), "video/mp4");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    c.startActivity(intent);
                }
            });



        }
    }

    @Override
    public int getItemCount() {
        return dataLists.size();
    }

    private String formatDuration(int time){

        int sec = time/1000;
        int second = sec % 60;
        int minute = sec / 60;
        if (minute >= 60) {
            int hour = minute / 60;
            minute %= 60;
            return hour + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
        }
        return (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
    }

}
