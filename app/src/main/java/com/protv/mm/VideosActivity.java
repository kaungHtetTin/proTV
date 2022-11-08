package com.protv.mm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.protv.mm.adapters.AdminVideoAdapter;
import com.protv.mm.app.MyHttp;
import com.protv.mm.app.Routing;
import com.protv.mm.models.FeedModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class VideosActivity extends AppCompatActivity {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    String category_id;
    Executor postExecutor;
    SwipeRefreshLayout swipe;
    ArrayList<FeedModel> feedModels=new ArrayList<>();
    AdminVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        category_id=getIntent().getExtras().getString("category_id","");
        postExecutor= ContextCompat.getMainExecutor(this);
        setUpView();
    }

    private void setUpView(){
        fab=findViewById(R.id.fab);
        recyclerView=findViewById(R.id.recyclerView);
        swipe=findViewById(R.id.swipe);

        LinearLayoutManager lm=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        adapter=new AdminVideoAdapter(this,feedModels);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VideosActivity.this,VideoAddingActivity.class);
                intent.putExtra("category_id",category_id);
                startActivity(intent);
            }
        });

        fetchFeed(category_id,true);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeed(category_id,true);
            }
        });
    }

    private void fetchFeed(String category_id,boolean vip){
        swipe.setRefreshing(true);
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
            feedModels.clear();
            JSONArray ja=new JSONArray(inputJson);
            for(int i=0;i<ja.length();i++){
                JSONObject jo=ja.getJSONObject(i);

                String thumbnail=Routing.THUMBNAIL+(jo.getString("thumbnail"));
                String videoTitle=(jo.getString("title"));
                String des=(jo.getString("description"));
                String downloadUrl=(jo.getString("url"));
                String id=jo.getString("id");
                feedModels.add(new FeedModel(id,thumbnail,videoTitle,des,downloadUrl,vip));
            }
        }
        catch (JSONException e)
        {
            Log.e("FeedJSONErrr ",e.toString());
        }
        adapter.notifyDataSetChanged();
    }
}