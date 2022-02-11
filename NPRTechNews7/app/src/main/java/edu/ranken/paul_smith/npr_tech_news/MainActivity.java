package edu.ranken.paul_smith.npr_tech_news;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import edu.ranken.paul_smith.npr_tech_news.data.NewsAPI;
import edu.ranken.paul_smith.npr_tech_news.ui.NprNewsApp;
import edu.ranken.paul_smith.npr_tech_news.ui.NprNewsModel;

public class MainActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = "MainActivity";

    // views
    private Button refreshButton;
    private TextView errorText;
    private TextView outputText;
    private TextView connectedText;
    private ImageView connectedIcon;

    // state
    private NprNewsApp app;
    private NprNewsModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        refreshButton = findViewById(R.id.refreshButton);
        errorText = findViewById(R.id.errorText);
        outputText = findViewById(R.id.outputText);
        connectedText = findViewById(R.id.connectedText);
        connectedIcon = findViewById(R.id.connectedIcon);

        // bind to model
        app = (NprNewsApp) getApplication(); // Remember to set android:name in manifest!!
        model = app.getModel();
        model.getConnected().observe(this, (connnected) -> showConnected(connnected));
        model.getFetching().observe(this, (fetching) -> {
            refreshButton.setEnabled(!fetching);
        });
        model.getErrorMessage().observe(this, (error) -> {
            errorText.setVisibility(error != null ? View.VISIBLE : View.GONE);
            errorText.setText(error);
        });
        model.getFeed().observe(this, (feed) -> showFeed(feed));
        model.getSnackbarMessage().observe(this, (message) -> {
            if (message != null) {
                Snackbar.make(refreshButton, message, Snackbar.LENGTH_SHORT).show();
                model.onSnackbarShown();
            }
        });

        // register listeners
        refreshButton.setOnClickListener((view) -> {
            model.refreshFeed();
            Log.i(LOG_TAG, "onClick");
        });

        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showConnected(boolean isConnected) {
        if (isConnected) {
            connectedText.setText(R.string.connected);
            connectedIcon.setImageResource(R.drawable.ic_wifi_on);
        } else {
            connectedText.setText(R.string.disconnected);
            connectedIcon.setImageResource(R.drawable.ic_wifi_off);
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