package com.example.recylerequestapp.remote;

import com.example.recylerequestapp.model.Item;
import com.example.recylerequestapp.model.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestService {

    @GET("recyclable_items")
    Call<List<Item>> getAllItems(@Header("api-key") String api_key);

    @GET("recyclable_items/{item_id}")
    Call<Item> getItem(@Header("api-key") String api_key, @Path("item_id") int id);

    @POST("requests")
    Call<Request> addRequest(
            @Header("api-key") String api_key,
            @Body Request request
    );

    @GET("requests")
    Call<List<Request>> getAllUserRequests(@Header("api-key") String apiKey, @Query("user_id") int userId);


    @GET("requests/?order=request_id&orderType=desc")
    Call<List<Request>> getAllUserRequestsSorted(@Header("api-key") String apiKey, @Query("user_id[e]") int userId);

    @GET("requests/{request_id}")
    Call<Request> getRequest(@Header("api-key") String apiKey, @Path("request_id") int request_id);

    @DELETE("requests/{request_id}")
    Call<Void> deleteRequest(@Header("api-key") String apiKey, @Path("request_id") int request_id);

    @GET("requests")
    Call<List<Request>> getAllRequests(@Header("api-key") String apiKey);

    @GET("requests")
    Call<List<Request>> getPending(@Header("api-key") String apiKey, @Query("status") String status);

    @GET("requests")
    Call<List<Request>> getComplete(@Header("api-key") String apiKey, @Query("status") String status);

    @GET("requests")
    Call<List<Request>> getRejected(@Header("api-key") String apiKey, @Query("status") String status);

    @FormUrlEncoded
    @POST("requests/{request_id}")
    Call<Request> updateAccept(@Header("api-key") String apiKey, @Path("request_id") int request_id, @Field("status") String status);

    @FormUrlEncoded
    @POST("requests/{request_id}")
    Call<Request> updateReject(@Header("api-key") String apiKey, @Path("request_id") int request_id, @Field("status") String status);

    @FormUrlEncoded
    @POST("requests/{request_id}")
    Call<Request> updateRequestStatus(
            @Header("api-key") String apiKey,
            @Path("request_id") int requestId,
            @Field("status") String status
    );

    @POST("recyclable_items")
    Call<Item> addItem(@Header("api-key") String apiKey, @Body Item item);

    @PUT("recyclable_items/{id}")
    Call<Item> updateItem(@Header("api-key") String token, @Path("id") int id, @Body Item item);

    @DELETE("recyclable_items/{id}")
    Call<Void> deleteItem(@Header("api-key") String token, @Path("id") int itemId);

}
