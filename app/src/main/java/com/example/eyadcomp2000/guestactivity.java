package com.example.eyadcomp2000;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class guestactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This layout loads only after LoginActivity verification
        setContentView(R.layout.activity_guest);
    }

    /**
     * Navigation to the Restaurant Menu.
     */
    public void openRestaurantMenu(View view) {
        Intent intent = new Intent(this, RestaurantMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Navigation to the Table Booking page.
     */
    public void openBookingPage(View view) {
        Intent intent = new Intent(this, BookTableActivity.class);
        startActivity(intent);
    }

    /**
     * Navigation to change or cancel existing reservations.
     */
    public void openReservationPage(View view) {
        Intent intent = new Intent(this, ChangeReservationActivity.class);
        startActivity(intent);
    }

    /**
     * Popup for submitting feedback/complaints.
     * FIXED: Now saves to MenuData and triggers notification count.
     */
    public void openCommentPopup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_comment, null);
        builder.setView(dialogView);

        final EditText commentInput = dialogView.findViewById(R.id.edit_comment_input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String comment = commentInput.getText().toString().trim();
            if (!comment.isEmpty()) {
                // STEP 2: Save the comment to the central static list
                MenuData.addComplaint(comment);

                // Feedback confirmation
                Toast.makeText(this, "Feedback sent to staff!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a comment before submitting.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * Returns to the Main landing page.
     */
    public void goBackToMain(View view) {
        finish(); // Closes this activity
    }
}