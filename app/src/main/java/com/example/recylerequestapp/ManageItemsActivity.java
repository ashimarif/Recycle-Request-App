package com.example.recylerequestapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recylerequestapp.adapter.ItemAdapter;
import com.example.recylerequestapp.model.Item;
import com.example.recylerequestapp.model.User;
import com.example.recylerequestapp.remote.ApiUtils;
import com.example.recylerequestapp.remote.RequestService;
import com.example.recylerequestapp.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestService requestService;
    private ItemAdapter adapter;
    private List<Item> itemList = new ArrayList<>();
    private SharedPrefManager spm;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);

        recyclerView = findViewById(R.id.recyclerItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spm = new SharedPrefManager(this);
        User user = spm.getUser();

        if (user == null || user.getToken() == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        token = user.getToken();
        requestService = ApiUtils.getRequestService();

        fetchItems();

        Button btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(ManageItemsActivity.this, AddItemActivity.class);
            startActivity(intent);
        });
    }

    private void fetchItems() {
        requestService.getAllItems(token).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    itemList = response.body();
                    adapter = new ItemAdapter(itemList,
                            item -> {
                                // Edit button click
                                Intent intent = new Intent(ManageItemsActivity.this, EditItemActivity.class);
                                intent.putExtra("item", item);
                                startActivity(intent);
                            },
                            item -> {
                                // Delete button click
                                showDeleteConfirmation(item);
                            }
                    );
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ManageItemsActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(ManageItemsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation(Item item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete \"" + item.getItem_name() + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> deleteItem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem(Item item) {
        requestService.deleteItem(token, item.getItem_id()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageItemsActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    itemList.remove(item);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ManageItemsActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageItemsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
