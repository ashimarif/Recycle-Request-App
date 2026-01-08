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

public class ViewActivity extends AppCompatActivity {

    private RecyclerView rvAdminRequests;
    private RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Toolbar toolbar = findViewById(R.id.adminToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Requests");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvAdminRequests = findViewById(R.id.recyclerAdminRequests);
        rvAdminRequests.setLayoutManager(new LinearLayoutManager(this));
        rvAdminRequests.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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

        requestService.getAllRequests(user.getToken()).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful()) {
                    List<Request> requests = response.body();

                    RequestHistoryAdapter adapter = new RequestHistoryAdapter(requests, (view, request) -> {
                        PopupMenu popup = new PopupMenu(ViewActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.admin_request_context_menu, popup.getMenu());

                        popup.setOnMenuItemClickListener(menuItem -> {
                            if (menuItem.getItemId() == R.id.menu_mark_completed) {
                                updateStatus(request.getRequest_id(), "Completed");
                            } else if (menuItem.getItemId() == R.id.menu_mark_pending) {
                                updateStatus(request.getRequest_id(), "Pending");
                            } else if (menuItem.getItemId() == R.id.menu_details) {
                                Intent intent = new Intent(ViewActivity.this, RequestDetailsActivity.class);
                                intent.putExtra("request", request);
                                startActivity(intent);
                            }
                            return true;
                        });

                        popup.show();
                    });

                    rvAdminRequests.setAdapter(adapter);
                } else {
                    Toast.makeText(ViewActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                    Log.e("ADMIN_VIEW", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(ViewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(int requestId, String status) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String token = spm.getUser().getToken();

        requestService.updateAccept(token, requestId, status).enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ViewActivity.this, "Status updated to " + status, Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(ViewActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                Toast.makeText(ViewActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
