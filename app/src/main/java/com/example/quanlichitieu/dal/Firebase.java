package com.example.quanlichitieu.dal;

import android.content.Context;
import android.service.controls.Control;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.fragment.FragmentHome;
import com.example.quanlichitieu.model.Notification;
import com.example.quanlichitieu.model.Statistic;
import com.example.quanlichitieu.model.Transaction;
import com.example.quanlichitieu.model.Type;
import com.example.quanlichitieu.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Firebase {
    private FirebaseDatabase database;
    public Firebase() {
        this.database = FirebaseDatabase.getInstance("https://expensemanagement-8f058-default-rtdb.asia-southeast1.firebasedatabase.app");
    }
    public void addNotification(Notification t, Context context) {
        DatabaseReference notiRef = database.getReference().child("Notifications");
        String transactionKey = notiRef.push().getKey();
        t.setId(transactionKey);
        notiRef.child(transactionKey).setValue(t)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Thêm dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                });
    }
    public void editNotification(Notification t, Context context) {
        DatabaseReference notiRef = database.getReference().child("Notifications");
        notiRef.child(t.getId()).setValue(t)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e  -> {
                });
    }
    public void addType(Type t, Context context) {
        DatabaseReference typeRef = database.getReference().child("Types");
        String typeKey = typeRef.push().getKey();
        t.setId(typeKey);
        typeRef.child(typeKey).setValue(t)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Thêm phân loại thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Thêm phân loại thất bại", Toast.LENGTH_SHORT).show();
                });
    }
    public void updateType(Type t, Context context) {
        DatabaseReference typeRef = database.getReference().child("Types");
        typeRef.child(t.getId()).setValue(t)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Cập nhập thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e  -> {
                    Toast.makeText(context, "Cập nhập thất bại", Toast.LENGTH_SHORT).show();
                });
    }
    public void addTransaction(Transaction t, Context context) {
        DatabaseReference transactionsRef = database.getReference().child("Transactions");
        String transactionKey = transactionsRef.push().getKey();
        t.setId(transactionKey);
        transactionsRef.child(transactionKey).setValue(t)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
                    updateStatistics(t.getDateUpdate(), t.getStatus(), t.getAmount(), "POST");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Thêm dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                });
    }
    public void updateStatistics(String time, String status, double cost, String method) {
        String str_time = time.substring(0,7);
        DatabaseReference statisticsRef = database.getReference().child("Statistics");
        Query query = statisticsRef.orderByChild("timePeriod").equalTo(str_time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int tmp=0;
                Statistic st = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    st = snapshot.getValue(Statistic.class);
                    tmp=1;
                    if(method.equals("POST")) {
                        if(status.equals("Chi tiêu")) {
                            st.setTotalExpense(st.getTotalExpense()+cost);
                        }else {
                            st.setTotalIncome(st.getTotalIncome()+cost);
                        }
                    }else if(method.equals("DELETE")) {
                        if(status.equals("Chi tiêu")) {
                            st.setTotalExpense(st.getTotalExpense()-cost);
                        }else {
                            st.setTotalIncome(st.getTotalIncome()-cost);
                        }
                    }
                    Statistic finalSt = st;
                    statisticsRef.child(st.getId()).setValue(st)
                            .addOnSuccessListener(aVoid -> {
                                FragmentHome.updateInOut(finalSt);
                            })
                            .addOnFailureListener(e -> {
                            });
                    break;
                }
                if(tmp==0) {
                    if(status.equals("Chi tiêu")) {
                        st = new Statistic(str_time,0,cost);
                    }else {
                        st = new Statistic(str_time,cost,0);
                    }
                    String statisticKey = statisticsRef.push().getKey();
                    st.setId(statisticKey);
                    Statistic finalSt = st;
                    statisticsRef.child(statisticKey).setValue(st)
                            .addOnSuccessListener(aVoid -> {
                                FragmentHome.updateInOut(finalSt);
                            })
                            .addOnFailureListener(e -> {
                            });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void editStatistics(Transaction o, Transaction n) {
        String oldDate = o.getDateUpdate();
        String newDate = n.getDateUpdate();
        String oStatus = o.getStatus();
        String nStatus = n.getStatus();
        double nCost = n.getAmount();
        String odate = oldDate.substring(0,7);
        String ndate = newDate.substring(0,7);
        DatabaseReference statisticsRef = database.getReference().child("Statistics");
        if(ndate.equals(odate)) {
            Query query = statisticsRef.orderByChild("timePeriod").equalTo(ndate);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Statistic st =null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         st = snapshot.getValue(Statistic.class);
                            if(oStatus.equals(nStatus)) {
                                double hieu = n.getAmount() - o.getAmount();
                                if(oStatus.equals("Chi tiêu")) {
                                    st.setTotalExpense(st.getTotalExpense()+hieu);
                                } else {
                                    st.setTotalIncome(st.getTotalIncome() + hieu);
                                }
                            }
                            else {
                                if(oStatus.equals("Chi tiêu")){
                                    st.setTotalExpense(st.getTotalExpense()-o.getAmount());
                                    st.setTotalIncome(st.getTotalIncome()+n.getAmount());
                                }else {
                                    st.setTotalIncome(st.getTotalIncome()-o.getAmount());
                                    st.setTotalExpense(st.getTotalExpense()+n.getAmount());
                                }
                            }
                        Statistic finalSt = st;
                        statisticsRef.child(st.getId()).setValue(st)
                                    .addOnSuccessListener(aVoid -> {
                                        FragmentHome.updateInOut(finalSt);
                                    })
                                    .addOnFailureListener(e -> {
                                    });
                            break;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else{
//            khác date
            Query query = statisticsRef.orderByChild("timePeriod").equalTo(odate);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Statistic st=null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        st = snapshot.getValue(Statistic.class);
                        if(oStatus.equals("Chi tiêu")) {
                            st.setTotalExpense(st.getTotalExpense()-o.getAmount());
                        } else {
                            st.setTotalIncome(st.getTotalIncome()-o.getAmount());
                        }
                        Statistic finalSt = st;
                        statisticsRef.child(st.getId()).setValue(st)
                                .addOnSuccessListener(aVoid -> {
                                    FragmentHome.updateInOut(finalSt);
                                })
                                .addOnFailureListener(e -> {
                                });
                        break;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            Query query1 = statisticsRef.orderByChild("timePeriod").equalTo(ndate);
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int tmp=0;
                    Statistic st=null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        tmp=1;
                        st = snapshot.getValue(Statistic.class);
                        if(nStatus.equals("Chi tiêu")) {
                            st.setTotalExpense(st.getTotalExpense()+n.getAmount());
                        } else {
                            st.setTotalIncome(st.getTotalIncome()+n.getAmount());
                        }
                        Statistic finalSt = st;
                        statisticsRef.child(st.getId()).setValue(st)
                                .addOnSuccessListener(aVoid -> {
                                    FragmentHome.updateInOut(finalSt);
                                })
                                .addOnFailureListener(e -> {
                                });
                        break;
                    }
                    if(tmp==0) {
                        if(nStatus.equals("Chi tiêu")) {
                            st = new Statistic(ndate,0,nCost);
                        }else {
                            st = new Statistic(ndate,nCost,0);
                        }
                        String statisticKey = statisticsRef.push().getKey();
                        st.setId(statisticKey);
                        Statistic finalSt = st;
                        statisticsRef.child(statisticKey).setValue(st)
                                .addOnSuccessListener(aVoid -> {
                                    FragmentHome.updateInOut(finalSt);
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public interface NotiCallback {
        void onNotificationsLoaded(List<Notification> list);
        void onNotificationsError(DatabaseError databaseError);
    }
    public void getAllNoti(NotiCallback callback) {
        DatabaseReference transactionsRef = database.getReference().child("Notifications");
        List<Notification> list = new ArrayList<>();
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification n = snapshot.getValue(Notification.class);
                    list.add(n);
                }
                callback.onNotificationsLoaded(list);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onNotificationsError(databaseError);
            }
        });
    }
    public interface TransactionCallback {
        void onTransactionsLoaded(List<Transaction> transactions);
        void onTransactionsError(DatabaseError databaseError);
    }

    public interface TypeCallback {
        void onTypesLoaded(List<Type> types);
        void onTypesError(DatabaseError databaseError);
    }
    public void getAllTypes(TypeCallback callback, String cate){
        DatabaseReference transactionsRef = database.getReference().child("Types");
        List<Type> list = new ArrayList<>();

        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Type t = snapshot.getValue(Type.class);
                    if(t.getStatus().equals(cate)){
                        list.add(t);
                    }
                }
                callback.onTypesLoaded(list);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onTypesError(databaseError);
            }
        });
    }
    public interface StatisticCallback {
        void onStatisticLoaded(Statistic statistic);
        void onStatisticError(DatabaseError databaseError);
    }

    public void getStatisticByDate(String date, StatisticCallback callback) {
        DatabaseReference transactionsRef = database.getReference().child("Statistics");
        Query query = transactionsRef.orderByChild("timePeriod").equalTo(date);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Statistic s = snapshot.getValue(Statistic.class);
                    callback.onStatisticLoaded(s);
                    return;
                }
                callback.onStatisticLoaded(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onStatisticError(databaseError);
            }
        });
    }
    public interface UserCallback {
        void onUserLoaded(User u);
        void onUserError(DatabaseError databaseError);
    }
    public void getUserByEmail(String email, UserCallback callback) {
        DatabaseReference userRef = database.getReference().child("Users");
        Query query = userRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User u = snapshot.getValue(User.class);
                    callback.onUserLoaded(u);
                    return;
                }
                callback.onUserError(null);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onUserError(databaseError);
            }
        });
    }

    public void getAllTransactions(TransactionCallback callback) {
        DatabaseReference transactionsRef = database.getReference().child("Transactions");
        List<Transaction> list = new ArrayList<>();

        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    list.add(transaction);
                }
                callback.onTransactionsLoaded(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onTransactionsError(databaseError);
            }
        });
    }
    public void getTransactionsByStatus(String status,TransactionCallback callback){
        DatabaseReference transactionsRef = database.getReference().child("Transactions");
        List<Transaction> list = new ArrayList<>();
        Query query = transactionsRef.orderByChild("status").equalTo(status);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue(Transaction.class));
                }
                callback.onTransactionsLoaded(list);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onTransactionsError(databaseError);
            }
        });
    }
    public void getTransactionByDate(String date, TransactionCallback callback) throws ParseException {
        String[] str = date.split("-");
        int month = Integer.valueOf(str[1]);
        int year = Integer.valueOf(str[0]);

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(Calendar.YEAR, year);
        calendarStart.set(Calendar.MONTH, month - 1);
        calendarStart.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendarStart.getTime();

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(Calendar.YEAR, year);
        calendarEnd.set(Calendar.MONTH, month - 1);
        calendarEnd.set(Calendar.DAY_OF_MONTH, calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = calendarEnd.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startOfMonth = dateFormat.format(startDate);
        String endOfMonth = dateFormat.format(endDate);

        DatabaseReference transactionsRef = database.getReference().child("Transactions");
        List<Transaction> list = new ArrayList<>();
        Query query = transactionsRef.orderByChild("dateUpdate").startAt(startOfMonth).endAt(endOfMonth);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue(Transaction.class));
                }
                callback.onTransactionsLoaded(list);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onTransactionsError(databaseError);
            }
        });
    }
    public void getTransactionsByName(String searchText,TransactionCallback callback){
        DatabaseReference transactionsRef = database.getReference().child("Transactions");
        List<Transaction> list = new ArrayList<>();
        Query query = transactionsRef.orderByChild("type").startAt(searchText)
                .endAt(searchText + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue(Transaction.class));
                }
                callback.onTransactionsLoaded(list);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onTransactionsError(databaseError);
            }
        });
    }

    public void getTransactionByDateToDate(String start,String end, TransactionCallback callback) throws ParseException {
        DatabaseReference transactionsRef = database.getReference().child("Transactions");
        List<Transaction> list = new ArrayList<>();
        Query query = transactionsRef.orderByChild("dateUpdate").startAt(start).endAt(end);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue(Transaction.class));
                }
                callback.onTransactionsLoaded(list);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onTransactionsError(databaseError);
            }
        });
    }
}
