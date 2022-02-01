package edu.ranken.paul_smith.npr_tech_news;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

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

    // state
    private FetchTask activeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        fetchButton = findViewById(R.id.fetchButton);
        outputText = findViewById(R.id.outputText);

        // register listeners
        fetchButton.setOnClickListener((view) -> {
            if (isConnected()) {
                activeTask = new FetchTask();
                activeTask.execute();
            } else {
                Snackbar.make(fetchButton, R.string.errorNetworkDisconnected, Snackbar.LENGTH_SHORT).show();
            }
            Log.i(LOG_TAG, "onClick");
        });

        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        if (activeTask != null) {
            activeTask.cancel(true);
        }
        super.onDestroy();
    }

    private boolean isConnected() {
        ConnectivityManager manager = getSystemService(ConnectivityManager.class);
        if (manager != null) {
            Network network = manager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                }
            }
        }
        return false;
    }

//    // Deprecated API for getting instantaneous network state
//    private boolean isConnected() {
//        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.isConnected();
//    }

    private class FetchResult {
        public final String output;
        public final Exception exception;

        public FetchResult(String output, Exception exception) {
            this.output = output;
            this.exception = exception;
        }
    }

    private class FetchTask extends AsyncTask<Void, Void, FetchResult> {

        @Override
        protected void onPreExecute() {
            fetchButton.setEnabled(false);
        }

        @Override
        protected FetchResult doInBackground(Void... params) {
            Log.i(LOG_TAG, "fetching news feed");
            HttpURLConnection connection = null;
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
                try (InputStream stream = connection.getInputStream()) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

                        // read the response line by line
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append('\n');
                        }

                        // return the result
                        return new FetchResult(sb.toString(), null);
                    }
                }

            } catch (MalformedURLException ex) {
                return new FetchResult(getString(R.string.errorFetchURL), ex);
            } catch (IOException ex) {
                return new FetchResult(getString(R.string.errorFetchIO), ex);
            } catch (Exception ex) {
                return new FetchResult(getString(R.string.errorFetchOther), ex);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(FetchResult result) {
            fetchButton.setEnabled(true);
            outputText.setText(result.output);

            if (result.exception != null) {
                Log.e(LOG_TAG, result.output, result.exception);
                Snackbar.make(fetchButton, result.output, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}