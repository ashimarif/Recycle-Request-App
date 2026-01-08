package com.example.recylerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recylerequestapp.adapter.RequestHistoryAdapter;
import com.example.recylerequestapp.model.Request;
import com.example.recylerequestapp.model.User;
import com.example.recylerequestapp.remote.ApiUtils;
import com.example.recylerequestapp.remote.RequestService;
import com.example.recylerequestapp.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_history);

        Toolbar toolbar = findViewById(R.id.requestToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Requests");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        rvHistory = findViewById(R.id.recyclerRequestHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        if (user == null || user.getToken() == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            spm.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        requestService = ApiUtils.getRequestService();

        // ✅ Load requests
        requestService.getAllUserRequests(user.getToken(), user.getId())
                .enqueue(new Callback<List<Request>>() {
                    @Override
                    public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                        if (response.isSuccessful()) {
                            List<Request> requests = response.body();

                            RequestHistoryAdapter adapter = new RequestHistoryAdapter(requests, (view, request) -> {
                                PopupMenu popup = new PopupMenu(RequestHistoryActivity.this, view);
                                popup.getMenuInflater().inflate(R.menu.request_history_context_menu, popup.getMenu());

                                popup.setOnMenuItemClickListener(menuItem -> {
                                    if (menuItem.getItemId() == R.id.menu_details) {
                                        Intent intent = new Intent(RequestHistoryActivity.this, RequestDetailsActivity.class);
                                        intent.putExtra("request", request);
                                        startActivity(intent);
                                    } else if (menuItem.getItemId() == R.id.menu_cancel) {
                                        if ("Pending".equalsIgnoreCase(request.getStatus())) {
                                            cancelRequest(request.getRequest_id());
                                        } else {
                                            Toast.makeText(RequestHistoryActivity.this, "Only pending requests can be cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    return true;
                                });

                                popup.show();
                            });

                            rvHistory.setAdapter(adapter);
                        } else {
                            Toast.makeText(RequestHistoryActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                            Log.e("MyApp", "Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Request>> call, Throwable t) {
                        Toast.makeText(RequestHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("MyApp", "Failure: " + t.toString());
                    }
                });
    }



    private void cancelRequest(int requestId) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String token = spm.getUser().getToken();

        requestService.deleteRequest(token, requestId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RequestHistoryActivity.this, "Request cancelled", Toast.LENGTH_SHORT).show();
                    recreate(); // Refresh the activity
                } else {
                    Toast.makeText(RequestHistoryActivity.this, "Failed to cancel: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RequestHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Back button behavior
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
