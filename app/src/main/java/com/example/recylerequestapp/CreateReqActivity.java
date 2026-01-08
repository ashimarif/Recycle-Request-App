package com.example.recylerequestapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.recylerequestapp.model.Item;
import com.example.recylerequestapp.model.Request;
import com.example.recylerequestapp.model.User;
import com.example.recylerequestapp.remote.ApiUtils;
import com.example.recylerequestapp.remote.RequestService;
import com.example.recylerequestapp.sharedpref.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReqActivity extends AppCompatActivity {

    private RequestService requestService;
    private Item item;
    private static TextView tvRequestDate;
    private static Date requestAtDate;
    private String itemName;
    private double itemPointsPerKg;

    public static class RequestDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            return new DatePickerDialog(getActivity(), this,
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            requestAtDate = new GregorianCalendar(year, month, day).getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            tvRequestDate.setText(sdf.format(requestAtDate));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_req);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.RequestItemLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int itemId = getIntent().getIntExtra("item_id", -1);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        requestService = ApiUtils.getRequestService();

        requestService.getItem(token, itemId).enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                if (response.code() == 200) {
                    item = response.body();
                    itemName = item.getItem_name(); // ✅ assign item name
                    TextView tvItemName = findViewById(R.id.tvItemName);
                    TextView tvPrice = findViewById(R.id.tvItemPrice);

                    tvRequestDate = findViewById(R.id.tvdatePicker);

                    tvItemName.setText(item.getItem_name());
                    tvPrice.setText(String.format("RM %.2f/kg", item.getPrice_per_kg()));


                    requestAtDate = new Date();
                    tvRequestDate.setText(sdf.format(requestAtDate));

                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    public void showRequestDatePickerDialog(View v) {
        DialogFragment newFragment = new RequestDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void requestcancelClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), NewReqActivity.class);
        startActivity(intent);
    }

    public void addNewRequest(View v) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        int itemId = item.getItem_id();
        int userId = user.getId();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String rDate = sdf.format(requestAtDate);

        EditText etWeight = findViewById(R.id.etItemWeight);
        EditText etAddress = findViewById(R.id.etAddressField);
        EditText etNotes = findViewById(R.id.etNoteField);

        String address = etAddress.getText().toString();
        String notes = etNotes.getText().toString();

        double weight;
        try {
            weight = Double.parseDouble(etWeight.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid weight input", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalprice = item.getPrice_per_kg() * weight;

        // ✅ Create Request object
        Request newRequest = new Request(
                itemId,
                userId,
                weight,
                address,
                notes,
                rDate,
                totalprice);

        // ✅ Call API with @Body request
        Call<Request> call = requestService.addRequest(user.getToken(), newRequest);

        call.enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Successfully created a new request!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), NewReqActivity.class));
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error [" + t.getMessage() + "]", Toast.LENGTH_LONG).show();
                Log.d("MyApp:", "Error: " + t.toString());
            }
        });
    }

    public void CheckTotalClicked(View v) {
        EditText etWeight = findViewById(R.id.etItemWeight);
        TextView tvTotalPrice = findViewById(R.id.tvtotalPrice);

        double weight;
        try {
            weight = Double.parseDouble(etWeight.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid weight input", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalprice = item.getPrice_per_kg() * weight;

        // ✅ Add this debug log right here:

        tvTotalPrice.setText(String.format("RM %.2f", totalprice));
    }


    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
