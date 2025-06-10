package ru.mirea.artemovnp.mireaproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey,
            @Query("pageSize") int pageSize
    );
}