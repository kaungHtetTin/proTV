package com.protv.mm;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.protv.mm.app.AppHandler;
import com.protv.mm.fragments.FragmentOne;
import com.protv.mm.fragments.FragmentThree;
import com.protv.mm.fragments.FragmentTwo;
import com.startapp.sdk.adsbase.StartAppSDK;
import java.util.Objects;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    BottomNavigationView bnv;
    FragmentOne fragmentOne;
//    FragmentTwo fragmentTwo;
    FragmentThree fragmentThree;

    RelativeLayout mainLayout;
    Executor postExecutor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout=findViewById(R.id.activity_main);
        postExecutor= ContextCompat.getMainExecutor(this);

        if(!SplashScreenActivity.isVIP)StartAppSDK.init(this, AppHandler.START_AD_ID, true);
        setUpView();
        if(SplashScreenActivity.updateAVAILABLE)updateDialog();

        checkVPN();
    }

    private void setUpView(){
        fragmentOne =new FragmentOne();
//        fragmentTwo=new FragmentTwo();
        fragmentThree=new FragmentThree();

        viewPager=findViewById(R.id.view_pager);
        ViewPagerAdapter vAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(vAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bnv.getMenu().findItem(R.id.frag_one).setChecked(true);
                        break;
//                    case 1:
//                        bnv.getMenu().findItem(R.id.frag_two).setChecked(true);
//                        break;
                    case 1:
                        bnv.getMenu().findItem(R.id.frag_three).setChecked(true);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        bnv=findViewById(R.id.bot_nav_view);
        bnv.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.frag_one:
                    viewPager.setCurrentItem(0);
                    break;

//                case R.id.frag_two:
//                    viewPager.setCurrentItem(1);
//                    break;

                case R.id.frag_three:
                    viewPager.setCurrentItem(1);
                    break;
            }

            return true;
        });


    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ViewPagerAdapter(FragmentManager fm){
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new FragmentOne();
//                case 1:
//                    return new FragmentTwo();
                case 1:
                    return new FragmentThree();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private void updateDialog(){
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View parent_view = LayoutInflater.from(this).inflate(R.layout.dialog_two_buttons, null);
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
                startActivity(browserIntent);
            }
        });


        call.setText("GET ON PLAYSTORE");
        call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View p1)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                startActivity(browserIntent);

            }}
        );
    }


    private void checkVPN(){
        ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(this).getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activeNetwork = cm.getActiveNetwork();
        }
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);

        Log.e("VPN ","USING - "+vpnInUse);
    }
}