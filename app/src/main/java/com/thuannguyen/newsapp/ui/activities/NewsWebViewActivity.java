package com.thuannguyen.newsapp.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.thuannguyen.newsapp.R;
import com.thuannguyen.newsapp.models.NewsModel;

public class NewsWebViewActivity extends AppCompatActivity {

    public static String ARGS_NEWS_MODEL = "args_news_model";

    private WebView webView;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private NewsModel newsModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_webview);

        newsModel = getIntent().getParcelableExtra(ARGS_NEWS_MODEL);

        init();
        swipeRefreshLayout.setOnRefreshListener(this::loadNewsUrl);

        loadNewsUrl();
    }

    private void init() {
        initViews();
        initWebView();
        initToolbar();
    }

    private void initWebView() {
        progressBar.setMax(100);
        progressBar.setProgress(0);
        webView.clearCache(true);
        webView.setInitialScale(1);
        WebSettings wSettings = webView.getSettings();
        wSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wSettings.setDefaultTextEncodingName("utf-8");
        wSettings.setLoadsImagesAutomatically(true);
        wSettings.setLoadWithOverviewMode(true);
        wSettings.setDomStorageEnabled(true);
        wSettings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.4; ko-kr; "
                + "LG-L160L Build/IML74K) AppleWebkit/537.36 (KHTML, like Gecko) "
                + "Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        wSettings.setSupportZoom(true);
        wSettings.setBuiltInZoomControls(true);
        wSettings.setDisplayZoomControls(false);
        wSettings.setUseWideViewPort(true);
        wSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient());
    }

    private void loadNewsUrl() {
        if (newsModel != null && newsModel.getLink() != null) {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            webView.loadUrl(newsModel.getLink());
        }
    }

    private void initViews() {
        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progress_bar);
        toolbar = findViewById(R.id.tool_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (newsModel != null && newsModel.getTitle() != null) toolbar.setTitle(newsModel.getTitle());
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }
}
