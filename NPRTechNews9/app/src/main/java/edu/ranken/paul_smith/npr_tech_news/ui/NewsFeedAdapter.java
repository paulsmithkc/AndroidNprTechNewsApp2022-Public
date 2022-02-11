package edu.ranken.paul_smith.npr_tech_news.ui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import edu.ranken.paul_smith.npr_tech_news.R;
import edu.ranken.paul_smith.npr_tech_news.data.NewsAPI;

public class NewsFeedAdapter extends RecyclerView.Adapter<StoryViewHolder> {
    private static final String LOG_TAG = "NewsFeedAdapter";

    private AppCompatActivity context;
    private LayoutInflater layoutInflater;
    private Picasso picasso;
    private NewsAPI.Feed feed;

    public NewsFeedAdapter(AppCompatActivity context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.picasso = Picasso.get();
        this.feed = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFeed(NewsAPI.Feed feed) {
        this.feed = feed;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (feed != null) {
            if (feed.items != null) {
                return feed.items.size();
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_story, parent, false);

        StoryViewHolder vh = new StoryViewHolder(itemView);
        vh.title = itemView.findViewById(R.id.item_story_title);
        vh.author = itemView.findViewById(R.id.item_story_author);
        vh.published = itemView.findViewById(R.id.item_story_published);
        vh.tags = itemView.findViewById(R.id.item_story_tags);
        vh.image = itemView.findViewById(R.id.item_story_image);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder vh, int position) {
        NewsAPI.FeedItem item = feed.items.get(position);
        
        String published = formatDateTime(item.date_published);
        String tags = formatTags(item.tags);

        vh.title.setText(item.title);
        vh.author.setText(item.author.name);
        vh.published.setText(published);
        vh.tags.setText(tags);
        vh.image.setImageResource(item.image != null ? R.drawable.ic_downloading : 0);
        vh.image.setVisibility(item.image != null ? View.VISIBLE : View.GONE);

        // cancel request in progress
        this.picasso.cancelRequest(vh.image);

        // request image to be downloaded, resized, and displayed
        if (item.image != null) {
            this.picasso
                .load(item.image)
                .placeholder(R.drawable.ic_downloading)
                .error(R.drawable.ic_error)
                .resizeDimen(R.dimen.item_story_image_resize, R.dimen.item_story_image_resize)
                .centerCrop()
                .into(vh.image);
        }
    }

    private String formatDateTime(String raw) {
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-dd-MM'T'HH:mm:ssZ", Locale.US);
            Date parsedDate = inputFormat.parse(raw);
            if (parsedDate != null) {
                DateFormat outputFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
                return outputFormat.format(parsedDate);
            } else {
                return context.getString(R.string.invalidDate);
            }
        } catch (ParseException ex) {
            Log.e(LOG_TAG, "Failed to parse: " + raw, ex);
            return context.getString(R.string.invalidDate);
        }
    }

    private String formatTags(List<String> tags) {
        if (tags == null || tags.size() == 0) {
            return context.getString(R.string.noTags);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String tag : tags) {
                sb.append(tag.toLowerCase(Locale.US)).append(", ");
            }
            sb.setLength(sb.length() - 2);
            return sb.toString();
        }
    }
}
