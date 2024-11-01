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

import com.example.smartshell.Utils.DateAndTimeUtils;
import com.example.smartshell.Utils.UserCredentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Signup extends AppCompatActivity {
    TextView userIdentifier_TV;
    ImageView backBtn;
    TextView yesAccBtn;
    AppCompatEditText
            email,
            name,
            password,
            confirmPassword;

    ProgressBar progressBar;
    AppCompatButton signupBtn;
    private boolean passwordVisible;
    private boolean confirmPasswordVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initWidgets();
        setUpUser();
        setUpButtons();
        setUpSignUp();
        passwordHideMethod();
    }

    private void setUpSignUp() {


        signupBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            signupBtn.setVisibility(View.GONE);

            String EMAIL = email.getText().toString();
            String NAME = name.getText().toString();
            String PASSWORD = password.getText().toString();
            String CONFIRM_PASSWORD = confirmPassword.getText().toString();

            if (EMAIL.isEmpty()){
                email.setError("This field need to be filled");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()){
                email.setError("Enter valid email address");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (NAME.isEmpty()){
                name.setError("This field need to be filled");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (PASSWORD.isEmpty()){
                password.setError("This field need to be filled");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (CONFIRM_PASSWORD.isEmpty()){
                confirmPassword.setError("This field need to be filled");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else if (!PASSWORD.equals(CONFIRM_PASSWORD)){
                password.setError("Password not match");
                confirmPassword.setError("Password not match");
                progressBar.setVisibility(View.GONE);
                signupBtn.setVisibility(View.VISIBLE);
            }
            else {
                registerUser(EMAIL, NAME, CONFIRM_PASSWORD);
            }
        });
    }

    private void registerUser(String email, String name, String password) {


        if (UserCredentials.userType.equals("admin")){
            FirebaseFirestore.getInstance().collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty() && querySnapshot != null){
                                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                        String userType = documentSnapshot.getString("accountType");
                                        if (userType.toLowerCase().equals("admin")) UserCredentials.isAdminExist = true;
                                    }

                                    checkIfAdminAlreadyExist(email,  name, password);
                                }
                            } else {
                                Log.d("TAG", "Failed to fetch users");
                            }
                        }
                    });

        } else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                saveUser(email, name,password);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                signupBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Failed to create account, Please try again later", Toast.LENGTH_LONG).show();
                                Log.d("TAG", "Failed to create accout: " + task.getException().getMessage());
                            }
                        }
                    });
        }

    }

    private void checkIfAdminAlreadyExist(String email, String name, String password) {
        Log.d("TAG", "is admin:" + UserCredentials.isAdminExist);
        if (UserCredentials.isAdminExist){
            Toast.makeText(getApplicationContext(), "Admin already exist", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            signupBtn.setVisibility(View.VISIBLE);
        } else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                saveUser(email, name,password);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                signupBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Failed to create account, Please try again later", Toast.LENGTH_LONG).show();
                                Log.d("TAG", "Failed to create account: " + task.getException().getMessage());
                            }
                        }
                    });
        }
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

        confirmPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= confirmPassword.getRight()-confirmPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = confirmPassword.getSelectionEnd();
                        if (confirmPasswordVisible){
                            //set drawable image here
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            confirmPasswordVisible = false;
                        }
                        else {

                            //set drawable image here
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            confirmPasswordVisible = true;

                        }
                        confirmPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }
    private void saveUser(String email, String name, String password) {
        String userId = FirebaseAuth.getInstance().getUid();
        HashMap<String, Object> users = new HashMap<>();
        users.put("accountType", !UserCredentials.userType.isEmpty()? UserCredentials.userType : "user");
        users.put("email", email);
        users.put("name", name);
        users.put("password", password);
        users.put("dateCreated", DateAndTimeUtils.getTime24HrsFormatAndDate());
        users.put("userId", userId);
        users.put("isBlocked", false);

        FirebaseFirestore.getInstance().collection("users").document(userId)
                .set(users)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Account created successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomePage.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to create account, Please try again later", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "Failed to save data of user up creating account: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void initWidgets() {
        userIdentifier_TV = findViewById(R.id.userIdentifier_TextView);
        backBtn = findViewById(R.id.back_ImageView);
        yesAccBtn = findViewById(R.id.yesAccount_TextView);

        email = findViewById(R.id.email_Edittext);
        name = findViewById(R.id.name_Edittext);
        password = findViewById(R.id.password_Edittext);
        confirmPassword = findViewById(R.id.confirmPassword_Edittext);
        progressBar = findViewById(R.id.progressbar);
        signupBtn = findViewById(R.id.signup_Button);
    }
    private void setUpButtons() {
        Intent intentGet = getIntent();
        String user = intentGet.getStringExtra("user_identifier");

        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.putExtra("user_identifier", user);

        backBtn.setOnClickListener(v->{startActivity(intent);});
        yesAccBtn.setOnClickListener(v->{
            startActivity(intent);
        });
    }

    private void setUpUser() {
        Intent intent = getIntent();
        String user = intent.getStringExtra("user_identifier");

        if(!user.equals("admin")) userIdentifier_TV.setVisibility(View.INVISIBLE);
        else userIdentifier_TV.setVisibility(View.VISIBLE);
    }
}