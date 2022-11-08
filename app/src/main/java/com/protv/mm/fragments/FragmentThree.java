package com.protv.mm.fragments;

import static com.protv.mm.SplashScreenActivity.isVIP;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.protv.mm.AdminActivity;
import com.protv.mm.R;
import com.protv.mm.SplashScreenActivity;
import com.protv.mm.adapters.MenuAdapter;
import com.protv.mm.app.AppHandler;
import com.protv.mm.models.MenuModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FragmentThree extends Fragment {
    View v;

    RecyclerView recyclerView;
    TextView tv_userId,tv_expireDate;
    MenuAdapter adapter;
    ArrayList<MenuModel>  menus=new ArrayList<>();
    ImageView iv_logo;
    int click;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_three, container, false);

        click=0;
        setUpView();

        return v;
    }


    private void setUpView(){
        recyclerView=v.findViewById(R.id.recyclerMenu);
        tv_userId=v.findViewById(R.id.tv_userID);
        tv_expireDate=v.findViewById(R.id.tv_expire_date);
        iv_logo=v.findViewById(R.id.iv);

        tv_userId.setText("ID - "+SplashScreenActivity.userId);
        if(isVIP){
            tv_expireDate.setText("VIP Plan Expire Date - "+AppHandler.formatTime(SplashScreenActivity.EXPIRE_DATE));
        }else{
            tv_expireDate.setText("No VIP Plan");
        }

//        iv_logo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                click++;
//                if(click<3){
//                    Toast.makeText(getActivity(),"Click",Toast.LENGTH_SHORT).show();
//                }else{
//                    click=0;
//                    startActivity(new Intent(getActivity(), AdminActivity.class));
//                }
//            }
//        });

        LinearLayoutManager lm=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);
        adapter=new MenuAdapter(getActivity(),menus);
        recyclerView.setAdapter(adapter);

        menus.add(new MenuModel("Get Ads Free Version",R.drawable.ic_baseline_attach_money_24));
        menus.add(new MenuModel("Check Update",R.drawable.ic_check_update));
        menus.add(new MenuModel("About",R.drawable.ic_about));


        adapter.notifyDataSetChanged();


    }
}
