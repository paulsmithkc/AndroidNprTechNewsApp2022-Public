package edu.ranken.paul_smith.npr_tech_news.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StoryViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView author;
    public TextView published;
    public TextView tags;
    public ImageView image;

    public StoryViewHolder(View itemView) {
        super(itemView);
    }
}
