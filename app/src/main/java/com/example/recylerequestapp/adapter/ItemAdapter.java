package com.example.recylerequestapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recylerequestapp.R;
import com.example.recylerequestapp.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final List<Item> itemList;
    private final OnEditClickListener editClickListener;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(Item item);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Item item);
    }

    public ItemAdapter(List<Item> itemList, OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener) {
        this.itemList = itemList;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvPoint;
        Button btnEdit, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvPricePerKg);
            tvPoint = itemView.findViewById(R.id.tvPointsPerKg);
            btnEdit = itemView.findViewById(R.id.btnEditItem);
            btnDelete = itemView.findViewById(R.id.btnDeleteItem);
        }

        public void bind(Item item) {
            tvName.setText("Item: " + item.getItem_name());
            tvPrice.setText("RM " + item.getPrice_per_kg() + " / kg");
            btnEdit.setOnClickListener(v -> editClickListener.onEditClick(item));
            btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(item));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}


