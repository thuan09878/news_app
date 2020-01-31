package com.thuannguyen.newsapp;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class NewsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
