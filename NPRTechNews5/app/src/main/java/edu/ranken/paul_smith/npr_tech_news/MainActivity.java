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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = "NPRTechNews";
    private static final String BASE_URL = "https://feeds.npr.org/1019/feed.json";

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
        Log.i(LOG_TAG, "onDestroy");
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

    private void onFetchResult(FetchResult result) {
        fetchButton.setEnabled(true);
        outputText.setText(result.output);

        if (result.exception != null) {
            Log.e(LOG_TAG, result.output, result.exception);
            Snackbar.make(fetchButton, result.output, Snackbar.LENGTH_SHORT).show();
        }
    }

    private class FetchResult {
        public final String output;
        public final Exception exception;

        public FetchResult(String output, Exception exception) {
            this.output = output;
            this.exception = exception;
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
            HttpURLConnection connection = null;
            FetchResult result;

            try {
                URL url = new URL(BASE_URL);

                // connect
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15_000);
                connection.setReadTimeout(15_000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                Log.i(LOG_TAG, "connected");

                // open a reader for the response
                try (InputStream stream = new BufferedInputStream(connection.getInputStream())) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

                        // read the response line by line
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while (!cancelled && (line = reader.readLine()) != null) {
                            sb.append(line).append('\n');
                        }

                        // create the result
                        result = new FetchResult(sb.toString(), null);
                    }
                }

            } catch (MalformedURLException ex) {
                result = new FetchResult(getString(R.string.errorFetchURL), ex);
            } catch (IOException ex) {
                result = new FetchResult(getString(R.string.errorFetchIO), ex);
            } catch (Exception ex) {
                result = new FetchResult(getString(R.string.errorFetchOther), ex);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            if (!cancelled) {
                final FetchResult finalResult = result;
                runOnUiThread(() -> onFetchResult(finalResult));
            }
        }
    }
}