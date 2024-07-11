package com.example.quanlichitieu.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.activity.AddTypeActivity;
import com.example.quanlichitieu.fragment.TablayoutFragment.ExpenseFragment;
import com.example.quanlichitieu.fragment.TablayoutFragment.IncomeFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CustomDialog extends DialogFragment implements View.OnClickListener {
    private Button btAddModal;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.categories_dialog, null);

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutModal);
        ViewPager viewPager = view.findViewById(R.id.viewPagerModal);
        btAddModal = view.findViewById(R.id.btAddModal);
        btAddModal.setOnClickListener(this);

        // Thiết lập Adapter cho ViewPager
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ExpenseFragment(), "Chi Tiêu");
        adapter.addFragment(new IncomeFragment(), "Thu Nhập");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        builder.setView(view)
                .setTitle("Thêm phân loại")
                .setNegativeButton("Đóng", (dialogInterface, i) -> dismiss());
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_border);
        }
        return dialog;
    }

    @Override
    public void onClick(View view) {
        if(view == btAddModal) {
            Intent intent= new Intent(getActivity(), AddTypeActivity.class);
            startActivity(intent);
        }
    }

    // Tạo PagerAdapter để quản lý các Fragment
    static class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
