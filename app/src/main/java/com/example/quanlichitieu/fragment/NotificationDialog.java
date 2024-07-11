package com.example.quanlichitieu.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.Utils.AlarmReceiver;
import com.example.quanlichitieu.adapter.NotiRecycleViewAdapter;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Notification;
import com.google.firebase.database.DatabaseError;

import java.util.Calendar;
import java.util.List;

public class NotificationDialog extends DialogFragment implements
        View.OnClickListener, NotiRecycleViewAdapter.ItemListener, NotiRecycleViewAdapter.OnItemToggleListener {
    private RecyclerView recycleViewNoti;
    private Button btAddNoti;
    private NotiRecycleViewAdapter adapter;
    private Firebase firebase;
    private List<Notification> listNoti;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.notification_dialog, null);
        initView(view);
        builder.setView(view)
                .setTitle("Danh sách nhắc nhở")
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_border);
        }
        return dialog;
    }

    private void initView(View view) {
        firebase = new Firebase();
        adapter = new NotiRecycleViewAdapter(getContext());
        recycleViewNoti = view.findViewById(R.id.recycleViewNoti);
        btAddNoti = view.findViewById(R.id.btAddNoti);
        btAddNoti.setOnClickListener(this);

        firebase.getAllNoti(new Firebase.NotiCallback() {
            @Override
            public void onNotificationsLoaded(List<Notification> list) {
                listNoti = list;
                adapter.setList(list);
                recycleViewNoti.setAdapter(adapter);
            }
            @Override
            public void onNotificationsError(DatabaseError databaseError) {
            }
        });

        adapter.setListener(this);
        adapter.setToggleListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recycleViewNoti.setLayoutManager(manager);
    }

    @Override
    public void onClick(View view) {
        if (view == btAddNoti) {
            NotiAddDialog notiAddDialog = new NotiAddDialog();
            notiAddDialog.show(getParentFragmentManager(), "NotiAddDialog");
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Notification n = listNoti.get(position);
        NotiEditDialog editDialog = NotiEditDialog.newInstance(n);
        editDialog.show(getActivity().getSupportFragmentManager(), "NotiEditDialog");
    }

    @Override
    public void onItemToggle(int position, boolean isChecked) {
        Notification n = listNoti.get(position);
        n.setActive(isChecked);
        firebase.editNotification(n,getContext());
        if(isChecked) {
            String time = n.getTime();
            String [] listStr = time.split(":");
            String sgio = listStr[0];
            String sphut =  listStr[1];
            int gio = Integer.parseInt(sgio);
            int phut = Integer.parseInt(sphut);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, gio);
            calendar.set(Calendar.MINUTE, phut);
            calendar.set(Calendar.SECOND, 0);
            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.setAction("MyAction");
            intent.putExtra("ten",n.getTen());
            intent.putExtra("time",n.getTime());
            intent.putExtra("note",n.getNote());
            intent.putExtra("id",n.getId());
            intent.putExtra("tansuat",n.getTansuat());
            alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            pendingIntent = PendingIntent.getBroadcast(getContext(), 0,
                    intent,PendingIntent.FLAG_UPDATE_CURRENT);
            String tansuat = n.getTansuat();
            if(tansuat.equals("Một lần")) {
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
            } else if(tansuat.equals("Hàng ngày")){
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        } else {
            pendingIntent.cancel();
        }

    }
}
