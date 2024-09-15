package com.example.smartshell.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.smartshell.Adapter.AllUserAdapter;
import com.example.smartshell.Adapter.BlockedUsersAdapter;
import com.example.smartshell.Model.AllUsersModel;
import com.example.smartshell.Model.BlockedUsersModel;
import com.example.smartshell.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class BlockedUsersFragment extends Fragment {


    BlockedUsersAdapter adapter;
    ArrayList<BlockedUsersModel> list;
    RecyclerView recyclerView;
    RelativeLayout noDataLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blocked_users, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        noDataLayout = view.findViewById(R.id.noData_Layout);
        setUpRecyclerView();
        return  view;
    }

    public void setUpRecyclerView() {

        list = new ArrayList<>();
        adapter = new BlockedUsersAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
                                    if(userType.toLowerCase().equals("user")){

                                        boolean isBlocked = (boolean) documentSnapshot.get("isBlocked");

                                        if (isBlocked){

                                            String name = documentSnapshot.getString("name");
                                            String email = documentSnapshot.getString("email");
                                            String userId = documentSnapshot.getString("userId");

                                            list.add(new BlockedUsersModel(email, name, userId));
                                        }
                                    }
                                }
                                if (adapter != null){
                                    adapter.notifyDataSetChanged();
                                    if (list.size() == 0 ){
                                        noDataLayout.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    } else {
                                        noDataLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }

                                }


                            }
                        } else {
                            Log.d("TAG", "Failed to fetch user");
                        }
                    }
                });
    }
}