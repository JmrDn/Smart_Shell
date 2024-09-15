package com.example.smartshell;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartshell.Utils.UserCredentials;

public class DrawerHeaderActivity extends AppCompatActivity {

    TextView userType;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_header);

        userType = findViewById(R.id.userIdentifier_TextView);
        Log.d("TAG", "USING HEADER");

        // Check if UserCredentials.userType is not null and not empty
        String user = UserCredentials.userType != null ? UserCredentials.userType : "";

        Log.d("TAG", "User type: " + user);

        if (!user.isEmpty()) {
            if (user.equals("user")) {
                userType.setVisibility(View.INVISIBLE);
                Log.d("TAG", "User is a regular user, hiding the userType TextView");
            } else {
                userType.setVisibility(View.VISIBLE);  // Make sure it's visible
                userType.setText(user);
                Log.d("TAG", "User type set to: " + user);
            }
        } else {
            userType.setVisibility(View.INVISIBLE);
            Log.d("TAG", "User type is empty or null, hiding the userType TextView");
        }


    }




}
