package com.example.smartshell.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartshell.Model.HistoryModel;
import com.example.smartshell.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    Context context;
    ArrayList<HistoryModel> list;
    public HistoryAdapter(Context context, ArrayList<HistoryModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {
        HistoryModel model = list.get(position);
        holder.dateAndTimeTV.setText(model.getDateAndTime());
        holder.waterSalinityTV.setText(model.getWaterSalinity());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView waterSalinityTV;
        TextView dateAndTimeTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

        waterSalinityTV = itemView.findViewById(R.id.waterSalinity_TextView);
        dateAndTimeTV = itemView.findViewById(R.id.dateAndTime_TextView);
        }
    }
}
