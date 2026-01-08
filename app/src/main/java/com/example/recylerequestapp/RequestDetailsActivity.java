package com.example.recylerequestapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recylerequestapp.model.Request;

public class RequestDetailsActivity extends AppCompatActivity {

    private TextView tvRequestDate, tvItemName, tvAddress, tvNotes, tvWeight, tvTotalPrice, tvTotalPoints, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details_main);

        Request request = (Request) getIntent().getSerializableExtra("request");

        if (request == null) {
            Toast.makeText(this, "Request details missing!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Bind views
        tvRequestDate = findViewById(R.id.tvRequestDate);
        tvItemName = findViewById(R.id.tvItemName);
        tvAddress = findViewById(R.id.tvAddress);
        tvNotes = findViewById(R.id.tvNotes);
        tvWeight = findViewById(R.id.tvWeight);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvStatus = findViewById(R.id.tvStatus);

        // Set data
        tvRequestDate.setText("Date: " + request.getRequest_date());
        tvItemName.setText(request.getItem().getItem_name());
        tvAddress.setText("Address: " + request.getAddress());
        tvNotes.setText("Notes: " + request.getNotes());
        tvWeight.setText("Weight: " + request.getWeight() + " kg");
        tvTotalPrice.setText("Total Price: RM " + request.getTotal_price());
        tvStatus.setText("Status: " + request.getStatus());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}
