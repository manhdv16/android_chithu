package com.example.quanlichitieu.Utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Notification;

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    final String CHANNEL_ID = "200";
    private Firebase firebase = new Firebase();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("MyAction")) {
            String ten = intent.getStringExtra("ten");
            String time = intent.getStringExtra("time");
            String note = intent.getStringExtra("note");
            String id = intent.getStringExtra("id");
            String tansuat = intent.getStringExtra("tansuat");
            Notification n = new Notification(id, ten, tansuat, time, note, false);
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel 2",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Miêu tả cho channel 2");
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(n.getTen())
                    .setContentText(n.getNote())
                    .setSmallIcon(R.drawable.baseline_notifications_active_24)
                    .setColor(Color.BLUE)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);

            notificationManager.notify(getNotificationid(), builder.build());
            firebase.editNotification(n, context.getApplicationContext());

            if (tansuat.equals("Hàng ngày")) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));
                calendar.set(Calendar.SECOND, 0);

                Intent newIntent = new Intent(context, AlarmReceiver.class);
                newIntent.setAction("MyAction");
                newIntent.putExtra("ten", ten);
                newIntent.putExtra("time", time);
                newIntent.putExtra("note", note);
                newIntent.putExtra("id", id);
                newIntent.putExtra("tansuat", tansuat);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id.hashCode(), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }
    private int getNotificationid() {
        int time = (int) new Date().getTime();
        return time;
    }
}
