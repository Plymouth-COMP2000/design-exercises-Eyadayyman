package com.example.eyadcomp2000;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ChangeReservationActivity extends AppCompatActivity {

    private EditText editDate, editTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guestportal4);

        // Link the EditText fields from your XML
        editDate = findViewById(R.id.edit_reservation_date);
        editTime = findViewById(R.id.edit_reservation_time);

        // Link the "Save" button to the logic
        findViewById(R.id.btn_save_changes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        String newDate = editDate.getText().toString().trim();
        String newTime = editTime.getText().toString().trim();

        if (newDate.isEmpty() || newTime.isEmpty()) {
            Toast.makeText(this, "Please enter new date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ask for name to identify which reservation to change
        showIdentityDialog(newDate, newTime, false);
    }

    public void cancelReservation(View view) {
        // Ask for name to identify which reservation to remove
        showIdentityDialog("", "", true);
    }

    private void showIdentityDialog(String date, String time, boolean isCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Identity");
        builder.setMessage("Enter the name used for the booking:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String searchName = input.getText().toString().toLowerCase().trim();
            boolean actionDone = false;

            for (int i = 0; i < MenuData.reservationsList.size(); i++) {
                if (MenuData.reservationsList.get(i).toLowerCase().contains(searchName)) {
                    if (isCancel) {
                        MenuData.deleteReservation(i);
                        Toast.makeText(this, "Reservation Cancelled", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update the entry in the shared list
                        String updatedDetails = searchName + " | " + date + " @ " + time + " (Updated)";
                        MenuData.reservationsList.set(i, updatedDetails);
                        Toast.makeText(this, "Details Updated!", Toast.LENGTH_SHORT).show();
                    }
                    actionDone = true;
                    break;
                }
            }

            if (actionDone) {
                finish(); // Return to previous screen
            } else {
                Toast.makeText(this, "Reservation not found for this name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}