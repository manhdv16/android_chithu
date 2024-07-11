package com.example.quanlichitieu.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.activity.EditTypeActivity;
import com.example.quanlichitieu.model.Type;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
public class PLRecycleViewAdapter extends RecyclerView.Adapter<PLRecycleViewAdapter.HomeViewHolder>{

    private List<Type> list;
    private ItemListener listener;
    private Context context;
    public PLRecycleViewAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    public void setList(List<Type> l){
        this.list = l;
        notifyDataSetChanged();
    }
    public void delete(int i){
        list.remove(i);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Type t = list.get(position);
        String type = t.getName();
        if(type.length()>9){
            type = type.substring(0,8)+"...";
        }
        holder.tvType.setText(type);
        holder.imDelete.setImageResource(R.drawable.baseline_delete_24);
        holder.imEdit.setImageResource(R.drawable.baseline_edit_24);
        holder.imDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(view,position);
            }
        });
        holder.imEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvType;
        private ImageView imDelete, imEdit;

        public HomeViewHolder(@NonNull View view) {
            super(view);
            tvType = view.findViewById(R.id.tvType);
            imDelete = view.findViewById(R.id.imDelete);
            imEdit = view.findViewById(R.id.imEdit);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if(listener != null) {
                listener.onItemClick(view,getAdapterPosition());
            }
        }
    }
    private void deleteData(View view,int position) {
        Type currentType = list.get(position);
        AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
        builder.setTitle("Thông báo xóa!");
        builder.setTitle("Bạn có chắc muốn xóa mục  "+currentType.getName()+" không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://expensemanagement-8f058-default-rtdb.asia-southeast1.firebasedatabase.app");
                DatabaseReference typeRef = database.getReference().child("Types").child(currentType.getId());
                typeRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(view.getContext(), "Xóa dữ liệu thành công", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(view.getContext(), "Xóa dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                        });
                list.remove(position);
                notifyDataSetChanged();
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
    private void updateData(View view, int position) {
        Type t = list.get(position);
        Intent intent = new Intent();
        Intent intentDetail = new Intent(context, EditTypeActivity.class);
        intentDetail.putExtra("type", t);
        context.startActivity(intentDetail);
    }
    public interface ItemListener{
        void onItemClick(View view,int position);
    }
}
