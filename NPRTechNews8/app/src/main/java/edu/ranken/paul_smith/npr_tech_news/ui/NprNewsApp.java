package edu.ranken.paul_smith.npr_tech_news.ui;

import android.app.Application;
import android.util.Log;

public class NprNewsApp extends Application {
    private static final String LOG_TAG = "NprNewsApp";

    private NprNewsModel model;

    @Override
    public void onCreate() {
        super.onCreate();

        model = new NprNewsModel(this);

        Log.i(LOG_TAG, "onCreate()");
    }

    public NprNewsModel getModel() {
        return model;
    }
}
