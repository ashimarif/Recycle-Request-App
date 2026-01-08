package com.example.recylerequestapp.remote;

public class ApiUtils {
    // REST API server URL
    public static final String BASE_URL = "http://178.128.220.20/2023379531/api/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
    public static RequestService getRequestService() {
        return RetrofitClient.getClient(BASE_URL).create(RequestService.class);
    }

    // ADD MORE SERVICE BELOW
}
