package com.thuannguyen.newsapp.ui.activities.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.reactivex.disposables.CompositeDisposable;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    CompositeDisposable compositeDisposable;

    public MainViewModelFactory(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(compositeDisposable);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
