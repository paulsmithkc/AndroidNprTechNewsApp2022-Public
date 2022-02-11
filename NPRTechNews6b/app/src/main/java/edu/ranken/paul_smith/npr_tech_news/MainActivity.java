package edu.ranken.paul_smith.npr_tech_news;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;

import edu.ranken.paul_smith.npr_tech_news.data.NewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = "NPRTechNews";
    // private static final String BASE_URL = "https://feeds.npr.org/1019/feed.json";

    // views
    private Button fetchButton;
    private TextView outputText;
    private TextView connectedText;
    private ImageView connectedIcon;

    // state
    private boolean isConnected;
    private ConnectivityManager connectivityManager;
    private NewsAPI newsApi;
    private Call<NewsAPI.Feed> newsRequest;

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            Log.i(LOG_TAG, "Connected to " + network);
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            isConnected =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

            Log.i(LOG_TAG, "Network " + network + " has internet capability: " + isConnected);
            runOnUiThread(() -> showConnected());
        }

        @Override
        public void onLost(@NonNull Network network) {
            isConnected = false;

            Log.i(LOG_TAG, "Disconnected from " + network);
            runOnUiThread(() -> showConnected());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        fetchButton = findViewById(R.id.fetchButton);
        outputText = findViewById(R.id.outputText);
        connectedText = findViewById(R.id.connectedText);
        connectedIcon = findViewById(R.id.connectedIcon);

        // register listeners
        fetchButton.setOnClickListener((view) -> {
            refreshFeed();
            Log.i(LOG_TAG, "onClick");
        });

        // network events
        connectivityManager = getSystemService(ConnectivityManager.class);
        connectivityManager.registerDefaultNetworkCallback(networkCallback);

        // news api
        newsApi = new NewsAPI();
        newsRequest = null;

        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
        if (newsRequest != null) {
            newsRequest.cancel();
        }
        super.onDestroy();
    }

    private void showConnected() {
        if (isConnected) {
            connectedText.setText(R.string.connected);
            connectedIcon.setImageResource(R.drawable.ic_wifi_on);
        } else {
            connectedText.setText(R.string.disconnected);
            connectedIcon.setImageResource(R.drawable.ic_wifi_off);
        }
    }

    private void refreshFeed() {
        if (isConnected) {
            fetchButton.setEnabled(false);
            if (newsRequest != null) newsRequest.cancel();
            newsRequest = newsApi.getFeed();
            newsRequest.enqueue(new Callback<NewsAPI.Feed>() {
                @Override
                public void onResponse(Call<NewsAPI.Feed> call, Response<NewsAPI.Feed> response) {
                    Log.i(LOG_TAG, "onResponse()");
                    String message = getString(R.string.messageFeedRefreshed);
                    fetchButton.setEnabled(true);
                    showFeed(response.body());
                    Snackbar.make(fetchButton, message, Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<NewsAPI.Feed> call, Throwable t) {
                    Log.e(LOG_TAG, "onFailure()", t);
                    String message = t.getMessage();
                    fetchButton.setEnabled(true);
                    outputText.setText(message);
                    Snackbar.make(fetchButton, message, Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            Snackbar.make(fetchButton, R.string.errorNetworkDisconnected, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showFeed(NewsAPI.Feed feed) {
        StringBuilder output = new StringBuilder();

        if (feed == null) {
            output.append("FEED MISSING\n");
        } else {
            output.append("NEWS FEED\n");
            output.append("description: ").append(feed.description).append("\n\n");
            if (feed.author == null) {
                output.append("AUTHOR MISSING\n\n");
            } else {
                output.append("author.name: ").append(feed.author.name).append("\n");
                output.append("author.url: ").append(feed.author.url).append("\n");
                output.append("author.avatar: ").append(feed.author.avatar).append("\n");
                output.append("\n");
            }
            if (feed.items == null) {
                output.append("ITEMS MISSING\n");
            } else {
                ArrayList<NewsAPI.FeedItem> items = feed.items;
                for (int i = 0; i < items.size(); ++i) {
                    NewsAPI.FeedItem item = feed.items.get(i);
                    output.append("items[").append(i).append("]: ").append(item.title).append("\n");
                }
                output.append("\n");
            }
        }

        outputText.setText(output);
    }
}