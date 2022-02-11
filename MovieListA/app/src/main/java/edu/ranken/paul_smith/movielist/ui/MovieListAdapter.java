package edu.ranken.paul_smith.movielist.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.ranken.paul_smith.movielist.R;
import edu.ranken.paul_smith.movielist.data.Movie;

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private AppCompatActivity context;
    private LayoutInflater layoutInflater;
    private List<Movie> items;

    public MovieListAdapter(AppCompatActivity context, List<Movie> items) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
    }

    public void setItems(List<Movie> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_movie, parent, false);

        MovieViewHolder vh = new MovieViewHolder(itemView);
        vh.title = itemView.findViewById(R.id.item_movie_title);
        vh.delete = itemView.findViewById(R.id.item_movie_delete);

        // register listeners
        vh.delete.setOnClickListener((view) -> {
            int index = vh.getAdapterPosition();
            items.remove(index);
            notifyItemRemoved(index);
            Toast.makeText(context, "REMOVED", Toast.LENGTH_SHORT).show();
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder vh, int position) {
        Movie item = items.get(position);
        vh.title.setText(item.title);
    }
}
