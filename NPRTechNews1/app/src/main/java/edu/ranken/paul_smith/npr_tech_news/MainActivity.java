package edu.ranken.paul_smith.npr_tech_news;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        fetchButton = findViewById(R.id.fetchButton);
        outputText = findViewById(R.id.outputText);

        // register listeners
        fetchButton.setOnClickListener((view) -> {
            fetchFeed();
        });
    }

    private void fetchFeed() {
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

            // read the response
            try (InputStream stream = connection.getInputStream()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

                    // read the response line by line
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    outputText.setText(sb);

                }
            }

        } catch (MalformedURLException ex) {
            showErrorMessage(getString(R.string.errorFetchURL), ex);
        } catch (IOException ex) {
            showErrorMessage(getString(R.string.errorFetchIO), ex);
        } catch (Exception ex) {
            showErrorMessage(getString(R.string.errorFetchOther), ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void showErrorMessage(String message, Exception ex) {
        Log.e(LOG_TAG, message, ex);
        outputText.setText(message);
    }
}