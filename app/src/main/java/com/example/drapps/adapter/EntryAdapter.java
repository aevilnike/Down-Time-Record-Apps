package com.example.drapps.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drapps.R;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private List<String> entryList;

    public EntryAdapter(List<String> entryList) {
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history_problem, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        String entry = entryList.get(position);
        holder.entryTextView.setText(entry);
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView entryTextView;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            entryTextView = itemView.findViewById(R.id.problemTv);
        }
    }
}