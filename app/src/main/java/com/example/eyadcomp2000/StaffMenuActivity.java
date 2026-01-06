package com.example.eyadcomp2000;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Button btnReadOnlyMenu, btnAddEditMenu, btnViewComplaints;
    private Button btnViewStaff, btnDeleteStaff, btnViewGuests, btnDeleteGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffportal2);

        // Link UI Elements
        notificationBadge = findViewById(R.id.notification_badge);
        btnReadOnlyMenu = findViewById(R.id.btn_read_only_menu);
        btnAddEditMenu = findViewById(R.id.add_edit_items_button);
        btnViewComplaints = findViewById(R.id.comments_button);
        btnViewStaff = findViewById(R.id.btn_view_staff);
        btnDeleteStaff = findViewById(R.id.btn_delete_staff);
        btnViewGuests = findViewById(R.id.btn_view_guests);
        btnDeleteGuest = findViewById(R.id.btn_delete_guest);

        // Role-Based Logic
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        if (isAdmin) {
            // ADMIN ACCESS: Can Edit Menu and Manage Users
            btnReadOnlyMenu.setVisibility(View.GONE);
            btnAddEditMenu.setVisibility(View.VISIBLE);
            btnViewComplaints.setVisibility(View.VISIBLE);
            btnViewStaff.setVisibility(View.VISIBLE);
            btnDeleteStaff.setVisibility(View.VISIBLE);
            btnViewGuests.setVisibility(View.VISIBLE);
            btnDeleteGuest.setVisibility(View.VISIBLE);
        } else {
            // STAFF ACCESS: View-Only Menu (Like Guest)
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
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        int count = MenuData.notificationCount;
        if (count > 0) {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(String.valueOf(count));
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    public void handleNotificationClick(View view) {
        if (MenuData.notificationCount > 0) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("New Alerts");
            String[] opts = {"Guest Comments (" + MenuData.customerComplaints.size() + ")",
                    "New Reservations (" + MenuData.reservationsList.size() + ")"};
            b.setItems(opts, (d, w) -> {
                if (w == 0) showComplaints(view);
                else openReservationsPage(view);
            });
            b.show();
        } else {
            Toast.makeText(this, "No alerts", Toast.LENGTH_SHORT).show();
        }
    }

    public void openReadOnlyMenu(View view) {
        // Directing to the guest's read-only view
        startActivity(new Intent(this, RestaurantMenuActivity.class));
    }

    public void openAddEditMenu(View view) {
        // Directing to the admin's management view
        startActivity(new Intent(this, MenuItemsListActivity.class));
    }

    public void openReservationsPage(View view) {
        startActivity(new Intent(this, ReservationsActivity.class));
    }

    public void showComplaints(View view) {
        if (MenuData.customerComplaints.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        for (String c : MenuData.customerComplaints) sb.append("• ").append(c).append("\n\n");
        new AlertDialog.Builder(this).setTitle("Guest Comments").setMessage(sb.toString())
                .setPositiveButton("Close", (d, w) -> { MenuData.notificationCount = 0; updateNotificationBadge(); })
                .setNeutralButton("Clear All", (d, w) -> {
                    MenuData.customerComplaints.clear();
                    MenuData.notificationCount = 0;
                    updateNotificationBadge();
                }).show();
    }

    public void viewStaffList(View view) { fetchUsers("staff", "Staff List"); }
    public void viewGuestList(View view) { fetchUsers("guest", "Guest List"); }

    private void fetchUsers(String type, String title) {
        String url = BASE_URL + "read_all_users/" + STUDENT_ID;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, res -> {
            try {
                JSONArray arr = res.getJSONArray("users");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject u = arr.getJSONObject(i);
                    if (u.getString("usertype").equals(type)) sb.append("• ").append(u.getString("username")).append("\n");
                }
                new AlertDialog.Builder(this).setTitle(title).setMessage(sb.toString()).setPositiveButton("OK", null).show();
            } catch (JSONException e) { e.printStackTrace(); }
        }, null);
        Volley.newRequestQueue(this).add(req);
    }

    public void openDeleteStaffDialog(View view) { deleteUserDialog("Enter Staff Username"); }
    public void openDeleteGuestDialog(View view) { deleteUserDialog("Enter Guest Username"); }

    private void deleteUserDialog(String hint) {
        final EditText input = new EditText(this);
        input.setHint(hint);
        new AlertDialog.Builder(this).setTitle("Delete User").setView(input)
                .setPositiveButton("Delete", (d, w) -> {
                    String user = input.getText().toString();
                    String url = BASE_URL + "delete_user/" + STUDENT_ID + "/" + user;
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, url, null,
                            r -> Toast.makeText(this, "Deleted: " + user, Toast.LENGTH_SHORT).show(), null);
                    Volley.newRequestQueue(this).add(req);
                }).setNegativeButton("Cancel", null).show();
    }

    public void showEmployeeGuide(View view) {
        new AlertDialog.Builder(this).setTitle("Employee Guide").setMessage("1. Professionalism\n2. Safety first").setPositiveButton("OK", null).show();
    }
}