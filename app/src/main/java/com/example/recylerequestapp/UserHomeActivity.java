package com.example.recylerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recylerequestapp.model.Request;
import com.example.recylerequestapp.model.User;
import com.example.recylerequestapp.remote.ApiUtils;
import com.example.recylerequestapp.remote.RequestService;
import com.example.recylerequestapp.sharedpref.SharedPrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity {

    private TextView tvUserName, tvTotalPoints, tvLatestItem, tvLatestStatus, tvLatestPrice, tvLatestWeight, tvLatestDate;
    private TextView tvCompletedCount, tvPendingCount;

    private Button btnLogout, btnSubmitRequest, btnMyRequests;
    private RecyclerView recyclerRewards;

    private SharedPrefManager spm;
    private RequestService requestService;
    private int userPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        requestService = ApiUtils.getRequestService();

        // Bind views
        tvUserName = findViewById(R.id.tvWelcome);
        tvLatestItem = findViewById(R.id.tvLatestItem);
        tvLatestStatus = findViewById(R.id.tvLatestStatus);
        tvLatestPrice = findViewById(R.id.tvLatestPrice);
        tvLatestWeight = findViewById(R.id.tvLatestWeight);
        tvLatestDate = findViewById(R.id.tvLatestDate);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvPendingCount = findViewById(R.id.tvPendingCount);

        btnLogout = findViewById(R.id.btnLogout);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnMyRequests = findViewById(R.id.btnMyRequests);

        if (user != null && user.getName() != null && !user.getName().isEmpty()) {
            tvUserName.setText("Welcome, " + user.getName() + " 👋");
        } else {
            tvUserName.setText("Welcome 👋");
        }

        btnSubmitRequest.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, NewReqActivity.class)));

        btnMyRequests.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, RequestHistoryActivity.class)));

        btnLogout.setOnClickListener(v -> {
            spm.logout();
            Toast.makeText(this, "You have successfully logged out.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        fetchRequestData(user);
    }

    private void fetchRequestData(User user) {
        if (user == null || user.getToken() == null) return;

        requestService.getAllUserRequests(user.getToken(), user.getId()).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Request> requests = response.body();

                    int completed = 0;
                    int pending = 0;

                    userPoints = 0;
                    for (Request req : requests) {


                        if ("Completed".equalsIgnoreCase(req.getStatus())) {
                            completed++;
                        } else if ("Pending".equalsIgnoreCase(req.getStatus())) {
                            pending++;
                        }
                    }


                    tvCompletedCount.setText("✅ Completed: " + completed);
                    tvPendingCount.setText("🕒 Pending: " + pending);

                    // Show latest request
                    if (!requests.isEmpty()) {
                        Collections.sort(requests, (r1, r2) -> r2.getRequest_date().compareTo(r1.getRequest_date()));
                        Request latest = requests.get(0);

                        tvLatestItem.setText("Item: " + (latest.getItem() != null ? latest.getItem().getItem_name() : "-"));
                        tvLatestStatus.setText("Status: " + latest.getStatus());
                        tvLatestPrice.setText("Price: RM " + latest.getTotal_price());
                        tvLatestWeight.setText("Weight: " + latest.getWeight() + " kg");

                        // Format date to: 21 July 2025
                        String dateFormatted = formatDate(latest.getRequest_date());
                        tvLatestDate.setText("Date: " + dateFormatted);
                    }


                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(UserHomeActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String formatDate(String originalDate) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
            return sdfOutput.format(sdfInput.parse(originalDate));
        } catch (ParseException e) {
            return originalDate;
        }
    }
}
