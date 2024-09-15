package com.example.smartshell.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartshell.Model.HarvestDatesModel;
import com.example.smartshell.R;
import com.example.smartshell.fragments.HarvestFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HarvestDatesAdapter extends RecyclerView.Adapter<HarvestDatesAdapter.MyViewHolder> {
    Context context;
    ArrayList<HarvestDatesModel> list;
    HarvestFragment harvestFragment;


    public HarvestDatesAdapter(Context context, ArrayList<HarvestDatesModel> list, HarvestFragment harvestFragment) {
        this.context = context;
        this.list = list;
        this.harvestFragment = harvestFragment;
    }

    @NonNull
    @Override
    public HarvestDatesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.harvest_date_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HarvestDatesAdapter.MyViewHolder holder, int position) {
        HarvestDatesModel model = list.get(position);
        holder.startDateTV.setText(model.getStartDate());
        holder.endDateTV.setText(model.getEndDate());
        holder.numOfDaysTV.setText(String.valueOf(model.getRemainingDays()));
        if (model.getRemainingDays() > 1){
            holder.daysTV.setText("DAYS");
        } else {
            holder.daysTV.setVisibility(View.GONE);
            holder.numOfDaysTV.setTextSize(40);
            holder.numOfDaysTV.setText("NOW");
        }
        final String docId = model.getDocId();
        holder.deleteBtn.setOnClickListener(v->{
            showDeleteDialog(docId);
        });
    }

    private void showDeleteDialog(String docId) {
        Dialog deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.setCancelable(false);
        deleteDialog.show();

        AppCompatButton deleteBtn = deleteDialog.findViewById(R.id.delete_Button);
        AppCompatButton cancelBtn = deleteDialog.findViewById(R.id.cancel_Button);

        deleteBtn.setOnClickListener(v->{
            FirebaseFirestore.getInstance().collection("harvest")
                    .document(docId)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_LONG).show();
                                harvestFragment.setUpRecyclerView();
                                deleteDialog.dismiss();
                            } else {
                                Toast.makeText(context, "Failed to delete", Toast.LENGTH_LONG).show();
                                deleteDialog.dismiss();
                            }
                        }
                    });
        });

        cancelBtn.setOnClickListener(v->{deleteDialog.dismiss();});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView startDateTV,endDateTV, numOfDaysTV, daysTV;
        ImageView deleteBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            startDateTV = itemView.findViewById(R.id.startDate_TextView);
            endDateTV = itemView.findViewById(R.id.endDate_TextView);
            numOfDaysTV = itemView.findViewById(R.id.numOfDays_TextView);
            daysTV = itemView.findViewById(R.id.days_TextView);
            deleteBtn = itemView.findViewById(R.id.delete_ImageView);
        }
    }
}
