package com.example.quanlichitieu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.quanlichitieu.fragment.FragmentSearch;
import com.example.quanlichitieu.fragment.FragmentChart;
import com.example.quanlichitieu.fragment.FragmentHome;
import com.example.quanlichitieu.fragment.FragmentSetting;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int numPage=4;
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentHome();
            case 1:
                return new FragmentChart();
            case 2:
                return new FragmentSearch();
            case 3:
                return new FragmentSetting();
        }
        return null;
    }

    @Override
    public int getCount() {
        return numPage;
    }

}
