package com.example.recylerequestapp;

import android.os.Bundle;
import android.view.View;
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

public class AddItemActivity extends AppCompatActivity {

    EditText etName, etPrice;
    Button btnSave;
    RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etName = findViewById(R.id.etItemName);
        etPrice = findViewById(R.id.etItemPrice);
        btnSave = findViewById(R.id.btnSaveItem);

        requestService = ApiUtils.getRequestService();

        btnSave.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);

            Item item = new Item(name, price);

            String token = new SharedPrefManager(this).getUser().getToken();
            requestService.addItem(token, item).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddItemActivity.this, "Item added!", Toast.LENGTH_SHORT).show();
                        finish(); // go back
                    } else {
                        Toast.makeText(AddItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    Toast.makeText(AddItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
