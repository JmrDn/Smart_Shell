package com.example.smartshell;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartshell.Utils.UserCredentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Login extends AppCompatActivity {
    TextView userIdentifier_TV;
    ImageView backBtn;
    TextView noAccBtn;
    AppCompatButton loginBtn;
    AppCompatEditText
            email,
            password;

    ProgressBar progressBar;

    private boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initWidgets();
        setUpUser();
        setUpButtons();
        passwordHideMethod();
    }


    private void setUpButtons() {
        backBtn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), ChooseUser.class));
        });
        noAccBtn.setOnClickListener(v->{
            Intent intentGet = getIntent();
            String user = intentGet.getStringExtra("user_identifier");

            Intent intent = new Intent(getApplicationContext(), Signup.class);
            intent.putExtra("user_identifier", user);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);

            String EMAIL = email.getText().toString();
            String PASSWORD = password.getText().toString();

            if (EMAIL.isEmpty()){
                email.setError("Enter email");

                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);

            } else if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()){
                email.setError("Enter valid email");

                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
            } else if (PASSWORD.isEmpty()){
                password.setError("Enter password");

                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
            } else {
                signInUser(EMAIL, PASSWORD);
            }



        });
    }

    private void signInUser(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_LONG).show();
                            saveUsersDataToSystem(email);
                            Intent intent = new Intent(getApplicationContext(), HomePage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to log in " + task.getException().getMessage(), Toast.LENGTH_LONG).show();;
                            progressBar.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void passwordHideMethod() {
        password.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = password.getSelectionEnd();
                        if (passwordVisible){
                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else {

                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;

                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void saveUsersDataToSystem(String email) {

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
    }

    private void setUpUser() {
        Intent intent = getIntent();
        String user = intent.getStringExtra("user_identifier");

        if(!user.equals("admin")) userIdentifier_TV.setVisibility(View.INVISIBLE);
        else userIdentifier_TV.setVisibility(View.VISIBLE);
    }

    private void initWidgets() {
        userIdentifier_TV = findViewById(R.id.userIdentifier_TextView);
        backBtn = findViewById(R.id.back_ImageView);
        noAccBtn = findViewById(R.id.dontHaveAccount_TextView);
        loginBtn = findViewById(R.id.login_Button);

        email = findViewById(R.id.email_Edittext);
        password = findViewById(R.id.password_Edittext);
        progressBar = findViewById(R.id.progressbar);

    }

    @Override
    protected void onStart() {
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
                                                UserCredentials.userType = userType;

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


        }
        super.onStart();
    }
}