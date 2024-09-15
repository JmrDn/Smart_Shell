package com.example.smartshell.Adapter;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartshell.Model.AllUsersModel;
import com.example.smartshell.Model.BlockedUsersModel;
import com.example.smartshell.R;
import com.example.smartshell.fragments.AllUserFragment;
import com.example.smartshell.fragments.BlockedUsersFragment;
import com.example.smartshell.fragments.UsersFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.MyViewHolder> {
    Context context;
    ArrayList<BlockedUsersModel> list;


    public BlockedUsersAdapter(Context context, ArrayList<BlockedUsersModel> list){
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public BlockedUsersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blocker_user_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedUsersAdapter.MyViewHolder holder, int position) {
        BlockedUsersModel model = list.get(position);
        holder.email.setText(model.getEmail());
        holder.name.setText(model.getName());
        final String name = model.getName();
        final String email = model.getEmail();
        final String userId = model.getUserId();
        
        holder.addBtn.setOnClickListener(v->{showAddDialog(name,email,userId);});
        
    }

    @SuppressLint("SetTextI18n")
    private void showAddDialog(String name, String email, String userId) {
        Dialog addUserDialog = new Dialog(context);
        addUserDialog.setContentView(R.layout.add_dialog_layout);
        addUserDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addUserDialog.setCancelable(false);
        addUserDialog.show();

        AppCompatButton yesBtn, noBtn;
        TextView nameOfUserBlock = addUserDialog.findViewById(R.id.blockUserName_Textview);
        yesBtn = addUserDialog.findViewById(R.id.yes_Button);
        noBtn = addUserDialog.findViewById(R.id.no_Button);

        nameOfUserBlock.setText("Are you sure you want \nto unblock " + name + " ?");

        HashMap<String, Object> updateBlockedUser = new HashMap<>();
        updateBlockedUser.put("isBlocked", false);

        yesBtn.setOnClickListener(v->{
            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .update(updateBlockedUser)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(context, "User " + name + " successfully unblocked", Toast.LENGTH_LONG).show();



                                if (context instanceof AppCompatActivity) {
                                    // Get the FragmentManager and replace the fragment
                                    Fragment fragment1 = new UsersFragment();
                                    ((AppCompatActivity) context).getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, fragment1)
                                            .commit();
                                }

                                addUserDialog.dismiss();
                            } else {
                                Toast.makeText(context, "Failed to unblock User " + name, Toast.LENGTH_LONG).show();
                                Log.d("TAG", "failed to block user " + name);
                                addUserDialog.dismiss();
                            }
                        }
                    });
        });
        noBtn.setOnClickListener(v->{addUserDialog.dismiss();});

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView email,name;
        ImageView addBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.email_TextView);
            name = itemView.findViewById(R.id.name_TextView);
            addBtn = itemView.findViewById(R.id.add_ImageView);
        }
    }
}
