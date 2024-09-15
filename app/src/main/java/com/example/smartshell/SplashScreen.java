package com.example.smartshell;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.smartshell.Utils.UserCredentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
                    FirebaseFirestore.getInstance().collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty() && querySnapshot != null){
                                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                                String emailFromDB = documentSnapshot.getString("email");
                                                if (!emailFromDB.isEmpty()){
                                                    if (email.equals(emailFromDB)){
                                                        String userType =  documentSnapshot.getString("accountType");
                                                        String name = documentSnapshot.getString("name");
                                                        UserCredentials.userType = userType;
                                                        UserCredentials.email = email;
                                                        UserCredentials.name = name;
                                                        finish();
                                                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                                                    }
                                                } else {
                                                    Log.d("TAG", "Email from db during fetch users data is empty");
                                                }
                                            }
                                        }
                                    } else {
                                        Log.d("TAG", "Failed to fetch users " + task.getException().getMessage());
                                    }
                                }
                            });
                } else {
                    startActivity(new Intent(getApplicationContext(), ChooseUser.class));
                }

            }
        },3000);
    }
}