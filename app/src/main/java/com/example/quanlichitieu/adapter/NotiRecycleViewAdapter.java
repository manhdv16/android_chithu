package com.example.quanlichitieu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotiRecycleViewAdapter extends RecyclerView.Adapter<NotiRecycleViewAdapter.NotiViewHolder> {

    private List<Notification> list;
    private Context context;
    private ItemListener listener;
    private OnItemToggleListener toggleListener;

    public NotiRecycleViewAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }
    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    public void setToggleListener(OnItemToggleListener toggleListener) {
        this.toggleListener = toggleListener;
    }

    public void setList(List<Notification> l){
        this.list = l;
        notifyDataSetChanged();
    }
    public void delete(int i){
        list.remove(i);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noti,parent,false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        Notification t = list.get(position);
        holder.tvName.setText(t.getTen());
        holder.tvTime.setText(t.getTime());
        holder.toggleBt.setChecked(t.isActive());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName,tvTime;
        private ToggleButton toggleBt;

        public NotiViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            toggleBt = view.findViewById(R.id.toggleBt);
            view.setOnClickListener(this);
            toggleBt.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                toggleListener.onItemToggle(position,isChecked);
            });
        }
        @Override
        public void onClick(View view) {
            if(listener != null) {
                listener.onItemClick(view,getAdapterPosition());
            }
        }
    }
    public interface ItemListener{
        void onItemClick(View view,int position);
    }
    public interface OnItemToggleListener{
        void onItemToggle(int position, boolean isChecked);
    }
}
