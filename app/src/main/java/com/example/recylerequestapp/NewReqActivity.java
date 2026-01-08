package com.example.recylerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recylerequestapp.adapter.RecycleItemAdapter;
import com.example.recylerequestapp.model.Item;
import com.example.recylerequestapp.model.User;
import com.example.recylerequestapp.remote.ApiUtils;
import com.example.recylerequestapp.remote.RequestService;
import com.example.recylerequestapp.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewReqActivity extends AppCompatActivity {


    private RecyclerView rvItemList;
    private RecycleItemAdapter adapter;
    private RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_req);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.SubmitRequestPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView ItemList
        rvItemList = findViewById(R.id.recyclerViewItems);

        //fetch items
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get book service instance
        requestService = ApiUtils.getRequestService();

        // execute the call. send the user token when sending the query
        requestService.getAllItems(token).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of book object from response
                    List<Item> items = response.body();

                    // initialize adapter
                    adapter = new RecycleItemAdapter(getApplicationContext(), items, new RecycleItemAdapter.OnItemSelectListener() {
                        @Override
                        public void onItemSelect(Item item) {
                            Intent intent = new Intent(NewReqActivity.this, CreateReqActivity.class);
                            intent.putExtra("item_id", item.getItem_id());
                            Log.d("DEBUG", "Selected item_id: " + item.getItem_id());
                            startActivity(intent);
                        }
                    });

                    // set adapter to the RecyclerView
                    rvItemList.setAdapter(adapter);

                    // set layout to recycler view
                    rvItemList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvItemList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvItemList.addItemDecoration(dividerItemDecoration);
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }
    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    public void homeClicked(View view) {
        // Example: go back to HomeActivity
        Intent intent = new Intent(this, UserHomeActivity.class);
        startActivity(intent);
        finish(); // optional
    }

}
