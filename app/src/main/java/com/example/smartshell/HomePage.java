package com.example.smartshell;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartshell.Utils.UserCredentials;
import com.example.smartshell.fragments.HarvestFragment;
import com.example.smartshell.fragments.HistoryFragment;
import com.example.smartshell.fragments.HomeFragment;
import com.example.smartshell.fragments.ProfileFragment;
import com.example.smartshell.fragments.UsersFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
@RequiresApi(api = Build.VERSION_CODES.O)
public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private final  String value = "Home";
    private MenuItem usersItem;
    TextView userTypeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initWidgets();
        setUpDrawer();
        setUpUser();
        setUpAdminInterface();
        setUpDefaultNavigation();
        Log.d("TAG", "Is home");
    }

    private void setUpUser() {
        String user = UserCredentials.userType;
        if (!user.isEmpty()){
            if (user.equals("user")){
                userTypeTV.setVisibility(View.INVISIBLE);
                usersItem.setVisible(false);
            } else {
                usersItem.setVisible(true);
                userTypeTV.setText(user);
            }

        } else {

            userTypeTV.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpAdminInterface() {

    }

    private void setUpDefaultNavigation() {
        Fragment selectedFragment = null;
        if (value.equals("Home")){
            selectedFragment = new HomeFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
    }

    private void initWidgets() {
        drawerLayout = findViewById(R.id.HomePage_Drawer);
        navigationView = findViewById(R.id.HomePage_Nav_View);
        toolbar = findViewById(R.id.toolbar);
        usersItem = navigationView.getMenu().findItem(R.id.users);
        userTypeTV = findViewById(R.id.userType_TextView);
    }
    private void setUpDrawer() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();


        Menu menu = navigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(false);
        }
        item.setChecked(true);

        if (itemId == R.id.home){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

            selectedFragment = new HomeFragment();
        }
        else if (itemId == R.id.profile){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);
            selectedFragment = new ProfileFragment();
        }
        else if (itemId == R.id.harvest){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);
            selectedFragment = new HarvestFragment();
        }
        else if (itemId == R.id.history){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);
            selectedFragment = new HistoryFragment();
        }
        else if (itemId == R.id.users){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);
            selectedFragment = new UsersFragment();
        }
        else if (itemId == R.id.logout){
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Successfully log out", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), ChooseUser.class));
                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

        }

        if (selectedFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}