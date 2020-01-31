package com.thuannguyen.newsapp.ui.activities.main;

import android.util.Xml;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thuannguyen.newsapp.BuildConfig;
import com.thuannguyen.newsapp.models.NewsModel;

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

public class MainViewModel extends ViewModel {
    private CompositeDisposable compositeDisposable;

    private MutableLiveData<List<NewsModel>> listNewsLiveData = null;
    private MutableLiveData<Boolean> isThemeChangedLiveData = null;

    public MainViewModel(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    public MutableLiveData<List<NewsModel>> getListNewsLiveData() {
        if (listNewsLiveData == null) {
            listNewsLiveData = new MutableLiveData<>();
            fetchNews();
        }
        return listNewsLiveData;
    }

    public void setListNewsLiveData(List<NewsModel> listNewsLiveData) {
        this.listNewsLiveData.setValue(listNewsLiveData);
    }

    public MutableLiveData<Boolean> getIsThemeChangedLiveData() {
        if (isThemeChangedLiveData == null) {
            isThemeChangedLiveData = new MutableLiveData<>();
        }
        return isThemeChangedLiveData;
    }

    public void changeTheme() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        isThemeChangedLiveData.setValue(true);
    }

    public void fetchNews() {
        String rssUrl = BuildConfig.NEWS_RSS_URL;

        compositeDisposable.add(Single.fromCallable(() -> {
            URL url = new URL(rssUrl);
            InputStream inputStream = url.openConnection().getInputStream();
            return parseFeed(inputStream);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(news -> {
                    listNewsLiveData.postValue(news);
                }, throwable -> {
                    listNewsLiveData.postValue(new ArrayList<>());
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

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
