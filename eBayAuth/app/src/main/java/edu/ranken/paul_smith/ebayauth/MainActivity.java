package edu.ranken.paul_smith.ebayauth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    // constants
    private static final String LOG_TAG = "MainActivity";

    // views
    private TextView outputText;

    // auth
    private AuthAPI authAPI;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        outputText = findViewById(R.id.outputText);

        // request auth token
        authAPI = new AuthAPI(AuthEnvironment.SANDBOX, "PaulSmit-explorer-SBX-0ac47d6f0-43658d8b", "SBX-ac47d6f02acb-50dd-4842-8ce4-a622");
        authAPI.authenticateAsync(
            (authResponse) -> {
                Log.i(LOG_TAG, "Authenticated.");
                this.authToken = authResponse.access_token;
                outputText.setText(this.authToken);
                Snackbar.make(outputText, R.string.messageAuthenticated, Snackbar.LENGTH_SHORT).show();
            },
            (ex) -> {
                Log.e(LOG_TAG, "Auth Error.", ex);
                outputText.setText(R.string.errorAuth);
                Snackbar.make(outputText, R.string.errorAuth, Snackbar.LENGTH_SHORT).show();
            }
        );
    }
}