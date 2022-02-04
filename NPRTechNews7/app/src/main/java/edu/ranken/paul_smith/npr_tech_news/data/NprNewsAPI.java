package edu.ranken.paul_smith.npr_tech_news.data;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class NprNewsAPI {
    private static final String LOG_TAG = "NprNewsAPI";

    private final Retrofit retrofit;
    private final NprNewsService service;

    public NprNewsAPI() {
        // Create retrofit context
        retrofit =
            new Retrofit.Builder()
                .baseUrl("https://feeds.npr.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create service from interface
        service = retrofit.create(NprNewsService.class);
    }

    public Call<Feed> getFeed() {
        return service.getFeed();
    }

    public interface NprNewsService {
        @GET("/1019/feed.json")
        Call<Feed> getFeed();
    }

    public static class Feed {
        public String description;
        public FeedAuthor author;
        public ArrayList<FeedItem> items;
    }

    public static class FeedAuthor {
        public String name;
        public String url;
        public String avatar;
    }

    public static class FeedItem {
        public String id;
        public String url;
        public String title;
    }
}
