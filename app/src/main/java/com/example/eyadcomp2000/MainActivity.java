package com.example.eyadcomp2000;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Added for debugging
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final String STUDENT_ID = "10812553";
    private final String BASE_URL = "http://10.240.72.69/comp2000/coursework/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // --- STAFF LOGIN POPUP ---
    public void openStaffPopup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Staff Login");

        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(v);

        final EditText etUser = v.findViewById(R.id.login_username);
        final EditText etPass = v.findViewById(R.id.login_password);

        builder.setPositiveButton("Login", (dialog, which) -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if (user.equals("admin") && pass.equals("1234")) {
                Intent intent = new Intent(this, StaffMenuActivity.class);
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
            } else {
                checkStaffDatabase(user, pass);
            }
        });

        builder.setNeutralButton("Register Staff", (dialog, which) -> openStaffSignupPopup());
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void checkStaffDatabase(String inputUser, String inputPass) {
        String url = BASE_URL + "read_all_users/" + STUDENT_ID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray users = response.getJSONArray("users");
                        boolean authenticated = false;

                        for (int i = 0; i < users.length(); i++) {
                            JSONObject userObj = users.getJSONObject(i);
                            if (userObj.getString("username").equals(inputUser) &&
                                    userObj.getString("password").equals(inputPass) &&
                                    userObj.getString("usertype").equals("staff")) {
                                authenticated = true;
                                break;
                            }
                        }

                        if (authenticated) {
                            Toast.makeText(this, "Welcome " + inputUser, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, StaffMenuActivity.class);
                            intent.putExtra("IS_ADMIN", false);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Invalid Staff Credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Database error: check format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // This error means your phone can't see the server
                    Toast.makeText(this, "Server unreachable. Are you on the right Wi-Fi?", Toast.LENGTH_LONG).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    // --- STAFF SIGNUP POPUP ---
    public void openStaffSignupPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Staff Registration");

        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(v);

        final EditText etStaffID = v.findViewById(R.id.login_username);
        final EditText etPass = v.findViewById(R.id.login_password);

        etStaffID.setHint("Staff ID (Username)");
        etPass.setHint("Password");

        builder.setPositiveButton("Create Account", (dialog, which) -> {
            String id = etStaffID.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            if (!id.isEmpty() && !pass.isEmpty()) {
                registerStaff(id, pass);
            }
        });
        builder.setNegativeButton("Back", null);
        builder.show();
    }

    private void registerStaff(String staffId, String password) {
        String url = BASE_URL + "create_user/" + STUDENT_ID;
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", staffId);
            postData.put("password", password);
            postData.put("firstname", "Staff");
            postData.put("lastname", "Member");
            postData.put("email", staffId + "@restaurant.com");
            postData.put("contact", STUDENT_ID);
            postData.put("usertype", "staff");
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> Toast.makeText(this, "Staff Account Created!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    // --- GUEST POPUP LOGIC ---
    public void openGuestPopup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_signup, null);
        builder.setView(v);

        final EditText etUser = v.findViewById(R.id.signup_username);
        final EditText etPass = v.findViewById(R.id.signup_password);
        final EditText etEmail = v.findViewById(R.id.signup_email);

        builder.setPositiveButton("Register", (dialog, which) -> {
            registerUser(etUser.getText().toString().trim(),
                    etPass.getText().toString().trim(),
                    etEmail.getText().toString().trim());
        });

        builder.setNeutralButton("Sign In", (dialog, which) -> {
            // Updated to use LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void registerUser(String user, String pass, String email) {
        String url = BASE_URL + "create_user/" + STUDENT_ID;
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", user);
            postData.put("password", pass);
            postData.put("firstname", "Guest");
            postData.put("lastname", "User");
            postData.put("email", email);
            postData.put("contact", STUDENT_ID);
            postData.put("usertype", "guest");
        } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    Toast.makeText(this, "Success! Please Login.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                },
                error -> Toast.makeText(this, "Error: Check Connection", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}