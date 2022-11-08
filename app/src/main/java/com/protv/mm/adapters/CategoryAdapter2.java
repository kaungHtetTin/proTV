package com.protv.mm.adapters;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.protv.mm.R;
import com.protv.mm.VideosActivity;
import com.protv.mm.app.MyDialog;
import com.protv.mm.app.MyHttp;
import com.protv.mm.app.Routing;
import com.protv.mm.models.CategoryModel;
import java.util.ArrayList;
import java.util.concurrent.Executor;


public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.Holder> {

    private final Activity c;
    private final ArrayList<CategoryModel> data;
    private final LayoutInflater mInflater;

    Executor postExecutor;

    public CategoryAdapter2(Activity c, ArrayList<CategoryModel> data){
        this.data=data;
        this.c=c;
        this.mInflater= LayoutInflater.from(c);
        postExecutor= ContextCompat.getMainExecutor(c);

    }


    @NonNull
    @Override
    public CategoryAdapter2.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_category2,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter2.Holder holder, int position) {
        try{

            CategoryModel model=data.get(position);
            holder.tv_category.setText(model.getTitle());
            boolean vip=model.isVip();
            if(vip) holder.iv_lock.setVisibility(View.VISIBLE);
            else holder.iv_lock.setVisibility(View.INVISIBLE);

        }catch (Exception ignored){

        }


    }

    public class Holder extends RecyclerView.ViewHolder{

        ImageView iv_more,iv_lock;
        TextView tv_category;

        public Holder(View view){
            super(view);

            tv_category=view.findViewById(R.id.tv_category);
            iv_more=view.findViewById(R.id.iv_more);
            iv_lock=view.findViewById(R.id.iv_lock);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CategoryModel model=data.get(getAbsoluteAdapterPosition());
                    Intent intent=new Intent(c, VideosActivity.class);
                    intent.putExtra("category_id",model.getId());
                    c.startActivity(intent);
                }
            });


            iv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CategoryModel model=data.get(getAbsoluteAdapterPosition());
                    showMenu(view,model.getId(),getAbsoluteAdapterPosition(),Holder.this);
                }
            });
        }
    }


    private void showMenu(View v,String category_id,int position,Holder holder){
        PopupMenu popup=new PopupMenu(c,v);
        popup.getMenuInflater().inflate(R.menu.category_pop_up,popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id=item.getItemId();
            if(id==R.id.vip){
                handleCategory( category_id,"updateVIP","1");
                holder.iv_lock.setVisibility(View.VISIBLE);
            }else if(id==R.id.delete) {
                MyDialog myDialog=new MyDialog(c, "Delete category!", "Do you really want to delete", new MyDialog.ConfirmClick() {
                    @Override
                    public void onConfirmClick() {
                        handleCategory(category_id,"delete","");
                        data.remove(position);
                        notifyDataSetChanged();
                    }
                });
                myDialog.showMyDialog();
            }else if(id==R.id.free){
                handleCategory(category_id,"updateVIP","0");
                holder.iv_lock.setVisibility(View.INVISIBLE);
            }
            return true;
        });
        popup.show();
    }

    private void handleCategory( String category_id,String action,String vip){
        new Thread(() -> {
            MyHttp myHttp=new MyHttp(MyHttp.RequesMethod.POST,new MyHttp.Response() {
                @Override
                public void onResponse(String response) {
                    Log.e("Category ",response );
                    postExecutor.execute(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
                @Override
                public void onError(String msg) {
                    Log.e("CategoryErr ",msg);
                }
            }).url(Routing.CATEGORY)
                    .field("category_id",category_id)
                    .field("vip",vip)
                    .field("action",action);

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
