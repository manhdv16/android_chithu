package com.example.quanlichitieu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Type;

public class AddTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edType;
    private Spinner spinner;
    private Button btAddType,btCancelType;
    private ArrayAdapter<String> typeAdapter;
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_type);
        firebase = new Firebase();
        edType = findViewById(R.id.edAddType);
        spinner = findViewById(R.id.spinnerType);
        btAddType = findViewById(R.id.btAddType);
        btCancelType = findViewById(R.id.btCancelType);
        btAddType.setOnClickListener(this);
        btCancelType.setOnClickListener(this);
        String[] types = {"Chi tiêu","Thu nhập"};
        typeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,types);
        spinner.setAdapter(typeAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view == btCancelType){
            finish();
        }
        else if(view == btAddType){
            updateData();
        }
    }

    private void updateData() {
        String type =edType.getText().toString();
        String status = spinner.getSelectedItem().toString();
        if(type.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên phân loại", Toast.LENGTH_SHORT).show();
        }else{
            firebase.addType(new Type(type,status),this);
            finish();
        }

    }
}