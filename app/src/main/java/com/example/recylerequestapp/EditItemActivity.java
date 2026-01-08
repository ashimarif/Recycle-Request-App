package com.example.recylerequestapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recylerequestapp.model.Item;
import com.example.recylerequestapp.remote.ApiUtils;
import com.example.recylerequestapp.remote.RequestService;
import com.example.recylerequestapp.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditItemActivity extends AppCompatActivity {

    EditText etItemName, etItemPrice;
    Button btnUpdateItem;
    Item currentItem;
    RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item); // You can reuse this layout

        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);
        btnUpdateItem = findViewById(R.id.btnSaveItem);
        btnUpdateItem.setText("Update Item");

        currentItem = (Item) getIntent().getSerializableExtra("item");
        if (currentItem != null) {
            etItemName.setText(currentItem.getItem_name());
            etItemPrice.setText(String.valueOf(currentItem.getPrice_per_kg()));
        }

        requestService = ApiUtils.getRequestService();

        btnUpdateItem.setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            double price = Double.parseDouble(etItemPrice.getText().toString().trim());
            currentItem.setItem_name(name);
            currentItem.setPrice_per_kg(price);

            String token = new SharedPrefManager(this).getUser().getToken();
            requestService.updateItem(token, currentItem.getItem_id(), currentItem).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditItemActivity.this, "Item updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditItemActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    Toast.makeText(EditItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
