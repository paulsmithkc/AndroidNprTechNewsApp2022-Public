package edu.ranken.paul_smith.movielist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import edu.ranken.paul_smith.movielist.data.Movie;
import edu.ranken.paul_smith.movielist.ui.MovieListAdapter;
import edu.ranken.paul_smith.movielist.ui.MovieListViewModel;

public class MainActivity extends AppCompatActivity {

    // views
    private EditText movieTitleInput;
    private ImageButton addButton;
    private TextView errorText;
    private RecyclerView movieList;

    // state
    private MovieListViewModel model;
    // private ArrayList<Movie> movies;
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

        // get model
        model = new ViewModelProvider(this).get(MovieListViewModel.class);
        model.getErrorText().observe(this, (message) -> {
            errorText.setVisibility(message != null ? View.VISIBLE : View.GONE);
            errorText.setText(message);
        });
//        model.getMovies().observe(this, (movies) -> {
//            adapter.setItems(movies);
//        });

        // init list
//        movies = new ArrayList<>();
//        movies.add(new Movie("Matrix"));
//        movies.add(new Movie("Matrix Reloaded"));
//        movies.add(new Movie("Matrix Revelations"));

        // create adapter
        adapter = new MovieListAdapter(this, model);

        // setup the recycler view
        movieList.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        movieList.setAdapter(adapter);

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
        model.addMovie(movieTitle);

        movieTitleInput.requestFocus();
        movieTitleInput.setSelection(0, movieTitleInput.getText().length());
        movieTitleInput.setText("");
    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}