package com.example.eyadcomp2000;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // For debugging
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StaffMenuActivity extends AppCompatActivity {

    private final String STUDENT_ID = "10812553";
    private final String BASE_URL = "http://10.240.72.69/comp2000/coursework/";

    private TextView notificationBadge;
    private FrameLayout notificationContainer;
    private Button btnReadOnlyMenu, btnAddEditMenu, btnViewComplaints;
    private Button btnViewStaff, btnDeleteStaff, btnViewGuests, btnDeleteGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffportal2);

        // Link Notification UI
        notificationBadge = findViewById(R.id.notification_badge);
        notificationContainer = findViewById(R.id.notification_container);

        // Link Management Buttons - Matched to XML IDs
        btnReadOnlyMenu = findViewById(R.id.btn_read_only_menu);
        btnAddEditMenu = findViewById(R.id.add_edit_items_button);
        btnViewComplaints = findViewById(R.id.comments_button); // XML says comments_button
        btnViewStaff = findViewById(R.id.btn_view_staff);
        btnDeleteStaff = findViewById(R.id.btn_delete_staff);
        btnViewGuests = findViewById(R.id.btn_view_guests);
        btnDeleteGuest = findViewById(R.id.btn_delete_guest);

        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        // Admin Visibility Logic
        if (isAdmin) {
            btnReadOnlyMenu.setVisibility(View.GONE);
            btnAddEditMenu.setVisibility(View.VISIBLE);
            btnViewComplaints.setVisibility(View.VISIBLE);
            btnViewStaff.setVisibility(View.VISIBLE);
            btnDeleteStaff.setVisibility(View.VISIBLE);
            btnViewGuests.setVisibility(View.VISIBLE);
            btnDeleteGuest.setVisibility(View.VISIBLE);
        } else {
            btnReadOnlyMenu.setVisibility(View.VISIBLE);
            btnAddEditMenu.setVisibility(View.GONE);
            btnViewComplaints.setVisibility(View.GONE);
            btnViewStaff.setVisibility(View.GONE);
            btnDeleteStaff.setVisibility(View.GONE);
            btnViewGuests.setVisibility(View.GONE);
            btnDeleteGuest.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Force UI refresh whenever returning to this screen
        updateNotificationBadge();
    }

    // --- NOTIFICATION BADGE CONTROL ---
    private void updateNotificationBadge() {
        int count = MenuData.notificationCount;
        if (count > 0) {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(String.valueOf(count));
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    /**
     * Handles clicking the bell icon.
     * Prioritizes showing complaints, then reservations.
     */
    public void handleNotificationClick(View view) {
        if (!MenuData.customerComplaints.isEmpty()) {
            showComplaints(view);
        } else if (MenuData.notificationCount > 0) {
            // If count exists but complaints list is empty, it's a reservation
            openReservationsPage(view);
        } else {
            Toast.makeText(this, "No new alerts", Toast.LENGTH_SHORT).show();
        }
    }

    // --- BUTTON CLICK METHODS ---

    /**
     * Shows complaints with a Clear All option to manage the static list.
     */
    public void showComplaints(View view) {
        if (MenuData.customerComplaints.isEmpty()) {
            showDialog("Guest Comments", "No complaints recorded.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String c : MenuData.customerComplaints) {
            sb.append("• ").append(c).append("\n\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guest Comments");
        builder.setMessage(sb.toString());

        builder.setPositiveButton("Close", (dialog, which) -> {
            // Reset count as the user has viewed the alerts
            MenuData.notificationCount = 0;
            updateNotificationBadge();
        });

        // Clears the static list and resets the badge
        builder.setNeutralButton("Clear All", (dialog, which) -> {
            MenuData.customerComplaints.clear();
            MenuData.notificationCount = 0;
            updateNotificationBadge();
            Toast.makeText(this, "Comments cleared.", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    public void openReservationsPage(View view) {
        // Reset count when entering reservations activity
        MenuData.notificationCount = 0;
        updateNotificationBadge();
        startActivity(new Intent(this, ReservationsActivity.class));
    }

    public void openAddEditMenu(View view) {
        startActivity(new Intent(this, MenuItemsListActivity.class));
    }

    public void openReadOnlyMenu(View view) {
        startActivity(new Intent(this, MenuItemsListActivity.class));
    }

    // --- ADMIN API TOOLS ---
    public void viewStaffList(View view) { fetchAndDisplayUsers("staff", "Registered Staff"); }
    public void viewGuestList(View view) { fetchAndDisplayUsers("guest", "Registered Guests"); }

    public void openDeleteStaffDialog(View view) {
        showDeleteDialog("Delete Staff", "Enter Staff ID", this::requestDeleteUser);
    }

    public void openDeleteGuestDialog(View view) {
        showDeleteDialog("Delete Guest", "Enter Guest Username", this::requestDeleteUser);
    }

    private void fetchAndDisplayUsers(String type, String title) {
        String url = BASE_URL + "read_all_users/" + STUDENT_ID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray users = response.getJSONArray("users");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject u = users.getJSONObject(i);
                            if (u.getString("usertype").equals(type)) sb.append("• ").append(u.getString("username")).append("\n\n");
                        }
                        showDialog(title, sb.length() > 0 ? sb.toString() : "No accounts found.");
                    } catch (JSONException e) { e.printStackTrace(); }
                }, null);
        Volley.newRequestQueue(this).add(request);
    }

    private void showDeleteDialog(String title, String hint, java.util.function.Consumer<String> action) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);
        final EditText input = new EditText(this);
        input.setHint(hint);
        b.setView(input);
        b.setPositiveButton("Delete", (d, w) -> {
            if (!input.getText().toString().isEmpty()) action.accept(input.getText().toString().trim());
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    private void requestDeleteUser(String username) {
        String url = BASE_URL + "delete_user/" + STUDENT_ID + "/" + username;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, url, null,
                r -> Toast.makeText(this, "Deleted " + username, Toast.LENGTH_SHORT).show(), null);
        Volley.newRequestQueue(this).add(req);
    }

    // --- UTILITIES ---
    public void showEmployeeGuide(View view) {
        showDialog("Employee Guide", "1. Arrive on time.\n2. Wash hands.\n3. Be professional.");
    }

    private void showDialog(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton("Close", null).show();
    }

    public void logoutStaff(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}