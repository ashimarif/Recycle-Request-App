package com.example.recylerequestapp.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    /**
     * Return instance of retrofit
     * @param URL REST API URL
     * @return retrofit instance
     */
    public static Retrofit getClient(String URL) {

        if (retrofit == null) {
            // Enable HTTP Logging for Retrofit
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // Initialize retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(client) // <-- use the logging client
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
