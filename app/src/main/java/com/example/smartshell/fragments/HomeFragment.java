package com.example.smartshell.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartshell.Model.HarvestDatesModel;
import com.example.smartshell.R;
import com.example.smartshell.Utils.DateAndTimeUtils;
import com.example.smartshell.Utils.HarvestUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment {

    TextView harvestDate;
    TextView numOfDays;
    TextView  days;
    TextView currentTime;
    TextView currentWaterSalinity;
    private Handler handler = new Handler();
    private Runnable runnable;
    AppCompatButton seeHistoryBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initWidgets(view);
        setUpDate();
        setUpUpcomingHarvestDate();
        setUpCurrentSalinityDetails();
        seeHistoryBtn.setOnClickListener(v->{onViewHistory();});

        runnable = new Runnable() {
            @Override
            public void run() {
                updateTime();

                handler.postDelayed(this, 60000);
            }
        };
        handler.post(runnable);
        return  view;
    }

    private void onViewHistory() {
        // In your current Fragment (e.g., FragmentA)
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Create an instance of the fragment you want to navigate to (e.g., FragmentB)
        Fragment newFragment = new HistoryFragment();

        // Replace the current fragment with the new one, and optionally add the transaction to the back stack
        transaction.replace(R.id.container, newFragment); // R.id.fragment_container is the container in the activity's layout where fragments are shown
        transaction.addToBackStack(null); // Add this transaction to the back stack (optional)
        transaction.commit();
    }

    private void updateTime() {

        currentTime.setText("As of " + DateAndTimeUtils.getTimeWithAMAndPM());
    }

    private int getSalinityNumber(int max, int min){
        return (int) (Math.random() * (max - min + 1)) + min;
    }
    private void setUpCurrentSalinityDetails() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Readings");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentSalinityFromDb = snapshot.child("Water").child("salinityStatus").getValue().toString();
                int salinityNumber = 0;
                if (currentSalinityFromDb.trim().toLowerCase().equals("below optimal")){
                    salinityNumber = getSalinityNumber(0,27);
                } else if (currentSalinityFromDb.trim().toLowerCase().equals("optimal")){
                    salinityNumber = getSalinityNumber(27, 35);
                } else if (currentSalinityFromDb.trim().toLowerCase().equals("above optimal")){
                    salinityNumber = getSalinityNumber(35, 50);
                }

                currentWaterSalinity.setText(currentSalinityFromDb + "- " + salinityNumber + " PPT");

                setHistory(currentSalinityFromDb);
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch current water salinity");
            }
        });
    }

    private void setHistory(String currentSalinityFromDb) {

        HashMap<String, Object> history = new HashMap();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:s");
        String currentDateAndTime = now.format(dateTimeFormatter);

        history.put("waterSalinity", currentSalinityFromDb);
        history.put("dateAndTime", currentDateAndTime);

        validateHistory(history);
    }

    private void validateHistory(HashMap<String, Object> history) {

        FirebaseFirestore.getInstance().collection("history")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty() && querySnapshot != null){
                                long recentHistoryTimeRange = 99999;
                                String recentWaterSalinity = "";
                                int querySize = querySnapshot.size();
                                int queries = 0;
                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    String dateAndTime = documentSnapshot.getString("dateAndTime");
                                    queries++;
                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy H:m:s");
                                    LocalDateTime dateTime = LocalDateTime.parse(dateAndTime, dateTimeFormatter);
                                    long fetchedHistoryTimeRange = DateAndTimeUtils.calculateMinutesAgo(dateTime);

                                    if(fetchedHistoryTimeRange < recentHistoryTimeRange){
                                        String waterSalinityFromHistory = documentSnapshot.getString("waterSalinity");
                                        if (!history.get("waterSalinity").equals(waterSalinityFromHistory)){
                                            recentWaterSalinity = waterSalinityFromHistory;
                                        }
                                    }
                                }

                                if (queries == querySize){
                                    if(!recentWaterSalinity.isEmpty() && recentWaterSalinity != null){
                                        saveHistory(history);
                                    }
                                }
                            } else {
                                Log.d("TAG", "Query snapshot of history is null");
                                saveHistory(history);
                            }
                        } else {
                            Log.d("TAG", "Fetching history is failed");
                        }
                    }
                });

    }

    private void saveHistory(HashMap<String, Object> history) {
        FirebaseFirestore.getInstance().collection("history")
                .add(history)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            Log.d("TAG", "New water salinity history added");
                        } else {
                            Log.d("TAG", "Failed to add water salinity history");
                        }
                    }
                });
    }

    private void setUpUpcomingHarvestDate() {

        FirebaseFirestore.getInstance().collection("harvest")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    String endDate = documentSnapshot.getString("endDate");
                                    String docId = documentSnapshot.getString("docId");
                                    long remainingDaysFromDB = DateAndTimeUtils.getRemainingDays(endDate);

                                    if (remainingDaysFromDB < HarvestUtils.remainingDays){
                                        HarvestUtils.remainingDays = remainingDaysFromDB;
                                        HarvestUtils.date = endDate;
                                        HarvestUtils.docId = docId;
                                    }

                                }

                                setUpDate();

                                Log.d("TAG", "Upcoming harvest date: " + HarvestUtils.date);


                            }
                        } else {
                            Log.d("TAG", "Failed to fetch harvest");
                        }
                    }
                });
    }

    private void setUpDate() {
        harvestDate.setText(HarvestUtils.date);
        currentTime.setText(DateAndTimeUtils.getTimeWithAMAndPM());
        numOfDays.setText(String.valueOf(HarvestUtils.remainingDays));
        if (HarvestUtils.remainingDays > 1){
            days.setText("DAYS");
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) numOfDays.getLayoutParams();

            // Set the top margin (in pixels)
            params.topMargin = 0;
            days.setVisibility(View.GONE);
            numOfDays.setTextSize(35);
            numOfDays.setText("NOW");
        }

    }


    private void initWidgets(View view) {
        numOfDays = view.findViewById(R.id.numOfDays_TextView);
        days = view.findViewById(R.id.days_TextView);
        harvestDate = view.findViewById(R.id.dateOfHarvest_TextView);

        currentTime = view.findViewById(R.id.timeOfWaterSalinity_TextView);
        currentWaterSalinity = view.findViewById(R.id.waterSalinity_TextView);
        seeHistoryBtn = view.findViewById(R.id.waterSalinityHistory_Button);
    }
}