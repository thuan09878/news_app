package com.thuannguyen.newsapp;

import android.content.Context;

import com.thuannguyen.newsapp.ui.activities.main.MainViewModelFactory;

import io.reactivex.disposables.CompositeDisposable;

public class Injection {

    public static CompositeDisposable provideCompositeDisposable(Context context) {
        return new CompositeDisposable();
    }

    public static MainViewModelFactory provideViewModelFactory(Context context) {
        CompositeDisposable compositeDisposable = provideCompositeDisposable(context);
        return new MainViewModelFactory(compositeDisposable);
    }
}