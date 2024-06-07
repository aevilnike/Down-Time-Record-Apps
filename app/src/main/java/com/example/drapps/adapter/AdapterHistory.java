package com.example.drapps.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drapps.FilterHistory;
import com.example.drapps.FilterRecord;
import com.example.drapps.R;
import com.example.drapps.model.ModelRecord;
import com.example.drapps.user.HistoryDetails;

import java.util.ArrayList;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.HolderRecord> implements Filterable {
    private Context context;
    public ArrayList<ModelRecord> recordList;
    private ArrayList<ModelRecord> filterList;
    private FilterHistory filter;

    public AdapterHistory(Context context, ArrayList<ModelRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        this.filterList = recordList;
    }

    @NonNull
    @Override
    public HolderRecord onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_history, parent, false);
        return new HolderRecord(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecord holder, int position) {
        ModelRecord modelRecord = recordList.get(position);
        final String uid = modelRecord.getUid();
        String date = modelRecord.getDate();
        String recordUid = modelRecord.getUserRecordId();

        holder.uidTv.setText(recordUid);
        holder.dateTv.setText(date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HistoryDetails.class);
                intent.putExtra("recordUid", recordUid);
                intent.putExtra("userId", uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterHistory(this, filterList);
        }
        return filter;
    }

    class HolderRecord extends RecyclerView.ViewHolder {
        private TextView uidTv, dateTv;

        public HolderRecord(@NonNull View itemView) {
            super(itemView);

            uidTv = itemView.findViewById(R.id.uidTv);
            dateTv = itemView.findViewById(R.id.dateTv);
        }
    }
}
