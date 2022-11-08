package com.protv.mm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

public class PermissionActivity extends Activity {
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE );
        } else {
            GoToMainActivity();
        }
    }

    private void GoToMainActivity (){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(String permission , int code ){
        requestPermissions(new String[]{permission}, code);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isPermissionGranted (Context c, String permission){
        return checkSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void exitApp(){
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GoToMainActivity();
                } else {
                    exitApp();
                }
                return;
            }
        }
    }


}
