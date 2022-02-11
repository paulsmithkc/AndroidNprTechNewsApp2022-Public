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
        movieList.add(new Movie("Matrix", "https://www.themoviedb.org/t/p/w220_and_h330_face/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg"));
        movieList.add(new Movie("Matrix Reloaded", "https://www.themoviedb.org/t/p/w220_and_h330_face/9TGHDvWrqKBzwDxDodHYXEmOE6J.jpg"));
        movieList.add(new Movie("Matrix Revolutions", "https://www.themoviedb.org/t/p/w220_and_h330_face/qEWiBXJGXK28jGBAm8oFKKTB0WD.jpg"));

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
            list.add(new Movie(movieTitle, null));

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
