package com.example.quanlichitieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.Utils.CurrencyUtils;
import com.example.quanlichitieu.model.Transaction;
import com.example.quanlichitieu.model.TransactionGroup;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.HomeViewHolder>{

    private List<Transaction> list;
    private ItemListener listener;
    private BtDeleteListener btDeleteListener;
    private Context context;
    public TransactionAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }
    public TransactionAdapter(List<Transaction> transactions) {
        this.list = transactions;
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    public void setBtDeleteListener(BtDeleteListener btDeleteListener) {
        this.btDeleteListener = btDeleteListener;
    }

    public void setList(List<Transaction> l){
        this.list = l;
        notifyDataSetChanged();
    }

    public List<Transaction> getList() {
        return list;
    }

    public void delete(int i){
        list.remove(i);
        notifyDataSetChanged();
    }
    public Transaction getItem(int i){
        return list.get(i);
    }
    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Transaction t = list.get(position);
        holder.tType.setText(t.getType());
        if(t.getStatus().equals("Chi tiêu")) {
            holder.tAmount.setText("-" + CurrencyUtils.formatVND((t.getAmount())));
        }else{
            holder.tAmount.setText("+" + CurrencyUtils.formatVND((t.getAmount())));
        }
        holder.itemView.setTag(t);
        holder.delete_button.setTag(t);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tType, tAmount;
        private ImageView img;
        private Button delete_button;
        public HomeViewHolder(@NonNull View view) {
            super(view);
            tType = view.findViewById(R.id.tType);
            tAmount = view.findViewById(R.id.tAmount);
            delete_button = view.findViewById(R.id.delete_button);
            view.setOnClickListener(this);
            delete_button.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if(view ==  delete_button){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Transaction t = (Transaction) view.getTag();
                    btDeleteListener.onBtClick((Transaction) view.getTag());
                }

            } else if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick((Transaction) view.getTag());
                }
            }
        }
    }
//    public interface ItemListener{
//        void onItemClick(View view,int position);
//    }
    public interface ItemListener {
        void onItemClick(Transaction transaction);
    }
    public interface BtDeleteListener{
        void onBtClick(Transaction transaction);
    }
}
