package com.example.quanlichitieu.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.model.User;

public class SettingDialog extends DialogFragment implements View.OnClickListener {
    private LinearLayout linearLoinhac,linearProfile,linearChangePass;
    private User u;

    public static SettingDialog newInstance(User u) {

        Bundle args = new Bundle();
        SettingDialog fragment = new SettingDialog();
        args.putSerializable("user", u);
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.setting_dialog, null);
        linearLoinhac = view.findViewById(R.id.linearLoinhac);
        linearProfile = view.findViewById(R.id.linearProfile);
        linearChangePass = view.findViewById(R.id.linearChangePass);
        linearChangePass.setOnClickListener(this);
        linearLoinhac.setOnClickListener(this);
        linearProfile.setOnClickListener(this);
        if (getArguments() != null) {
            u = (User) getArguments().getSerializable("user");
        }
        builder.setView(view)
                .setTitle("Cài đặt");
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_border);
        }
        return dialog;
    }
    @Override
    public void onClick(View view) {
        if(view ==linearProfile) {
            ProfileDialog profileDialog = ProfileDialog.newInstance(u);
            profileDialog.show(getActivity().getSupportFragmentManager(), "ProfileDialog");
        } else if(view == linearLoinhac) {
            NotificationDialog notificationDialog = new NotificationDialog();
            notificationDialog.show(getActivity().getSupportFragmentManager(),"NotificationDialog");
        } else if(view ==linearChangePass){
            ChangePassDialog changePassDialog = new ChangePassDialog();
            changePassDialog.show(getActivity().getSupportFragmentManager(),"ChangePassDialog");
        }
    }
}
