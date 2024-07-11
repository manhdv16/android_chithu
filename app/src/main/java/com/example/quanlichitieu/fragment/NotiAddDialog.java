package com.example.quanlichitieu.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Notification;

import java.util.Calendar;

public class NotiAddDialog extends DialogFragment implements View.OnClickListener {
    private EditText edTen,edNote;
    private Spinner spinner;
    private TextView tvTime;
    private Button btAddNoti;
    private Firebase firebase;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.noti_add_dialog, null);
        initView(view);
        builder.setView(view)
                .setTitle("Thêm nhắc nhở");
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_border);
        }
        return dialog;
    }

    private void initView(View view) {
        edTen = view.findViewById(R.id.edTen);
        edNote = view.findViewById(R.id.edNote);
        spinner = view.findViewById(R.id.spinner);
        tvTime = view.findViewById(R.id.tvTime);
        btAddNoti = view.findViewById(R.id.btAddNoti);
        firebase = new Firebase();
        btAddNoti.setOnClickListener(this);
        tvTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btAddNoti){
            Notification n = new Notification();
            n.setTen(edTen.getText().toString().trim());
            n.setTansuat(spinner.getSelectedItem().toString().trim());
            n.setTime(tvTime.getText().toString().trim());
            n.setNote(edNote.getText().toString().trim());
            n.setActive(false);
            firebase.addNotification(n,getContext());
            dismiss();
        } else if(view == tvTime) {
            showTimePickerDialog();
        }
    }
    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                tvTime.setText(formattedTime);
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }
}
