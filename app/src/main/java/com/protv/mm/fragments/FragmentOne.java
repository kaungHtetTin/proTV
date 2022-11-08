package com.protv.mm.fragments;


import static com.protv.mm.SplashScreenActivity.isVIP;
import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.protv.mm.R;
import com.protv.mm.ScrapingActivity;
import com.protv.mm.SplashScreenActivity;
import com.protv.mm.VIPRegisterActivity;
import com.protv.mm.adapters.CategoryAdapter;
import com.protv.mm.adapters.FeedAdapter;
import com.protv.mm.app.JSONGetter;
import com.protv.mm.app.MyHttp;
import com.protv.mm.app.Routing;
import com.protv.mm.interfaces.OnCategoryItemClickListener;
import com.protv.mm.models.CategoryModel;
import com.protv.mm.models.FeedModel;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class FragmentOne extends Fragment {

    View v;

    RecyclerView recyclerViewFeed,recyclerViewCategory;
    SwipeRefreshLayout swipe;
    FeedAdapter feedAdapter;
    public static ArrayList<FeedModel> feeds=new ArrayList<>();
    public int PAGE=0;
    public boolean vipChannel=false;
    CategoryAdapter categoryAdapter;
    ArrayList<CategoryModel> categories=new ArrayList<>();
    Executor postExecutor;

    ImageView iv_xnxx,iv_xvideo;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_one, container, false);
        postExecutor= ContextCompat.getMainExecutor(getActivity());

        setUpView();

        return v;
    }

    private void setUpView(){
        recyclerViewCategory=v.findViewById(R.id.recyclerViewCategory);
        recyclerViewFeed=v.findViewById(R.id.recyclerViewFeed);
        swipe=v.findViewById(R.id.swipe);

        iv_xnxx=v.findViewById(R.id.iv_xnxx);
        iv_xvideo=v.findViewById(R.id.iv_xvideo);

        GridLayoutManager gm=new GridLayoutManager(getActivity(),2);
        recyclerViewFeed.setLayoutManager(gm);
        feedAdapter=new FeedAdapter(getActivity(),feeds,false);
        recyclerViewFeed.setAdapter(feedAdapter);

        LinearLayoutManager lm=new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        recyclerViewCategory.setLayoutManager(lm);
        categoryAdapter=new CategoryAdapter(getActivity(), categories, new OnCategoryItemClickListener() {
            @Override
            public void onClick(int position) {

                if(categories.size()>0) {
                    PAGE=position;
                    swipe.setRefreshing(true);
                    fetchFeed(categories.get(position).getId(),categories.get(position).isVip());
                }
            }
        });
        recyclerViewCategory.setAdapter(categoryAdapter);
        processCategoryJSON(SplashScreenActivity.categories);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(categories.size()>0){

                    fetchFeed(categories.get(PAGE).getId(),categories.get(PAGE).isVip());
                }
            }
        });


        iv_xvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ScrapingActivity.class);
                intent.putExtra("link","https://www.xvideos.com");
                startActivity(intent);
            }
        });

        iv_xnxx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(), ScrapingActivity.class);
                intent.putExtra("link","https://www.xnxx.com/");
                startActivity(intent);
            }
        });


    }

    public void processCategoryJSON(String response){
        try
        {
            categories.clear();
            JSONArray ja=new JSONArray(response);
            for(int i=0;i<ja.length();i++){
                JSONObject jo=ja.getJSONObject(i);
                String id=jo.getString("id");
                String category_name=jo.getString("category_name");
                boolean vip=jo.getInt("is_vip")==1;
                categories.add(new CategoryModel(category_name,id,vip));
            }
        }
        catch (JSONException e)
        {
            Log.e("JSONErr ",e.toString());
        }

        categoryAdapter.notifyDataSetChanged();
        if(categories.size()>0){
            if(feeds.size()==0){
                vipChannel=categories.get(0).isVip();

                fetchFeed(categories.get(0).getId(),vipChannel);
            }
        }

    }


    private void fetchFeed(String category_id,boolean vip){

        Log.e("VIP ",vipChannel+"");
        new Thread(() -> {
            MyHttp myHttp=new MyHttp(MyHttp.RequesMethod.GET, new MyHttp.Response() {
                @Override
                public void onResponse(String response) {
                    Log.e("Category ",response );
                    postExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            swipe.setRefreshing(false);
                            processFeedJSJON(response,vip);
                        }
                    });

                }
                @Override
                public void onError(String msg) {
                    Log.e("CategoryErr ",msg);
                }
            }).url(Routing.VIDEO+"?category_id="+category_id);

            myHttp.runTask();
        }).start();

    }

    public void processFeedJSJON(String inputJson,boolean vip){

        try
        {
            feeds.clear();
            JSONArray ja=new JSONArray(inputJson);
            for(int i=0;i<ja.length();i++){
                JSONObject jo=ja.getJSONObject(i);

                String thumbnail=Routing.THUMBNAIL+(jo.getString("thumbnail"));
                String videoTitle=(jo.getString("title"));
                String des=(jo.getString("description"));
                String downloadUrl=(jo.getString("url"));
                String id=jo.getString("id");
                feeds.add(new FeedModel(id,thumbnail,videoTitle,des,downloadUrl,vip));
            }
        }
        catch (JSONException e)
        {
            Log.e("FeedJSONErrr ",e.toString());
        }
        feedAdapter.notifyDataSetChanged();
    }
}
