package com.example.quanlichitieu.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NotiEditDialog extends DialogFragment implements View.OnClickListener {
    private EditText edTen,edNote;
    private Spinner spinner;
    private TextView tvTime;
    private Button btEditNoti,btDeleteNoti;
    private Firebase firebase;
    private Notification currentNoti;

    public static NotiEditDialog newInstance(Notification noti) {
        Bundle args = new Bundle();
        NotiEditDialog fragment = new NotiEditDialog();
        args.putSerializable("noti", noti);
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.noti_edit_dialog, null);
        initView(view);
        builder.setView(view)
                .setTitle("Chi tiết");
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
        btEditNoti = view.findViewById(R.id.btEditNoti);
        btDeleteNoti = view.findViewById(R.id.btDeleteNoti);
        firebase = new Firebase();
        btEditNoti.setOnClickListener(this);
        btDeleteNoti.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        if (getArguments() != null) {
            currentNoti = (Notification) getArguments().getSerializable("noti");
            edTen.setText(currentNoti.getTen());
            edNote.setText(currentNoti.getNote());
            tvTime.setText(currentNoti.getTime());
            int index = getIndexByText(currentNoti.getTansuat());
            spinner.setSelection(index);
        }
    }

    private int getIndexByText(String tansuat) {
        int index;
        if(tansuat.equals("Hàng ngày")){
            index =1;
        } else if(tansuat.equals("Hàng tuần")){
            index = 2;
        } else if(tansuat.equals("Hàng tháng")){
            index=3;
        } else {
            index=0;
        }
        return index;
    }

    @Override
    public void onClick(View view) {
        if(view == btEditNoti){
            Notification n = new Notification();
            n.setId(currentNoti.getId());
            n.setTen(edTen.getText().toString().trim());
            n.setTansuat(spinner.getSelectedItem().toString().trim());
            n.setTime(tvTime.getText().toString().trim());
            n.setNote(edNote.getText().toString().trim());
            n.setActive(false);
            firebase.editNotification(n,getContext());
            dismiss();
        } else if(view == tvTime) {
            showTimePickerDialog();
        } else if(view == btDeleteNoti) {
            deleteNoti(view);
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
    private void deleteNoti(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
        builder.setTitle("Thông báo xóa!");
        builder.setTitle("Bạn có chắc muốn xóa nhắc nhở  "+currentNoti.getTen()+" không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://expensemanagement-8f058-default-rtdb.asia-southeast1.firebasedatabase.app");
                DatabaseReference notiRef = database.getReference().child("Notifications").child(currentNoti.getId());
                notiRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(view.getContext(), "Xóa dữ liệu thành công", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(view.getContext(), "Xóa dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                        });
                dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
