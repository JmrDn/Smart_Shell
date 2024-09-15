package com.example.smartshell.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartshell.Adapter.MyViewPagerAdapter;
import com.example.smartshell.R;
import com.google.android.material.tabs.TabLayout;


public class UsersFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager2;

    MyViewPagerAdapter myViewPagerAdapter;

   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        initWidgets(view);
        setUpViewPager();
        return view;
    }

    private void setUpViewPager() {
        myViewPagerAdapter = new MyViewPagerAdapter(this);
        viewPager2.setAdapter(myViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    private void initWidgets(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager2);
    }
}