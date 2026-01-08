package com.example.recylerequestapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recylerequestapp.R;
import com.example.recylerequestapp.model.Request;

import java.util.List;

public class RequestHistoryAdapter extends RecyclerView.Adapter<RequestHistoryAdapter.ViewHolder> {

    private final List<Request> requestList;
    private final OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, Request request);
    }

    public RequestHistoryAdapter(List<Request> requestList, OnItemLongClickListener longClickListener) {
        this.requestList = requestList;
        this.longClickListener = longClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequestDate, tvItemName, tvWeight, tvAddress, tvNote, tvTotalPrice, tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRequestDate = itemView.findViewById(R.id.tvRequestDate);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Request request) {
            tvRequestDate.setText("Date: " + request.getRequest_date());

            if (request.getItem() != null) {
                tvItemName.setText("Item: " + request.getItem().getItem_name());
            } else {
                tvItemName.setText("Item: Unknown");
            }

            tvWeight.setText("Weight: " + request.getWeight() + " kg");
            tvAddress.setText("Address: " + request.getAddress());
            tvNote.setText("Note: " + request.getNotes());
            tvTotalPrice.setText("Price: RM " + request.getTotal_price());
            tvStatus.setText("Status: " + request.getStatus());

            itemView.setOnLongClickListener(v -> {
                longClickListener.onItemLongClick(v, request);
                return true;
            });
        }
    }

    @NonNull
    @Override
    public RequestHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_recycle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHistoryAdapter.ViewHolder holder, int position) {
        holder.bind(requestList.get(position));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}

