package com.example.recylerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.recylerequestapp.R;
import com.example.recylerequestapp.model.FailLogin;
import com.example.recylerequestapp.model.User;
import com.example.recylerequestapp.remote.ApiUtils;
import com.example.recylerequestapp.remote.UserService;
import com.example.recylerequestapp.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // get references to form elements
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);

    }


    public void loginClicked(View view) {

        // get username and password entered by user
        String username = email.getText().toString();
        String userPass= password.getText().toString();

        // validate form, make sure it is not empty
        if (validateLogin(username, userPass)) {
            // if not empty, login using REST API
            doLogin(username, userPass);

        }

    }

    /**
     * Call REST API to login
     *
     * @param username username
     * @param password password
     */
    private void doLogin(String username, String password) {

        // get UserService instance
        UserService userService = ApiUtils.getUserService();

        // prepare the REST API call using the service interface
        if(checkemail(username)){
            // prepare the REST API call using the service interface
            Call<User> call = userService.loginEmail(username, password);

            // execute the REST API call
            call.enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call call, Response response) {

                    if (response.isSuccessful()) {  // code 200
                        // parse response to POJO
                        User user = (User) response.body();
                        if (user != null && user.getToken() != null) {
                            // successful login. server replies a token value
                            displayToast("Login successful");
                            displayToast("Token: " + user.getToken());
                            // store value in Shared Preferences
                            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                            spm.storeUser(user);
                            // forward user to MainActivity
                            if (user.getRole().equalsIgnoreCase("admin")) {
                                startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                            } else {
                                startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                            }

                            finish();

                        } else {
                            // server return success but no user info replied
                            displayToast("Login error");
                        }
                    } else {  // other than 200
                        // try to parse the response to FailLogin POJO
                        String errorResp = null;
                        try {
                            errorResp = response.errorBody().string();
                            FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                            displayToast(e.getError().getMessage());
                        } catch (Exception e) {
                            Log.e("MyApp:", e.toString()); // print error details to error log
                            displayToast("Error");
                        }
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    displayToast("Error connecting to server.");
                    displayToast(t.getMessage());
                    Log.e("MyApp:", t.toString()); // print error details to error log
                }
            });}
        else {
            Call<User> call = userService.login(username, password);

            // execute the REST API call
            call.enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call call, Response response) {

                    if (response.isSuccessful()) {  // code 200
                        // parse response to POJO
                        User user = (User) response.body();
                        if (user != null && user.getToken() != null) {
                            // successful login. server replies a token value
                            displayToast("Login successful");
                            displayToast("Token: " + user.getToken());
                            // store value in Shared Preferences
                            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                            spm.storeUser(user);
                            // forward user to MainActivity
                            if (user.getRole().equalsIgnoreCase("Admin")) {
                                startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                            } else {
                                startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                            }
                            finish();

                        } else {
                            // server return success but no user info replied
                            displayToast("Login error");
                        }
                    } else {  // other than 200
                        // try to parse the response to FailLogin POJO
                        String errorResp = null;
                        try {
                            errorResp = response.errorBody().string();
                            FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                            displayToast(e.getError().getMessage());
                        } catch (Exception e) {
                            Log.e("MyApp:", e.toString()); // print error details to error log
                            displayToast("Error");
                        }
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    displayToast("Error connecting to server.");
                    displayToast(t.getMessage());
                    Log.e("MyApp:", t.toString()); // print error details to error log
                }
            });
        }
    }
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    private boolean checkemail(String username){
        for(int i=0; i<username.length();i++){
            char letters = username.charAt(i);
            if (letters == '@'){
                return true;
            }
        }
        return false;
    }
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public void registerClicked(View view) {
        // forward user to BookListActivity
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}