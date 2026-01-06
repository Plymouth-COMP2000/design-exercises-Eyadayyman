package com.example.eyadcomp2000;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private final String STUDENT_ID = "10812553";
    private final String BASE_URL = "http://10.240.72.69/comp2000/coursework/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Called when the Login button is clicked.
     * Fetches the password from the API and verifies it.
     */
    public void handleLogin(View view) {
        EditText etUser = findViewById(R.id.login_username);
        EditText etPass = findViewById(R.id.login_password);

        String username = etUser.getText().toString().trim();
        String passwordEntered = etPass.getText().toString().trim();

        if (username.isEmpty() || passwordEntered.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "read_user/" + STUDENT_ID + "/" + username;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject userObj = response.getJSONObject("user");
                        String dbPassword = userObj.getString("password");

                        if (dbPassword.equals(passwordEntered)) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            // MOVE to the Guest Portal
                            startActivity(new Intent(this, guestactivity.class));
                            finish(); // Closes Login so user can't "Go Back" to it
                        } else {
                            Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "User not found. Please register first.", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}