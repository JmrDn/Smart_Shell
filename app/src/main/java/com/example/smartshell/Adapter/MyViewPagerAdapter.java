package com.example.smartshell.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartshell.fragments.AllUserFragment;
import com.example.smartshell.fragments.BlockedUsersFragment;
import com.example.smartshell.fragments.UsersFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull UsersFragment fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
            default:
                return new AllUserFragment();
            case 1:
                return new BlockedUsersFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
