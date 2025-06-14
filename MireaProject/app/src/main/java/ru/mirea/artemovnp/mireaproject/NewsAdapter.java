package ru.mirea.artemovnp.mireaproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsItems;
    private Context context;

    public NewsAdapter(List<NewsItem> newsItems, Context context) {
        this.newsItems = newsItems;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = newsItems.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());

        if (item.getUrlToImage() != null && !item.getUrlToImage().isEmpty()) {
            Picasso.get()
                    .load(item.getUrlToImage())
                    .placeholder(R.drawable.ic_news_placeholder)
                    .error(R.drawable.ic_news_placeholder)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_news_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        ImageView image;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_title);
            description = itemView.findViewById(R.id.news_description);
            image = itemView.findViewById(R.id.news_image);
        }
    }
}