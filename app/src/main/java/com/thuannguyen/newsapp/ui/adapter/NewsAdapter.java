package com.thuannguyen.newsapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding.view.RxView;
import com.thuannguyen.newsapp.R;
import com.thuannguyen.newsapp.models.NewsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsModelViewHolder> {
    private Context context;
    private List<NewsModel> newsModelList = new ArrayList<>();

    private NewsAdapterListener newsAdapterListener;

    public NewsAdapter(Context context, NewsAdapterListener newsAdapterListener) {
        this.context = context;
        this.newsAdapterListener = newsAdapterListener;
    }

    @NonNull
    @Override
    public NewsModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsModelViewHolder(context, v, newsAdapterListener);
    }

    @Override
    public void onBindViewHolder(NewsModelViewHolder holder, int position) {
        holder.bind(newsModelList.get(position));
    }

    public void setNewsModelList(List<NewsModel> newsModelList) {
        this.newsModelList.clear();
        this.newsModelList.addAll(newsModelList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return newsModelList.size();
    }

    public List<NewsModel> getNewsModelList() {
        return newsModelList;
    }

    static class NewsModelViewHolder extends RecyclerView.ViewHolder {
        private Context context;

        public NewsModelViewHolder(Context context, View itemView, NewsAdapterListener listener) {
            super(itemView);
            this.context = context;
            RxView.clicks(itemView)
                    .throttleFirst(1000L, TimeUnit.MILLISECONDS)
                    .subscribe(aVoid -> {
                        final int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onClickItem(pos);
                        }
                    });
        }

        public void bind(@NonNull NewsModel newsModel) {
            ((TextView) itemView.findViewById(R.id.titleText)).setText(newsModel.getTitle());
            ((TextView) itemView.findViewById(R.id.pubDate))
                    .setText(newsModel.getPubDate());
            ((TextView) itemView.findViewById(R.id.linkText)).setText(newsModel.getLink());
        }
    }

    public interface NewsAdapterListener {
        void onClickItem(int position);
    }
}
