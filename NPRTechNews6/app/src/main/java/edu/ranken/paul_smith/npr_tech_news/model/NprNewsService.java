package edu.ranken.paul_smith.npr_tech_news.model;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NprNewsService {
    @GET("/1019/feed.json")
    Call<Feed> getFeed();
}
