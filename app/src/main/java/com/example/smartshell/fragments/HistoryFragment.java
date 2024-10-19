package com.example.smartshell.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartshell.Adapter.HistoryAdapter;
import com.example.smartshell.Model.HarvestDatesModel;
import com.example.smartshell.Model.HistoryModel;
import com.example.smartshell.R;
import com.example.smartshell.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;
    HistoryAdapter adapter;
    ArrayList<HistoryModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initWidgets(view);
        setUpRecyclerView();
        return  view;
    }

    private void setUpRecyclerView() {
        list = new ArrayList<>();
        adapter = new HistoryAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore.getInstance().collection("history")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                list.clear();
                                for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                    String waterSalinity = documentSnapshot.getString("waterSalinity");
                                    String dateAndTime = documentSnapshot.getString("dateAndTime");
                                    dateAndTime = DateAndTimeUtils.convertLocalDateAndTimeString(dateAndTime);
                                    list.add(new HistoryModel(waterSalinity, dateAndTime));
                                }

                                if (adapter != null) adapter.notifyDataSetChanged();
                                list.sort(new Comparator<HistoryModel>() {

                                    @Override
                                    public int compare(HistoryModel o1, HistoryModel o2) {

                                        String oneDateAndTimeString = o1.getDateAndTime();
                                        String twoDateAndTimeString = o2.getDateAndTime();


                                        // Define the same pattern used for formatting
                                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy h:mm a");

                                        // Convert the string back to LocalDateTime
                                        LocalDateTime oneDateTime = LocalDateTime.parse(oneDateAndTimeString, dateTimeFormatter);
                                        LocalDateTime twoDateTime = LocalDateTime.parse(twoDateAndTimeString, dateTimeFormatter);
                                        return oneDateTime.compareTo(twoDateTime);
                                    }
                                });
                            } else {
                                Log.d("TAG", "Query snapshot of history is null");
                            }
                        } else {
                            Log.d("TAG", "task is not successfull");
                        }
                    }
                });
    }

    private void initWidgets(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
    }
}