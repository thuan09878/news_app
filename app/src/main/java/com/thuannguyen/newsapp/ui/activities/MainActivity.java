package com.thuannguyen.newsapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.thuannguyen.newsapp.BuildConfig;
import com.thuannguyen.newsapp.R;
import com.thuannguyen.newsapp.models.NewsModel;
import com.thuannguyen.newsapp.ui.adapter.NewsAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.thuannguyen.newsapp.ui.activities.NewsWebViewActivity.ARGS_NEWS_MODEL;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView rcvNews;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private NewsAdapter newsAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        fetchNews();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
        initToolbar();
        initRefresh();
        initRcvNews();
    }

    private void initRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            newsAdapter.setNewsModelList(new ArrayList<>());
            fetchNews();
        });
    }

    private void initRcvNews() {
        rcvNews = findViewById(R.id.rcv_news);

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
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    private void fetchNews() {
        progressBar.setVisibility(View.VISIBLE);
        String rssUrl = BuildConfig.NEWS_RSS_URL;

        compositeDisposable.add(Single.fromCallable(() -> {
            URL url = new URL(rssUrl);
            InputStream inputStream = url.openConnection().getInputStream();
            return parseFeed(inputStream);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(news -> {
                    progressBar.setVisibility(View.GONE);
                    newsAdapter.setNewsModelList(news);
                }, throwable -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
                }));
    }

    private List<NewsModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String link = null;
        String description = null;
        String pubDate = null;
        String guid = null;
        boolean isItem = false;
        List<NewsModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                } else if (name.equalsIgnoreCase("pubDate")) {
                    pubDate = result;
                } else if (name.equalsIgnoreCase("guid")) {
                    guid = result;
                }

                if (title != null && link != null && description != null && pubDate != null && guid != null) {
                    if (isItem) {
                        NewsModel item = new NewsModel(title, pubDate, description, link, guid);
                        items.add(item);
                    }

                    title = null;
                    link = null;
                    description = null;
                    pubDate = null;
                    guid = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }
}
