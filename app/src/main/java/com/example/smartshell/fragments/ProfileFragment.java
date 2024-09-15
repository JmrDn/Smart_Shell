package com.example.smartshell.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartshell.R;
import com.example.smartshell.Utils.UserCredentials;


public class ProfileFragment extends Fragment {
    TextView name,
            email;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.name_TextView);
        email = view.findViewById(R.id.email_TextView);

        name.setText(UserCredentials.name != null? UserCredentials.name: "");
        email.setText(UserCredentials.email != null? UserCredentials.email:"");

        return view;
    }
}