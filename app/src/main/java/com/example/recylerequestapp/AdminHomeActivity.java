package com.example.recylerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recylerequestapp.sharedpref.SharedPrefManager;

public class AdminHomeActivity extends AppCompatActivity {

    Button btnViewRequests, btnManageItems, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home); // this must match your XML filename

        // bind UI elements
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnManageItems = findViewById(R.id.btnManageItems);
        btnLogout = findViewById(R.id.btnLogout);

        btnViewRequests.setOnClickListener(view -> {
            startActivity(new Intent(AdminHomeActivity.this, ViewActivity.class));
        });
        btnManageItems.setOnClickListener(view -> {
            startActivity(new Intent(AdminHomeActivity.this, ManageItemsActivity.class));
        });
//        // ♻️ Manage recyclable items
//        btnManageItems.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminHomeActivity.this, ManageItemsActivity.class);
//                startActivity(intent);
//            }
//        });


    }
    public void adminLogout(View view) {

        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
