package com.example.eyadcomp2000;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookTableActivity extends AppCompatActivity {

    private EditText inputName, inputTime, inputDate, inputPartySize, inputPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Links to your tableportal3.xml
        setContentView(R.layout.tableportal3);

        // Initialize all input fields
        inputName = findViewById(R.id.input_name);
        inputTime = findViewById(R.id.input_time);
        inputDate = findViewById(R.id.input_date);
        inputPartySize = findViewById(R.id.input_party_size);
        inputPhone = findViewById(R.id.input_phone);

        // Set the "Book Now" button logic
        findViewById(R.id.btn_book_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBooking();
            }
        });
    }

    private void handleBooking() {
        // Convert inputs to strings
        String name = inputName.getText().toString().trim();
        String time = inputTime.getText().toString().trim();
        String date = inputDate.getText().toString().trim();
        String party = inputPartySize.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();

        // Basic validation: ensure required fields aren't empty
        if (name.isEmpty() || time.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required (*) fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format the reservation details for the staff list
        String fullReservation = name + " | " + date + " @ " + time + " (Size: " + party + ") - " + phone;

        // Add to the shared static list in MenuData
        MenuData.addReservation(fullReservation);

        Toast.makeText(this, "Booking Successful! See you soon.", Toast.LENGTH_LONG).show();

        // Close the page and return to Guest Portal
        finish();
    }

    // Back button logic
    public void goBackToGuest(View view) {
        finish();
    }
}