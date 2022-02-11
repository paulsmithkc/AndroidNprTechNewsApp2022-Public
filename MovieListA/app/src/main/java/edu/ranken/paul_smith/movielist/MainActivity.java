package edu.ranken.paul_smith.movielist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.ranken.paul_smith.movielist.data.Movie;
import edu.ranken.paul_smith.movielist.ui.MovieListAdapter;

public class MainActivity extends AppCompatActivity {

    // views
    private EditText movieTitleInput;
    private ImageButton addButton;
    private TextView errorText;
    private RecyclerView movieList;

    // state
    private ArrayList<Movie> movies;
    private MovieListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        movieTitleInput = findViewById(R.id.movieTitleInput);
        addButton = findViewById(R.id.addButton);
        errorText = findViewById(R.id.errorText);
        movieList = findViewById(R.id.movieList);

        // init list
        movies = new ArrayList<>();
        movies.add(new Movie("Matrix"));
        movies.add(new Movie("Matrix Reloaded"));
        movies.add(new Movie("Matrix Revolutions"));

        // create adapter
        adapter = new MovieListAdapter(this, movies);

        // setup the recycler view
        movieList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        movieList.setAdapter(adapter);

        // register listeners
        movieTitleInput.setOnEditorActionListener((view, actionId, keyEvent) -> {
            Log.i("MainActivity", "actionId: " + actionId);
            if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard(this, view);
                addMovie();
                return true;
            }
            return false;
        });
        addButton.setOnClickListener((view) -> {
            hideKeyboard(this, view);
            addMovie();
        });
    }

    private void addMovie() {
        String movieTitle = movieTitleInput.getText().toString().trim();
        if (movieTitle.length() == 0) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText(R.string.errorMovieTitleEmpty);
        } else {
            errorText.setVisibility(View.GONE);
            errorText.setText("");

            // update the list
            movies.add(new Movie(movieTitle));
            adapter.notifyItemInserted(movies.size() - 1);
            if (movies.size() > 10) {
                movies.remove(0);
                adapter.notifyItemRemoved(0);
            }
        }

        movieTitleInput.requestFocus();
        movieTitleInput.setSelection(0, movieTitleInput.getText().length());
        movieTitleInput.setText("");
    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}