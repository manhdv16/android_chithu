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

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edCost, edCate, edTime, edNote;
    private Spinner spinner;
    private Button btAdd, btCancel;
    private ArrayAdapter<String> typeAdapter;
    private Firebase firebase;
    private Intent intent;
    private Type currentType;

    private List<String> listTypes = new ArrayList<>();
    private String selectedType;
    private int FLAG = 0;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
    }

    private void initView() {
        firebase = new Firebase();
        edCate = findViewById(R.id.edCate);
        edTime = findViewById(R.id.edTime);
        edNote = findViewById(R.id.edNote);
        spinner = findViewById(R.id.spinnerTran);
        btAdd = findViewById(R.id.btAdd);
        btCancel = findViewById(R.id.btCancel);
        edCost = findViewById(R.id.edCost);
        btAdd.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        String[] types = {"Chi tiêu","Thu nhập"};
        typeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,types);
        spinner.setAdapter(typeAdapter);
        intent = getIntent();
        currentType = (Type) intent.getSerializableExtra("type");
        if(currentType.getStatus().equals("Thu nhập")){
            spinner.setSelection(1);
        }else{
            spinner.setSelection(0);
        }
        edCate.setText(currentType.getName());
        edCost.addTextChangedListener(new MoneyTextWatcher(edCost));
        edCate.setOnClickListener(v-> showCategoryDialog());
        edTime.setOnClickListener(v -> showDateTimePickerDialog());
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

    private void handleSelectedItem(String selectedItem) {
        listTypes.clear();
        if(FLAG ==1) {
            edCate.setText("");
        }
        FLAG=1;
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

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn danh mục");
        String[] arrayStr = listTypes.toArray(new String[0]);
        builder.setItems(arrayStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedType = listTypes.get(which);
                edCate.setText(selectedType);
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
        if (view == btCancel) {
            finish();
        } else if (view == btAdd) {
            updateData();
        }
    }

    private void updateData() {

        String str = edCost.getText().toString();
        String type = edCate.getText().toString();
        String time = edTime.getText().toString();
        String note = edNote.getText().toString();
        String status = spinner.getSelectedItem().toString();

        if (str.isEmpty() || type.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            String cleanStr = str.replaceAll("[,.]", "");
            double cost = Double.valueOf(cleanStr);
            Transaction t = new Transaction(cost, type, time, note, status);
            firebase.addTransaction(t, this);
            finish();
        }
    }
}