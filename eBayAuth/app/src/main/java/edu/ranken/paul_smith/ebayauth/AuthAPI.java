package edu.ranken.paul_smith.ebayauth;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class AuthAPI {
    private static final String LOG_TAG = "AuthAPI";

    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;
    private final Retrofit retrofit;
    private final AuthService service;

    public AuthAPI(AuthEnvironment env, String clientId, String clientSecret) {
        this(env.baseUrl, clientId, clientSecret);
    }

    public AuthAPI(String baseUrl, String clientId, String clientSecret) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        // Create retrofit context
        retrofit =
            new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create service from interface
        service = retrofit.create(AuthService.class);
    }

    private Call<AuthResponse> authenticate() {

        // Base64 encode credentials
        Charset utf8 = StandardCharsets.UTF_8;
        String credentialString = clientId + ":" + clientSecret;
        byte[] credentialBytes = credentialString.getBytes(utf8);
        String credentialBase64 = Base64.encodeToString(credentialBytes, Base64.NO_WRAP);

        // Assemble request data
        String authorization = "Basic " + credentialBase64;
        String grantType = "client_credentials";
        String scope = "https://api.ebay.com/oauth/api_scope";

        // Create call
        return service.authenticate(authorization, grantType, scope);
    }

    public AuthResponse authenticateSync() throws AuthException {
        Call<AuthResponse> call = authenticate();
        try {
            Response<AuthResponse> response = call.execute();
            AuthResponse authResponse = handleResponse(call, response);
            return authResponse;
        } catch (AuthException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthException(ex.getMessage(), ex);
        }
    }

    public Call<AuthResponse> authenticateAsync(AuthCallback callback, AuthFailureCallback callbackFail) {
        Call<AuthResponse> call = authenticate();
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Log.i(LOG_TAG, "onResponse()");
                try {
                    AuthResponse authResponse = handleResponse(call, response);
                    callback.onAuth(authResponse);
                } catch (AuthException ex) {
                    callbackFail.onAuthFailure(ex);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure()");
                callbackFail.onAuthFailure(new AuthException(t.getMessage(), t));
            }
        });
        return call;
    }

    private AuthResponse handleResponse(Call<AuthResponse> call, Response<AuthResponse> response) throws AuthException {
        if (response.isSuccessful()) {
            return response.body();
        } else {
            String errorMessage = "status: " + response.code();
            Gson gson = new Gson();
            if (response.errorBody() != null) {
                AuthFailureResponse responseBody = gson.fromJson(response.errorBody().charStream(), AuthFailureResponse.class);
                if (responseBody != null && responseBody.error != null) {
                    errorMessage = responseBody.error + ": " + responseBody.error_description;
                }
            }
            throw new AuthException(errorMessage);
        }
    }

    public interface AuthService {
        @POST("/identity/v1/oauth2/token")
        @FormUrlEncoded
        @Headers({
            "Accept: application/json",
            "User-Agent: retrofit/2.9.0"
        })
        Call<AuthResponse> authenticate(
            @Header("Authorization") String authorization,
            @Field("grant_type") String grantType,
            @Field("scope") String scope
        );
    }

    public interface AuthCallback {
        void onAuth(AuthResponse authResponse);
    }

    public interface AuthFailureCallback {
        void onAuthFailure(AuthException ex);
    }

    public static class AuthResponse {
        public String access_token;
        public String token_type;
        public Integer expires_in;
    }

    public static class AuthFailureResponse {
        public String error;
        public String error_description;
    }

    public static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }
        public AuthException(String message, Throwable t) {
            super(message, t);
        }
    }
}

