package com.thuannguyen.newsapp.ui.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.thuannguyen.newsapp.Injection;
import com.thuannguyen.newsapp.R;
import com.thuannguyen.newsapp.ui.activities.NewsWebViewActivity;
import com.thuannguyen.newsapp.ui.adapter.NewsAdapter;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


import static com.thuannguyen.newsapp.ui.activities.NewsWebViewActivity.ARGS_NEWS_MODEL;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private MainViewModel mainViewModel;

    private NewsAdapter newsAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        MainViewModelFactory mainViewModelFactory = Injection.provideViewModelFactory();
        mainViewModel = new ViewModelProvider(this, mainViewModelFactory).get(MainViewModel.class);

        setupObservers();
    }

    private void setupObservers() {
        mainViewModel.getListNewsLiveData().observe(this, newsModels -> {
            progressBar.setVisibility(View.GONE);
            if (newsModels.isEmpty()) {
                Toast.makeText(MainActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
            } else {
                newsAdapter.setNewsModelList(newsModels);
            }
        });

        mainViewModel.getIsThemeChangedLiveData().observe(this, isChanged -> {
            if (isChanged) {
                restartApp();
            }
        });
    }

    private void restartApp() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_theme:
                mainViewModel.changeTheme();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void initViews() {
        initToolbar();
        initRcvNews();
        initRefresh();
    }

    private void initRefresh() {
        progressBar = findViewById(R.id.progress_bar);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);

        refreshLayout.setOnRefreshListener(() -> {
            newsAdapter.setNewsModelList(new ArrayList<>());
            progressBar.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
            mainViewModel.fetchNews();
        });
    }

    private void initRcvNews() {
        RecyclerView rcvNews = findViewById(R.id.rcv_news);
        newsAdapter = new NewsAdapter(this, position -> {
            Intent intent = new Intent(this, NewsWebViewActivity.class);
            intent.putExtra(ARGS_NEWS_MODEL, newsAdapter.getNewsModelList().get(position));
            startActivity(intent);
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvNews.setLayoutManager(layoutManager);
        rcvNews.setHasFixedSize(true);
        rcvNews.setAdapter(newsAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }
}
