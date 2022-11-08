package com.protv.mm;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.protv.mm.app.MyHttp;
import com.protv.mm.app.RealPathUtil;
import com.protv.mm.app.Routing;

import java.util.concurrent.Executor;

public class VideoAddingActivity extends AppCompatActivity {


    EditText et_title,et_url,et_description;
    Button bt_add;
    ImageView iv_thumbnail;
    String imagePath=null;
    Uri imageUri=null;
    ProgressBar pb;
    String title,description,url,category_id;

    Executor postExecutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_adding);
        category_id=getIntent().getExtras().getString("category_id","");
        postExecutor=ContextCompat.getMainExecutor(this);

        setUpView();
    }

    private void setUpView(){
        et_title=findViewById(R.id.et_title);
        et_url=findViewById(R.id.et_url);
        et_description=findViewById(R.id.et_descriptio);
        bt_add=findViewById(R.id.bt_add);
        iv_thumbnail=findViewById(R.id.iv_thumbnail);
        pb=findViewById(R.id.pb);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        iv_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermissionGranted()){
                    pickImageFromGallery();
                }else {
                    takePermission();
                }
            }
        });
    }

    private void validate(){
        title=et_title.getText().toString();
        description=et_description.getText().toString();
        url=et_url.getText().toString();

        if(TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(description)){
            Toast.makeText(this,"Please enter description",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(url)){
            Toast.makeText(this, "Please enter mediafire url", Toast.LENGTH_SHORT).show();
            return;
        }
        if(imagePath==null){
            Toast.makeText(this,"Please select the thumbnail",Toast.LENGTH_SHORT).show();
            return;
        }

        addVideo();
    }

    private void addVideo(){
        pb.setVisibility(View.VISIBLE);
        new Thread(() -> {
            MyHttp myHttp=new MyHttp(MyHttp.RequesMethod.POST, new MyHttp.Response() {
                @Override
                public void onResponse(String response) {
                    Log.e("Category ",response );
                    postExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.INVISIBLE);
                            finish();
                        }
                    });

                }
                @Override
                public void onError(String msg) {
                    Log.e("CategoryErr ",msg);
                    pb.setVisibility(View.INVISIBLE);

                }
            }).url(Routing.VIDEO)
                    .field("title",title)
                    .field("category_id",category_id)
                    .field("description",description)
                    .field("url",url)
                    .field("action","add")
                    .file("myfile",imagePath);

            myHttp.runTask();
        }).start();
    }

    private boolean isPermissionGranted(){
        int  readExternalStorage= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return  readExternalStorage== PackageManager.PERMISSION_GRANTED;
    }

    private void takePermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
    }


    private void pickImageFromGallery(){
        mGetContent.launch("image/*");
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if(uri!=null){
                        imageUri=uri;
                        imagePath=RealPathUtil.getRealPath(VideoAddingActivity.this,uri);
                        iv_thumbnail.setImageURI(imageUri);

                    }else{
                        Toast.makeText(getApplicationContext(),"No photo is selected!",Toast.LENGTH_SHORT).show();
                    }
                }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            if(requestCode==101){
                boolean readExternalStorage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(readExternalStorage){
                    pickImageFromGallery();
                }else {
                    takePermission();
                }
            }
        }
    }
}