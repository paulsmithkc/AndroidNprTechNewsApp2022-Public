package edu.ranken.paul_smith.npr_tech_news.ui;

import android.net.ConnectivityManager;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import edu.ranken.paul_smith.npr_tech_news.R;
import edu.ranken.paul_smith.npr_tech_news.data.ConnectivityDataSource;
import edu.ranken.paul_smith.npr_tech_news.data.NewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NprNewsModel extends AndroidViewModel {
    private static final String LOG_TAG = "NprNewsModel";

    // connectivity
    private final ConnectivityDataSource connectivityDataSource;
    private final LiveData<Boolean> connected;

    // news feed
    private final NewsAPI newsApi;
    private final MutableLiveData<Boolean> fetching;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<NewsAPI.Feed> feed;

    // snackbar
    private final MutableLiveData<String> snackbarMessage;

    public NprNewsModel(NprNewsApp app) {
        super(app);

        // connectivity
        ConnectivityManager connectivityManager = app.getSystemService(ConnectivityManager.class);
        connectivityDataSource = new ConnectivityDataSource(connectivityManager);
        connected = connectivityDataSource.getConnected();

        // news feed
        newsApi = new NewsAPI();
        fetching = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>(null);
        feed = new MutableLiveData<>(null);

        // snackbar
        snackbarMessage = new MutableLiveData<>(null);

        // fetch feed when connected
        connected.observeForever((connected) -> {
            if (connected) {
                if (feed.getValue() == null) {
                    refreshFeed();
                }
            }
        });

        Log.i(LOG_TAG, "constructor");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        connectivityDataSource.unregister();
    }

    public LiveData<Boolean> getConnected() {
        return connected;
    }

    public LiveData<Boolean> getFetching() {
        return fetching;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<NewsAPI.Feed> getFeed() {
        return feed;
    }

    public LiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }

    public void refreshFeed() {
        Log.i(LOG_TAG, "refreshFeed()");
        if (Boolean.TRUE.equals(connected.getValue())) {
            fetching.postValue(true);
            newsApi.getFeed().enqueue(new Callback<NewsAPI.Feed>() {
                @Override
                public void onResponse(Call<NewsAPI.Feed> call, Response<NewsAPI.Feed> response) {
                    Log.i(LOG_TAG, "onResponse()");
                    String message = getApplication().getString(R.string.messageFeedRefreshed);
                    fetching.postValue(false);
                    errorMessage.postValue(null);
                    feed.postValue(response.body());
                    snackbarMessage.postValue(message);
                }

                @Override
                public void onFailure(Call<NewsAPI.Feed> call, Throwable t) {
                    Log.e(LOG_TAG, "onFailure()", t);
                    String message = t.getMessage();
                    fetching.postValue(false);
                    errorMessage.postValue(message);
                    snackbarMessage.postValue(message);
                }
            });
        } else {
            String message = getApplication().getString(R.string.errorNetworkDisconnected);
            fetching.postValue(false);
            errorMessage.postValue(message);
            snackbarMessage.postValue(message);
        }
    }

    public void onSnackbarShown() {
        snackbarMessage.postValue(null);
    }
}
