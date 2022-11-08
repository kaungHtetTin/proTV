package com.protv.mm.adapters;

import static com.protv.mm.SplashScreenActivity.isVIP;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.protv.mm.R;
import com.protv.mm.interfaces.OnCategoryItemClickListener;
import com.protv.mm.models.CategoryModel;


import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    private final Activity c;
    private final ArrayList<CategoryModel> data;
    private final LayoutInflater mInflater;
    OnCategoryItemClickListener mListener;

    public CategoryAdapter(Activity c, ArrayList<CategoryModel> data,OnCategoryItemClickListener mListener){
        this.data=data;
        this.c=c;
        this.mInflater= LayoutInflater.from(c);
        this.mListener=mListener;

    }


    @NonNull
    @Override
    public CategoryAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_category,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.Holder holder, int position) {
        try{

            CategoryModel model=data.get(position);
            holder.tv.setText(model.getTitle());
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

        TextView tv;
        CardView card;
        ImageView iv_lock;

        public Holder(View view){
            super(view);

            tv=view.findViewById(R.id.text);
            card=view.findViewById(R.id.card);
            iv_lock=view.findViewById(R.id.iv_lock);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(getAbsoluteAdapterPosition());
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



}
