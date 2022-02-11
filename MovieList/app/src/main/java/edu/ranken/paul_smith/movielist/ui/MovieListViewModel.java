package edu.ranken.paul_smith.movielist.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.ranken.paul_smith.movielist.data.Movie;

public class MovieListViewModel extends ViewModel {

    // private MutableLiveData<String> movieTitleInput;
    private MutableLiveData<String> errorText;
    private MutableLiveData<ArrayList<Movie>> movies;

    public MovieListViewModel() {

        // init list
        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie("Matrix"));
        movieList.add(new Movie("Matrix Reloaded"));
        movieList.add(new Movie("Matrix Revolutions"));

        // create live data
        errorText = new MutableLiveData<>(null);
        movies = new MutableLiveData<>(movieList);
    }

    public LiveData<String> getErrorText() {
        return errorText;
    }

    public LiveData<ArrayList<Movie>> getMovies() {
        return movies;
    }

    public void addMovie(String movieTitle) {
        if (movieTitle.length() == 0) {
            errorText.postValue("title was blank");
        } else {
            errorText.postValue("");

            ArrayList<Movie> list = movies.getValue();
            list.add(new Movie(movieTitle));

            if (list.size() > 10) {
                list.remove(0);
            }

            movies.postValue(list);
        }
    }

    public void removeMovie(int index) {
        ArrayList<Movie> list = movies.getValue();
        list.remove(index);
        movies.postValue(list);
    }
}
