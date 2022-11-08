package com.protv.mm;

import android.app.Application;

public class MyApp extends Application
{

    @Override
    public void onCreate()
    {
        TypefaceUtils.overrideFont(getApplicationContext(), "SERIF", "font/app.ttf");
        super.onCreate();
    }

}


