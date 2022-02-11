package edu.ranken.paul_smith.movielist.ui;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MovieViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public ImageButton delete;

    public MovieViewHolder(View itemView) {
        super(itemView);
    }
}
