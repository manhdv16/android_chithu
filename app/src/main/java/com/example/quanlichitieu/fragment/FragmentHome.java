package com.example.quanlichitieu.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.Utils.CurrencyUtils;
import com.example.quanlichitieu.activity.TransactionActivity;
import com.example.quanlichitieu.adapter.GroupAdapter;
import com.example.quanlichitieu.adapter.TransactionAdapter;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Statistic;
import com.example.quanlichitieu.model.Transaction;
import com.example.quanlichitieu.model.TransactionGroup;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private Firebase firebase;
    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private static TextView tvDate,tvSodu,tvChitieu,tvThunhap;
    private LinearLayout lnDate;
    private LinearLayoutManager manager;
    private List<TransactionGroup> groupList ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
//        adapter = new TransactionAdapter(getContext());
//        adapter.setListener(this);
//        adapter.setBtDeleteListener(this);
        adapter = new GroupAdapter();
        manager= new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        try {
            updateHome(tvDate.getText().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        adapter.setOnTransactionClickListener(new TransactionAdapter.ItemListener() {
            @Override
            public void onItemClick(Transaction transaction) {
                Intent intentDetail = new Intent(getActivity(), TransactionActivity.class);
                intentDetail.putExtra("transaction", transaction);
                startActivity(intentDetail);
            }
        });
        adapter.setBtDeleteListener(new TransactionAdapter.BtDeleteListener() {
            @Override
            public void onBtClick(Transaction transaction) {
                deleteData(transaction);
            }
        });
    }

    private void initView(View view) {
        firebase = new Firebase();
        groupList = new ArrayList<>();
        tvDate = view.findViewById(R.id.tvDate);
        recyclerView = view.findViewById(R.id.recycleView);
        tvSodu = view.findViewById(R.id.tvSodu);
        tvChitieu = view.findViewById(R.id.tvChitieu);
        tvThunhap = view.findViewById(R.id.tvThunhap);
        lnDate = view.findViewById(R.id.lnDate);
        lnDate.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            updateHome(tvDate.getText().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == lnDate) {
            new RackMonthPicker(getContext())
                    .setLocale(Locale.ENGLISH)
                    .setPositiveButton(new DateMonthDialogListener() {
                        @Override
                        public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                            String selectedDate = String.format("%d-%02d", year, month);
                            tvDate.setText(selectedDate);
                            try {
                                updateHome(selectedDate);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .setNegativeButton(new OnCancelMonthDialogListener() {
                        @Override
                        public void onCancel(AlertDialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
    private void updateHome(String selectedDate) throws ParseException {
        groupList.clear();
        firebase.getTransactionByDate(selectedDate, new Firebase.TransactionCallback() {
            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {
                List<Transaction> transactionList = transactions;
                groupList = groupTransactionsByDate(transactionList);
                adapter.setGroupList(groupList);
                recyclerView.setAdapter(adapter);
                firebase.getStatisticByDate(selectedDate, new Firebase.StatisticCallback() {
                    @Override
                    public void onStatisticLoaded(Statistic statistic) {
                        if (statistic != null) {
                            updateInOut(statistic);
                        }else{
                            updateInOut(new Statistic(selectedDate,0,0));
                        }
                    }
                    @Override
                    public void onStatisticError(DatabaseError databaseError) {
                    }
                });
            }

            private List<TransactionGroup> groupTransactionsByDate(List<Transaction> transactionList) {
                Map<String, List<Transaction>> groupedMap = new HashMap<>();

                for (Transaction transaction : transactionList) {
                    String date = transaction.getDateUpdate();
                    if (!groupedMap.containsKey(date)) {
                        groupedMap.put(date, new ArrayList<>());
                    }
                    groupedMap.get(date).add(transaction);
                }

                List<TransactionGroup> transactionGroups = new ArrayList<>();
                for (Map.Entry<String, List<Transaction>> entry : groupedMap.entrySet()) {
                    transactionGroups.add(new TransactionGroup(entry.getKey(), entry.getValue()));
                }
                Collections.sort(transactionGroups, new Comparator<TransactionGroup>() {
                    @Override
                    public int compare(TransactionGroup t1, TransactionGroup t2) {
                        return t2.getDate().compareTo(t1.getDate());
                    }
                });
                return transactionGroups;
            }

            @Override
            public void onTransactionsError(DatabaseError databaseError) {
            }
        });
    }
    public static void updateInOut(Statistic t) {
        if(t.getTimePeriod().equals(tvDate.getText().toString().trim())){
            double expense = t.getTotalExpense();
            double income = t.getTotalIncome();
            double profit = income-expense;
            tvSodu.setText(CurrencyUtils.formatVND(profit));
            tvChitieu.setText(CurrencyUtils.formatVND(expense));
            tvThunhap.setText(CurrencyUtils.formatVND(income));
        }
    }

    private void deleteData(Transaction t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Transaction transaction = t;
        builder.setTitle("Thông báo xóa!");
        builder.setTitle("Bạn có chắc muốn xóa mục  "+t.getType()+" không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Firebase firebase = new Firebase();
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://expensemanagement-8f058-default-rtdb.asia-southeast1.firebasedatabase.app");
                DatabaseReference transactionsRef = database.getReference().child("Transactions").child(t.getId());
                transactionsRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            adapter.delete(t);
                            Toast.makeText(getContext(), "Xóa dữ liệu thành công", Toast.LENGTH_SHORT).show();
                            firebase.updateStatistics(t.getDateUpdate(), t.getStatus(), t.getAmount(), "DELETE");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Xóa dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                        });
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
