package com.example.smartshell.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment {

    TextView harvestDate, numOfDays, days;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initWidgets(view);
        setUpDate();
        setUpUpcomingHarvestDate();

        return  view;
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
    }
}