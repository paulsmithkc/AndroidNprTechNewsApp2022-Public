package edu.ranken.paul_smith.npr_tech_news.ui;

import android.net.ConnectivityManager;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import edu.ranken.paul_smith.npr_tech_news.R;
import edu.ranken.paul_smith.npr_tech_news.data.ConnectivityDataSource;
import edu.ranken.paul_smith.npr_tech_news.data.NprNewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NprNewsModel extends AndroidViewModel {
    private static final String LOG_TAG = "NprNewsModel";

    // connectivity
    private final ConnectivityDataSource connectivityDataSource;
    private final LiveData<Boolean> connected;

    // news feed
    private final NprNewsAPI newsApi;
    private final MutableLiveData<Boolean> fetching;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<NprNewsAPI.Feed> feed;

    // snackbar
    private final MutableLiveData<String> snackbarMessage;

    public NprNewsModel(NprNewsApp app) {
        super(app);

        // connectivity
        ConnectivityManager connectivityManager = app.getSystemService(ConnectivityManager.class);
        connectivityDataSource = new ConnectivityDataSource(connectivityManager);
        connected = connectivityDataSource.getConnected();

        // news feed
        newsApi = new NprNewsAPI();
        fetching = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>(null);
        feed = new MutableLiveData<>(null);

        // snackbar
        snackbarMessage = new MutableLiveData<>(null);

        Log.i(LOG_TAG, "constructor");
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

    public LiveData<NprNewsAPI.Feed> getFeed() {
        return feed;
    }

    public LiveData<String> getSnackbarMessage() {
        return snackbarMessage;
    }

    public void refreshFeed() {
        Log.i(LOG_TAG, "refreshFeed()");
        if (Boolean.TRUE.equals(connected.getValue())) {
            fetching.postValue(true);
            newsApi.getFeed().enqueue(new Callback<NprNewsAPI.Feed>() {
                @Override
                public void onResponse(Call<NprNewsAPI.Feed> call, Response<NprNewsAPI.Feed> response) {
                    Log.i(LOG_TAG, "onResponse()");
                    String message = getApplication().getString(R.string.messageFeedRefreshed);
                    fetching.postValue(false);
                    errorMessage.postValue(null);
                    feed.postValue(response.body());
                    snackbarMessage.postValue(message);
                }

                @Override
                public void onFailure(Call<NprNewsAPI.Feed> call, Throwable t) {
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
