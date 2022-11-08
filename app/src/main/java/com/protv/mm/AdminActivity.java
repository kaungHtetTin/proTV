package com.protv.mm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.protv.mm.app.AppHandler;

import java.util.Calendar;
import java.util.TimeZone;

public class AdminActivity extends AppCompatActivity {

    EditText et_password,et_id;
    Button bt_check,bt_add,bt_add_video;
    LinearLayout layout_auth,layout_register;
    DatePicker datePicker;
    TextView tv_expire;

    FirebaseDatabase firebaseDatabase;
    private DatabaseReference db;
    String adminPassword=null;

    long expireDate=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        firebaseDatabase=FirebaseDatabase.getInstance();
        db= firebaseDatabase.getReference();


        setUpView();
    }

    private void setUpView(){
        et_password=findViewById(R.id.et_password);
        et_id=findViewById(R.id.et_id);
        bt_check=findViewById(R.id.bt_check);
        bt_add=findViewById(R.id.bt_add);
        layout_auth=findViewById(R.id.authLayout);
        layout_register=findViewById(R.id.registerLayout);
        datePicker=findViewById(R.id.datePicker);
        tv_expire=findViewById(R.id.tv_expire_date);
        bt_add_video=findViewById(R.id.bt_add_video);

        fetchAdminPassword();
        initializeDatePicker();

        bt_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adminPassword!=null){
                    if(adminPassword.equals(et_password.getText().toString())){
                        layout_auth.setVisibility(View.GONE);
                        layout_register.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(AdminActivity.this, "Auth Fail! Please try again", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AdminActivity.this, "Auth Fail! Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });



        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(et_id.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter user ID",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(expireDate<System.currentTimeMillis() || expireDate==0){
                    Toast.makeText(getApplicationContext(),"Please check expire date",Toast.LENGTH_SHORT).show();
                    return;
                }

                String userID=et_id.getText().toString();
                db.child("Users").child(userID).child("id").setValue(userID);
                db.child("Users").child(userID).child("expireDate").setValue(expireDate);

                et_id.setText("");
                Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_SHORT).show();
            }
        });


        bt_add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this,CategoriesActivity.class));
            }
        });

    }

    private void initializeDatePicker(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(System.currentTimeMillis());
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                tv_expire.setText("Expire At - "+day+" - "+ AppHandler.month[month]+" - "+year);
                calendar.set(i,i1,i2);
                expireDate=calendar.getTimeInMillis();

            }
        });
    }

    private void fetchAdminPassword(){
        db.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    adminPassword=(String)dataSnapshot.child("password").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}