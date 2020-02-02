package com.thuannguyen.newsapp;

import com.thuannguyen.newsapp.ui.activities.main.MainViewModelFactory;

import io.reactivex.disposables.CompositeDisposable;

public class Injection {

    public static CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    public static MainViewModelFactory provideViewModelFactory() {
        CompositeDisposable compositeDisposable = provideCompositeDisposable();
        return new MainViewModelFactory(compositeDisposable);
    }
}