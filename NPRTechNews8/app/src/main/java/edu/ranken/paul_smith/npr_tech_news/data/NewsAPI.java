package edu.ranken.paul_smith.npr_tech_news.data;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class NewsAPI {
    private static final String LOG_TAG = "NewsAPI";

    private final Retrofit retrofit;
    private final NewsService service;

    public NewsAPI() {
        // Create retrofit context
        retrofit =
            new Retrofit.Builder()
                .baseUrl("https://feeds.npr.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create service from interface
        service = retrofit.create(NewsService.class);
    }

    public Call<Feed> getFeed() {
        return service.getFeed();
    }

    public interface NewsService {
        @GET("/1019/feed.json")
        Call<Feed> getFeed();
    }

    public static class Feed {
        public String description;
        public FeedAuthor author;
        public List<FeedItem> items;
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
        public String summary;
        public String image;
        public String date_published;
        public FeedAuthor author;
        public List<String> tags;
    }
}
