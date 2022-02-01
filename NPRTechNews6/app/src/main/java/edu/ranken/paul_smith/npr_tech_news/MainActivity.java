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

import edu.ranken.paul_smith.npr_tech_news.model.Feed;
import edu.ranken.paul_smith.npr_tech_news.model.FeedItem;
import edu.ranken.paul_smith.npr_tech_news.model.NprNewsService;
import retrofit2.Call;
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
    private FetchTask activeTask;

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

    private void showConnected() {
        if (isConnected) {
            connectedText.setText(R.string.connected);
            connectedIcon.setImageResource(R.drawable.ic_wifi_on);
        } else {
            connectedText.setText(R.string.disconnected);
            connectedIcon.setImageResource(R.drawable.ic_wifi_off);
        }
    }

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
            startFetch();
            Log.i(LOG_TAG, "onClick");
        });

        // network events
        connectivityManager = getSystemService(ConnectivityManager.class);
        connectivityManager.registerDefaultNetworkCallback(networkCallback);

        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
        if (activeTask != null) {
            activeTask.cancel();
        }
        super.onDestroy();
    }

    private void startFetch() {
        if (isConnected) {
            fetchButton.setEnabled(false);
            if (activeTask != null) activeTask.cancel();
            activeTask = new FetchTask();
            activeTask.start();
        } else {
            Snackbar.make(fetchButton, R.string.errorNetworkDisconnected, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onFetchResult(String output, Exception ex) {
        fetchButton.setEnabled(true);
        outputText.setText(output);

        if (ex != null) {
            Snackbar.make(fetchButton, output, Snackbar.LENGTH_SHORT).show();
        }
    }

    private class FetchTask extends Thread {

        private boolean cancelled = false;

        public void cancel() {
            cancelled = true;
            interrupt();
        }

        @Override
        public void run() {
            Log.i(LOG_TAG, "fetching news feed");

            // Build retrofit instance
            Retrofit retrofit =
                    new Retrofit.Builder()
                            .baseUrl("https://feeds.npr.org")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

            // Create service from interface
            NprNewsService service = retrofit.create(NprNewsService.class);

            // Get news feed
            try {
                Call<Feed> call = service.getFeed();
                Response<Feed> response = call.execute();
                Feed feed = response.body();
                Log.i(LOG_TAG, "parsed response");

                // Use parsed data to generate output
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
                        ArrayList<FeedItem> items = feed.items;
                        for (int i = 0; i < items.size(); ++i) {
                            FeedItem item = feed.items.get(i);
                            output.append("items[").append(i).append("]: ").append(item.title).append("\n");
                        }
                        output.append("\n");
                    }
                }


                onResult(output.toString(), null);
            } catch (IOException ex) {
                onResult(getString(R.string.errorFetchIO), ex);
            }
        }

        private void onResult(String output, Exception ex) {
            if (ex != null) {
                Log.e(LOG_TAG, output, ex);
            }
            if (!cancelled) {
                runOnUiThread(() -> onFetchResult(output, ex));
            }
        }
    }
}