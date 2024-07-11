package com.example.quanlichitieu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.Utils.CurrencyUtils;
import com.example.quanlichitieu.Utils.MoneyTextWatcher;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.dal.SQLiteHelper;
import com.example.quanlichitieu.model.Transaction;
import com.example.quanlichitieu.model.Type;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edCost, edType, edTime, edNote;
    private Button btUpdate,btEdit;
    private Transaction currentTrans;
    private Firebase firebase;
    private Spinner spinner;
    private ArrayAdapter<String> typeAdapter;
    private List<String> listTypes = new ArrayList<>();
    private String selectedType;
    private Intent intent;
    private int FLG=0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        initView();
    }

    private void initView() {
        firebase = new Firebase();
        edCost = findViewById(R.id.edCost);
        edType = findViewById(R.id.edType);
        edTime = findViewById(R.id.edTime);
        edNote = findViewById(R.id.edNote);
        spinner = findViewById(R.id.spinnerUpdate);
        btEdit = findViewById(R.id.btEdit);
        btUpdate = findViewById(R.id.btUpdate);
        edType.setOnClickListener(v-> showCategoryDialog());
        edTime.setOnClickListener(v -> showDateTimePickerDialog());
        btUpdate.setOnClickListener(this);
        btEdit.setOnClickListener(this);
        edCost.setEnabled(false);
        edType.setEnabled(false);
        edTime.setEnabled(false);
        edNote.setEnabled(false);
        spinner.setEnabled(false);
        String[] types = {"Chi tiêu","Thu nhập"};
        typeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,types);
        spinner.setAdapter(typeAdapter);
        intent = getIntent();
        currentTrans = (Transaction) intent.getSerializableExtra("transaction");
        if (currentTrans != null) {
            edCost.setText(CurrencyUtils.formatVND(currentTrans.getAmount()));
            edType.setText(currentTrans.getType());
            edTime.setText(currentTrans.getDateUpdate());
            edNote.setText(currentTrans.getNote());
            String status = currentTrans.getStatus();
            if(status.equals("Chi tiêu")) {
                spinner.setSelection(0);
            }else spinner.setSelection(1);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                handleSelectedItem(selectedItem);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn danh mục");
        String[] arrayStr = listTypes.toArray(new String[0]);
        builder.setItems(arrayStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedType = listTypes.get(which);
                edType.setText(selectedType);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDateTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);
                    String selectedDateTime = dateFormat.format(calendar.getTime());
                    edTime.setText(selectedDateTime);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view == btEdit) {
            btUpdate.setVisibility(View.VISIBLE);
            btEdit.setVisibility(View.GONE);
            edCost.setEnabled(true);
            String str = edCost.getText().toString().replaceAll("[₫\\s]","").trim();
            edCost.setText(str);
            edType.setEnabled(true);
            edTime.setEnabled(true);
            edNote.setEnabled(true);
            spinner.setEnabled(true);
            edCost.addTextChangedListener(new MoneyTextWatcher(edCost));
        }else if (view == btUpdate) {
            updateData();
        }
    }
    private void updateData() {
        String str = edCost.getText().toString();
        String type = edType.getText().toString();
        String time = edTime.getText().toString();
        String note = edNote.getText().toString();
        String status = spinner.getSelectedItem().toString();
        String cleanStr = str.replaceAll("[,.₫\\s]", "").trim();
        double cost = Double.valueOf(cleanStr);
        Transaction t = new Transaction(cost,type,time,note,status);
        t.setId(currentTrans.getId());
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://expensemanagement-8f058-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference transactionsRef = database.getReference().child("Transactions").child(currentTrans.getId());
        transactionsRef.setValue(t)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhập dữ liệu thành công", Toast.LENGTH_SHORT).show();
                    firebase.editStatistics(currentTrans, t);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Thêm dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                });
        finish();
    }
    private void handleSelectedItem(String selectedItem) {
        if(FLG ==1) {
            edType.setText("");
        }
        FLG=1;
        listTypes.clear();
        firebase.getAllTypes(new Firebase.TypeCallback() {
            @Override
            public void onTypesLoaded(List<Type> types) {
                int i=0;
                for(Type t: types) {
                    listTypes.add(t.getName());
                }
            }
            @Override
            public void onTypesError(DatabaseError databaseError) {
            }
        }, selectedItem);
    }
}