package edu.ranken.paul_smith.npr_tech_news;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import edu.ranken.paul_smith.npr_tech_news.data.NewsAPI;
import edu.ranken.paul_smith.npr_tech_news.ui.NewsFeedAdapter;
import edu.ranken.paul_smith.npr_tech_news.ui.NprNewsApp;
import edu.ranken.paul_smith.npr_tech_news.ui.NprNewsModel;

public class MainActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = "MainActivity";

    // views
    private Button refreshButton;
    private TextView errorText;
    private RecyclerView storyList;
    private TextView connectedText;
    private ImageView connectedIcon;

    // state
    private NprNewsApp app;
    private NprNewsModel model;
    private NewsFeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        refreshButton = findViewById(R.id.refreshButton);
        errorText = findViewById(R.id.errorText);
        storyList = findViewById(R.id.storyList);
        connectedText = findViewById(R.id.connectedText);
        connectedIcon = findViewById(R.id.connectedIcon);

        // create adapter
        adapter = new NewsFeedAdapter(this);

        // configure recycler view
        int storiesPerRow = getResources().getInteger(R.integer.stories_per_row);
        storyList.setLayoutManager(new GridLayoutManager(this, storiesPerRow, RecyclerView.VERTICAL, false));
        storyList.setAdapter(adapter);

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
        adapter.setFeed(feed);
    }
}