package com.example.quanlichitieu.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.activity.TransactionActivity;
import com.example.quanlichitieu.adapter.TransactionAdapter;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Transaction;
import com.google.firebase.database.DatabaseError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FragmentSearch extends Fragment implements View.OnClickListener, TransactionAdapter.ItemListener {
    private SearchView searchView;
    private EditText tStart,tEnd;
    private Spinner spinner;
    private Button btSearch;
    private TransactionAdapter adapter;
    private List<Transaction> listSearch;
    private RecyclerView recyclerView;
    private String searchText="";
    private Firebase firebase;
    private ArrayAdapter<String> typeAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.notifyItemChanged(position);
                if (direction == ItemTouchHelper.LEFT) {
                    viewHolder.itemView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    viewHolder.itemView.findViewById(R.id.delete_button).setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                    if (dX < -10) {
                        viewHolder.itemView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
                    } else if (dX > 5) {
                        viewHolder.itemView.findViewById(R.id.delete_button).setVisibility(View.GONE);
                    }
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initView(View view) {
        firebase = new Firebase();
        listSearch = new ArrayList<>();
        searchView = view.findViewById(R.id.search);
        tStart = view.findViewById(R.id.tStart);
        tEnd = view.findViewById(R.id.tEnd);
        btSearch = view.findViewById(R.id.btSearch);
        recyclerView =  view.findViewById(R.id.recyclerView);
        spinner = view.findViewById(R.id.spinner);
        String[] types = {"Tất cả","Chi tiêu","Thu nhập"};
        typeAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,types);
        spinner.setAdapter(typeAdapter);

        adapter = new TransactionAdapter(getContext());
        adapter.setListener(this);
        LinearLayoutManager manager= new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        btSearch.setOnClickListener(this);
        tStart.setOnClickListener(this);
        tEnd.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchText = s;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchText=s;
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == tStart) {
            showDateTimePickerDialog(tStart);
        }else if (view ==tEnd) {
            showDateTimePickerDialog(tEnd);
        } else if(view == btSearch) {
            listSearch.clear();
            if(!isValidation()){
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ", Toast.LENGTH_SHORT).show();
            } else if(!tStart.getText().toString().equals("")){
                try {
                    getTransactionByDate(tStart.getText().toString(),tEnd.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }else if(!searchText.equals("")){
                getTransactionByName(searchText);
            } else {
                getAllTransactions();
            }

        }
    }

    private void getAllTransactions() {
        firebase.getAllTransactions(new Firebase.TransactionCallback() {
            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {

                String status = spinner.getSelectedItem().toString();
                if(!status.equals("Tất cả") ) {
                    updateListByStatus(transactions, status);
                }else{
                    listSearch = transactions;
                }
                adapter.setList(listSearch);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onTransactionsError(DatabaseError databaseError) {

            }
        });
    }

    private void getTransactionByName(String searchText) {
        firebase.getTransactionsByName(searchText, new Firebase.TransactionCallback() {
            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {
                String status = spinner.getSelectedItem().toString();
                if(!status.equals("Tất cả") ) {
                    updateListByStatus(transactions, status);
                }else {
                    listSearch = transactions;
                }
                adapter.setList(listSearch);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onTransactionsError(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Không tìm thấy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTransactionByDate(String tStart,String tEnd) throws ParseException {
        firebase.getTransactionByDateToDate(tStart, tEnd, new Firebase.TransactionCallback() {
            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {
                List<Transaction> tmpList = new ArrayList<>();
                for(int i=0;i<transactions.size();i++) {
                    if(!searchText.equals("")) {
                        if(transactions.get(i).getType().contains(searchText)){
                            tmpList.add(transactions.get(i));
                        }
                    }else{
                        tmpList.add(transactions.get(i));
                    }
                }
                String status = spinner.getSelectedItem().toString();
                if(!status.equals("Tất cả") ) {
                    updateListByStatus(tmpList, status);
                }else {
                    listSearch = tmpList;
                }
                adapter.setList(listSearch);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onTransactionsError(DatabaseError databaseError) {

            }
        });
    }
    private void updateListByStatus(List<Transaction> list,String status) {
        for(int i=0;i<list.size();i++) {
            if(list.get(i).getStatus().equals(status)){
                listSearch.add(list.get(i));
            }
        }
    }

    private boolean isValidation() {
        String dau = tStart.getText().toString();
        String cuoi = tEnd.getText().toString();
        if(dau.equals("") && cuoi.equals("")) return true;
        if(!dau.equals("") && cuoi.equals("")) return false;
        if(dau.equals("") && !cuoi.equals("")) return false;
        if(!dau.equals("") && !cuoi.equals("")) return true;
        return false;
    }

    private void showDateTimePickerDialog(EditText t) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);
                    String selectedDateTime = dateFormat.format(calendar.getTime());
                    t.setText(selectedDateTime);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    @Override
    public void onItemClick(Transaction t) {
        Intent intentDetail = new Intent(getActivity(), TransactionActivity.class);
        intentDetail.putExtra("transaction", t);
        startActivity(intentDetail);
    }
}
