package edu.ranken.paul_smith.npr_tech_news.data;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ConnectivityDataSource extends ConnectivityManager.NetworkCallback {
    private static final String LOG_TAG = "ConnectivityDataSource";

    private final MutableLiveData<Boolean> connected;
    private final ConnectivityManager connectivityManager;

    public ConnectivityDataSource(ConnectivityManager connectivityManager) {
        this.connected = new MutableLiveData<>(false);
        this.connectivityManager = connectivityManager;

        // register callback
        this.connectivityManager.registerDefaultNetworkCallback(this);
    }

    public void unregister() {
        this.connectivityManager.unregisterNetworkCallback(this);
    }

    public LiveData<Boolean> getConnected() {
        return connected;
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        Log.i(LOG_TAG, "Connected to " + network);
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        boolean connected =
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        Log.i(LOG_TAG, "Network " + network + " has internet capability: " + connected);
        this.connected.postValue(connected);
    }

    @Override
    public void onLost(@NonNull Network network) {
        Log.i(LOG_TAG, "Disconnected from " + network);
        this.connected.postValue(false);
    }
}
