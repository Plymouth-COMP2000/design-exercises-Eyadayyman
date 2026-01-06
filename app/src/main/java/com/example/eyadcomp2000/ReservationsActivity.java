package com.example.eyadcomp2000;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ReservationsActivity extends AppCompatActivity {

    private ArrayAdapter<String> plannedAdapter;
    private ArrayAdapter<String> ongoingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Links to the layout with two side-by-side lists
        setContentView(R.layout.staffportal3);

        // CLEAR NOTIFICATIONS: Reset the badge count as the staff is now viewing the data
        MenuData.notificationCount = 0;

        // 1. Setup Planned List (Left side - Guest bookings)
        ListView plannedListView = findViewById(R.id.planned_list_view);
        plannedAdapter = new ArrayAdapter<>(this, R.layout.white_text_item, MenuData.reservationsList);
        plannedListView.setAdapter(plannedAdapter);

        // 2. Setup Ongoing List (Right side - Seated guests)
        ListView ongoingListView = findViewById(R.id.ongoing_list_view);
        ongoingAdapter = new ArrayAdapter<>(this, R.layout.white_text_item, MenuData.ongoingReservationsList);
        ongoingListView.setAdapter(ongoingAdapter);

        // LOGIC: Click a 'Planned' reservation to seat the guest (Move to Ongoing)
        plannedListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < MenuData.reservationsList.size()) {
                String item = MenuData.reservationsList.get(position);

                // Move from one shared static list to the other
                MenuData.reservationsList.remove(position);
                MenuData.ongoingReservationsList.add(item + " (Seated)");

                // Refresh both adapters to update the UI
                plannedAdapter.notifyDataSetChanged();
                ongoingAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Guest moved to Ongoing (Seated)", Toast.LENGTH_SHORT).show();
            }
        });

        // LOGIC: Click an 'Ongoing' reservation to complete/remove it
        ongoingListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < MenuData.ongoingReservationsList.size()) {
                MenuData.ongoingReservationsList.remove(position);
                ongoingAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Table Cleared/Reservation Finished", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Navigation helper to return to the Staff Dashboard.
     */
    public void goBackToMenu(View view) {
        finish();
    }

    /**
     * Secondary exit method for consistency with XML onClick definitions.
     */
    public void goBackToStaffMenu(View view) {
        finish();
    }
}