package com.protv.mm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.protv.mm.adapters.CategoryAdapter2;
import com.protv.mm.app.MyHttp;
import com.protv.mm.app.Routing;
import com.protv.mm.models.CategoryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class CategoriesActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    Executor postExecutor;

    ArrayList<CategoryModel> categories=new ArrayList<>();
    CategoryAdapter2 adapter;

    EditText et;
    Button bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        postExecutor= ContextCompat.getMainExecutor(this);
        setUpView();

    }


    private void setUpView(){
        swipeRefreshLayout=findViewById(R.id.swipe);
        recyclerView=findViewById(R.id.recyclerView);
        et=findViewById(R.id.et_category);
        bt=findViewById(R.id.bt_add);


        LinearLayoutManager lm=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        adapter=new CategoryAdapter2(this,categories);
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchCategory();
            }
        });


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(et.getText().toString())){
                    addCategory();
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter category",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        fetchCategory();
        super.onResume();
    }


    private void fetchCategory(){
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            MyHttp myHttp=new MyHttp(MyHttp.RequesMethod.GET, new MyHttp.Response() {
                @Override
                public void onResponse(String response) {
                    Log.e("Category ",response );
                    postExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            categories.clear();
                            try{
                                JSONArray ja=new JSONArray(response);
                                for(int i=0;i<ja.length();i++){
                                    JSONObject jo=ja.getJSONObject(i);
                                    String id=jo.getString("id");
                                    String category_name=jo.getString("category_name");
                                    boolean vip=jo.getInt("is_vip")==1;
                                    categories.add(new CategoryModel(category_name,id,vip));
                                }

                            }catch (Exception e){}

                            adapter.notifyDataSetChanged();
                        }
                    });

                }
                @Override
                public void onError(String msg) {
                    Log.e("CategoryErr ",msg);
                    swipeRefreshLayout.setRefreshing(false);

                }
            }).url(Routing.CATEGORY);

            myHttp.runTask();
        }).start();
    }


    private void addCategory(){
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            MyHttp myHttp=new MyHttp(MyHttp.RequesMethod.POST, new MyHttp.Response() {
                @Override
                public void onResponse(String response) {
                    Log.e("Category ",response );
                    postExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            et.setText("");
                            fetchCategory();
                        }
                    });

                }
                @Override
                public void onError(String msg) {
                    Log.e("CategoryErr ",msg);
                    swipeRefreshLayout.setRefreshing(false);

                }
            }).url(Routing.CATEGORY)
                    .field("name",et.getText().toString())
                    .field("action","create");

            myHttp.runTask();
        }).start();
    }
}