package com.example.recylerequestapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recylerequestapp.R;
import com.example.recylerequestapp.model.Item;

import java.util.List;


public class RecycleItemAdapter extends RecyclerView.Adapter<RecycleItemAdapter.ViewHolder> {

    private final List<Item> itemList;
    private final OnItemSelectListener listener;

    public interface OnItemSelectListener {
        void onItemSelect(Item item);
    }

    public RecycleItemAdapter(Context context, List<Item> itemList, OnItemSelectListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPrice;

        private final View btnSelect;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvItemName);
            tvPrice = view.findViewById(R.id.tvPricePerKg);
            btnSelect = view.findViewById(R.id.btnSelectItem);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Item item, OnItemSelectListener listener) {
            tvName.setText(item.getItem_name());
            tvPrice.setText("Price: RM " + item.getPrice_per_kg() + " /kg");
            btnSelect.setOnClickListener(view -> listener.onItemSelect(item));
        }
    }

    @NonNull
    @Override
    public RecycleItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleItemAdapter.ViewHolder holder, int position) {
        holder.bind(itemList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

