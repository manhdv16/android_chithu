package com.example.quanlichitieu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.adapter.PLRecycleViewAdapter;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Type;

public class EditTypeActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edType;
    private Spinner spinner;
    private Button btAddType,btCancelType;
    private ArrayAdapter<String> typeAdapter;
    private Firebase firebase;
    private Intent intent;
    private Type currentType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_type);
        firebase = new Firebase();
        edType = findViewById(R.id.edAddTypeUpdate);
        spinner = findViewById(R.id.spinnerTypeEdit);
        btAddType = findViewById(R.id.btUpdateType);
        btCancelType = findViewById(R.id.btCancelTypeUpdate);
        btAddType.setOnClickListener(this);
        btCancelType.setOnClickListener(this);
        String[] types = {"Chi tiêu","Thu nhập"};
        typeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,types);
        spinner.setAdapter(typeAdapter);
        intent = getIntent();
        currentType = (Type) intent.getSerializableExtra("type");
        if (currentType != null) {
            edType.setText(currentType.getName());
            String status = currentType.getStatus();
            if(status.equals("Chi tiêu")) {
                spinner.setSelection(0);
            }else spinner.setSelection(1);
        }
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
        firebase.updateType(new Type(currentType.getId(),type,status), this);
        finish();
    }
}