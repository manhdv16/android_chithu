package com.example.quanlichitieu.adapter;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.model.Transaction;
import com.example.quanlichitieu.model.TransactionGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<TransactionGroup> groupList;
    private TransactionAdapter.ItemListener itemListener;
    private TransactionAdapter.BtDeleteListener btDeleteListener;

    public void setOnTransactionClickListener(TransactionAdapter.ItemListener listener) {
        this.itemListener = listener;
    }

    public void setBtDeleteListener(TransactionAdapter.BtDeleteListener btDeleteListener) {
        this.btDeleteListener = btDeleteListener;
    }

    public void delete(Transaction t){
        for (TransactionGroup group : groupList) {
            if (group.getTransactions().contains(t)) {
                group.getTransactions().remove(t);
                if (group.getTransactions().isEmpty()) {
                    int groupPosition = groupList.indexOf(group);
                    groupList.remove(groupPosition);
                    notifyItemRemoved(groupPosition);
                } else {
                    notifyDataSetChanged();
                }
                break;
            }
        }
    }
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        public TextView tvGroupDate;
        public RecyclerView rvTransactions;

        public GroupViewHolder(View itemView) {
            super(itemView);
            tvGroupDate = itemView.findViewById(R.id.tvGroupDate);
            rvTransactions = itemView.findViewById(R.id.rvTransactions);
        }
    }

    public GroupAdapter() {
        this.groupList = new ArrayList<>();
    }

    public void setGroupList(List<TransactionGroup> groupList) {
        this.groupList = groupList;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        TransactionGroup group = groupList.get(position);
        holder.tvGroupDate.setText(group.getDate());

        // Set up inner RecyclerView
        TransactionAdapter transactionAdapter = new TransactionAdapter(group.getTransactions());
        transactionAdapter.setListener(itemListener);
        transactionAdapter.setBtDeleteListener(btDeleteListener);
        holder.rvTransactions.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvTransactions.setAdapter(transactionAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Transaction transaction = transactionAdapter.getItem(position);
                if (direction == ItemTouchHelper.LEFT) {
                    viewHolder.itemView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    viewHolder.itemView.findViewById(R.id.delete_button).setVisibility(View.GONE);
                }
                transactionAdapter.notifyItemChanged(position);
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
        itemTouchHelper.attachToRecyclerView(holder.rvTransactions);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

