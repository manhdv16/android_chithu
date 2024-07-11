package com.example.quanlichitieu.fragment.TablayoutFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.activity.AddActivity;
import com.example.quanlichitieu.adapter.PLRecycleViewAdapter;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.Type;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class ExpenseFragment extends Fragment implements PLRecycleViewAdapter.ItemListener {
    private RecyclerView recycleViewExpense;
    private Firebase firebase;
    private PLRecycleViewAdapter adapter;
    private List<Type> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.c_fragment_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebase = new Firebase();
        recycleViewExpense = view.findViewById(R.id.recycleViewExpense);
        adapter = new PLRecycleViewAdapter(getContext());
        firebase.getAllTypes(new Firebase.TypeCallback() {
            @Override
            public void onTypesLoaded(List<Type> types) {
                adapter.setList(types);
                list = types;
            }
            @Override
            public void onTypesError(DatabaseError databaseError) {
            }
        }, "Chi tiêu");
        adapter.setListener(this);
        LinearLayoutManager manager= new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recycleViewExpense.setLayoutManager(manager);
        recycleViewExpense.setAdapter(adapter);
    }
    @Override
    public void onResume() {
        super.onResume();
        firebase.getAllTypes(new Firebase.TypeCallback() {
            @Override
            public void onTypesLoaded(List<Type> types) {
                adapter.setList(types);
                list = types;
            }
            @Override
            public void onTypesError(DatabaseError databaseError) {
            }
        }, "Chi tiêu");
    }

    @Override
    public void onItemClick(View view, int position) {
        Type t = list.get(position);
        Intent intent = new Intent();
        Intent intentDetail = new Intent(getActivity(), AddActivity.class);
        intentDetail.putExtra("type", t);
        getActivity().startActivity(intentDetail);
    }
}