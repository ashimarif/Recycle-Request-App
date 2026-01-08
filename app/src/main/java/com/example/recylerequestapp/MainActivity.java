package com.example.recylerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recylerequestapp.model.User;
import com.example.recylerequestapp.sharedpref.SharedPrefManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());

            if (spm.isLoggedIn()) {
                User user = spm.getUser();

                if (user.getRole().equalsIgnoreCase("admin")) {
                    // If admin, go to admin screen
                    Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                } else {
                    // If normal user, go to user screen
                    Intent intent = new Intent(MainActivity.this, UserHomeActivity.class);
                    startActivity(intent);
                }
            } else {
                // Not logged in yet
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            finish();
        }, 2000); // splash delay
    }
}