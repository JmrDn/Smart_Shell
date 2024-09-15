package com.example.smartshell.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartshell.Adapter.HarvestDatesAdapter;
import com.example.smartshell.Model.AllUsersModel;
import com.example.smartshell.Model.HarvestDatesModel;
import com.example.smartshell.R;
import com.example.smartshell.Utils.CalendarUtils;
import com.example.smartshell.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HarvestFragment extends Fragment {

    AppCompatButton addDateBtn;
    TextView startDateTV;
    TextView endDateTV;
    Dialog selectDateDialog;
    HarvestDatesAdapter adapter;
    ArrayList<HarvestDatesModel> list;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_harvest, container, false);
        initWidgets(view);
        setUpRecyclerView();
        
        addDateBtn.setOnClickListener(v->{showSelectDateDialog();});
        return  view;
    }

    public void setUpRecyclerView() {
        list = new ArrayList<>();
        adapter = new HarvestDatesAdapter(getContext(), list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore.getInstance().collection("harvest")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    String startDate = documentSnapshot.getString("startDate");
                                    String endDate = documentSnapshot.getString("endDate");
                                    String docId = documentSnapshot.getString("docId");
                                    long remainingDaysFromDB = DateAndTimeUtils.getRemainingDays(endDate);


                                    list.add(new HarvestDatesModel(startDate, endDate,docId, remainingDaysFromDB));

                                }

                                if (adapter != null){
                                    adapter.notifyDataSetChanged();
                                }

                                list.sort(new Comparator<HarvestDatesModel>() {
                                    @Override
                                    public int compare(HarvestDatesModel o1, HarvestDatesModel o2) {
                                        return Long.compare(o1.getRemainingDays(), o2.getRemainingDays());
                                    }
                                });
                            }
                        } else {
                            Log.d("TAG", "Failed to fetch harvest");
                        }
                    }
                });
    }

    private  void  showCancelDialog(){
        Dialog cancelDialog = new Dialog(getContext());
        cancelDialog.setContentView(R.layout.discard_dialog);
        cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cancelDialog.setCancelable(false);
        cancelDialog.show();

        AppCompatButton yesBtn = cancelDialog.findViewById(R.id.yes_Button);
        AppCompatButton noBtn = cancelDialog.findViewById(R.id.no_Button);

        yesBtn.setOnClickListener(v->{
            cancelDialog.dismiss();
            selectDateDialog.dismiss();

            startDateTV.setText("");
            endDateTV.setText("");
        });

        noBtn.setOnClickListener(v->{ cancelDialog.dismiss();});
    }

    private void showSelectDateDialog() {
        selectDateDialog = new Dialog(getContext());
        selectDateDialog.setContentView(R.layout.select_date_dialog);
        selectDateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectDateDialog.setCancelable(false);
        selectDateDialog.show();

        AppCompatButton selectStartDateBtn = selectDateDialog.findViewById(R.id.selectStartDate_Button);
        AppCompatButton selectEndDateBtn = selectDateDialog.findViewById(R.id.selectEndDate_Button);
        AppCompatButton submitBtn = selectDateDialog.findViewById(R.id.submit_Button);
        AppCompatButton cancelBtn = selectDateDialog.findViewById(R.id.cancel_Button);
        startDateTV = selectDateDialog.findViewById(R.id.startDate_TextView);
        endDateTV = selectDateDialog.findViewById(R.id.endDate_TextView);


        selectStartDateBtn.setOnClickListener(v->{showStartDatePickerDialog();});
        selectEndDateBtn.setOnClickListener(v->{showEndDatePickerDialog();});

        cancelBtn.setOnClickListener(v->{
            String startDate = startDateTV.getText().toString();
            String endDate = endDateTV.getText().toString();

            if (startDate.isEmpty() && endDate.isEmpty()){
                selectDateDialog.dismiss();
            } else {
                showCancelDialog();
            }
        });

        submitBtn.setOnClickListener(v->{

            String startDate = startDateTV.getText().toString();
            String endDate = endDateTV.getText().toString();

            if (startDate.isEmpty()){
                Toast.makeText(getContext(), "Start date is empty", Toast.LENGTH_LONG).show();
            } else if (endDate.isEmpty()){
                Toast.makeText(getContext(), "End date is empty", Toast.LENGTH_LONG).show();
            } else {
                submit(startDate, endDate);
            }
        });

    }

    private void submit (String startDate, String endDate){

        String charSet = "abcdefghijklmnopqrstuvwxyz" +
                "1234567890" +
                "_-";
        String randomStr = "";
        Random rand = new Random();
        int length = 15;

        for(int i  = 0; i < length; i++){
            randomStr +=
                    charSet.charAt(rand.nextInt(charSet.length()));
        }
        LocalDate dateToday = LocalDate.now();
        String todayDateId = DateAndTimeUtils.parseDateToDateId(dateToday);

        String docId = todayDateId + "_" + randomStr;
        String dateAndTimeNow = DateAndTimeUtils.getTime24HrsFormatAndDate();
        HashMap<String, Object> newHarvestDate = new HashMap<>();
        newHarvestDate.put("startDate", startDate);
        newHarvestDate.put("endDate", endDate);
        newHarvestDate.put("dateAndTimeSubmitted", dateAndTimeNow);
        newHarvestDate.put("docId", docId);
        newHarvestDate.put("isDone", false);

        FirebaseFirestore.getInstance().collection("harvest").document(docId)
                .set(newHarvestDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(),"Successfully submit", Toast.LENGTH_LONG).show();
                            setUpRecyclerView();
                            selectDateDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Failed to submit, Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void showStartDatePickerDialog() {
        // Show a date picker dialog to select a date
        final Calendar c = Calendar.getInstance();

        if (CalendarUtils.startSelectedDate != null) {
            // Set the custom date as the initial date in the DatePicker
            c.set(CalendarUtils.startSelectedDate.getYear(), CalendarUtils.startSelectedDate.getMonthValue() - 1, CalendarUtils.startSelectedDate.getDayOfMonth());
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        @SuppressLint("SetTextI18n")
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {

            String dateId = String.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(year1 - 1900, monthOfYear, dayOfMonth)));
            CalendarUtils.startSelectedDate = DateAndTimeUtils.getLocalDate(dateId);
            dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.startSelectedDate);
            updateStartDateTextView();

            
        }, year, month, day);

        // Display the date picker dialog
        datePickerDialog.show();

    }
    private void showEndDatePickerDialog() {
        // Show a date picker dialog to select a date
        final Calendar c = Calendar.getInstance();

        if (CalendarUtils.endSelectedDate != null) {
            // Set the custom date as the initial date in the DatePicker
            c.set(CalendarUtils.endSelectedDate.getYear(), CalendarUtils.endSelectedDate.getMonthValue() - 1, CalendarUtils.endSelectedDate.getDayOfMonth());
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        @SuppressLint("SetTextI18n")
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {

            String dateId = String.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(year1 - 1900, monthOfYear, dayOfMonth)));
            CalendarUtils.endSelectedDate = DateAndTimeUtils.getLocalDate(dateId);
            updateEndDateTextView();


        }, year, month, day);

        // Display the date picker dialog
        datePickerDialog.show();

    }

    private void updateStartDateTextView() {
        String dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.startSelectedDate);

        startDateTV.setText(DateAndTimeUtils.convertToDateWordFormat(dateId));
    }
    private void updateEndDateTextView() {
        String dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.endSelectedDate);

        endDateTV.setText(DateAndTimeUtils.convertToDateWordFormat(dateId));
    }



    private void initWidgets(View view) {
        addDateBtn = view.findViewById(R.id.addDate_Button);
        recyclerView = view.findViewById(R.id.recyclerview);
    }
}