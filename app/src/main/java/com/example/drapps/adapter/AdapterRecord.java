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

import com.example.drapps.FilterRecord;
import com.example.drapps.model.ModelRecord;
import com.example.drapps.R;
import com.example.drapps.admin.RecordDetails;

import java.util.ArrayList;

public class AdapterRecord extends RecyclerView.Adapter<AdapterRecord.HolderRecord> implements Filterable {

    private Context context;
    public ArrayList<ModelRecord>recordList;
    private ArrayList<ModelRecord> filterList;
    private OnItemClickListener listener;
    private FilterRecord filter;
    public AdapterRecord(Context context, ArrayList<ModelRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        this.filterList = recordList;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterRecord(this, filterList);
        }
        return filter;
    }

    public interface OnItemClickListener {
        void onFixClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HolderRecord onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_list, parent, false);
        return new HolderRecord(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecord holder, int position) {
        //Get data
        ModelRecord modelRecord = recordList.get(position);
        final String uid = modelRecord.getUid();
        String employeeName = modelRecord.getEmployeeName();
        String employeeNo = modelRecord.getEmployeeNo();
        String linesub = modelRecord.getLinesub();
        String teamMember = modelRecord.getTeamMember();
        String action = modelRecord.getAction();
        String ect = modelRecord.getEct();
        String saim = modelRecord.getSaim();
        String pbEct1 = modelRecord.getPbEct1();
        String pbEct2 = modelRecord.getPbEct2();
        String pbEct3 = modelRecord.getPbEct3();
        String pbEct4 = modelRecord.getPbEct4();
        String pbEct5 = modelRecord.getPbEct5();
        String pbEct6 = modelRecord.getPbEct6();
        String pbEct7 = modelRecord.getPbEct7();
        String pbSaim1 = modelRecord.getPbSaim1();
        String pbSaim2 = modelRecord.getPbSaim2();
        String pbSaim3 = modelRecord.getPbSaim3();
        String pbSaim4 = modelRecord.getPbSaim4();
        String pbSaim5 = modelRecord.getPbSaim5();
        String pbSaim6 = modelRecord.getPbSaim6();
        String pbSaim7 = modelRecord.getPbSaim7();
        String startTime = modelRecord.getStartTime();
        String finishTime = modelRecord.getFinishTime();
        String date = modelRecord.getDate();
        String evidence = modelRecord.getEvidence();
        String recordUid = modelRecord.getUserRecordId();

        //Set data
        holder.nameTv.setText(employeeName);
        holder.dateTv.setText(date);
        holder.noPkTv.setText(employeeNo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RecordDetails.class);
                intent.putExtra("recordUid", recordUid);
                intent.putExtra("userId", uid);
                context.startActivity(intent);
            }
        });
        // Set click listeners for buttons
        holder.fixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFixClick(position);
                }
            }
        });

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class HolderRecord extends RecyclerView.ViewHolder {
        private TextView nameTv,dateTv,fixBtn,delBtn,noPkTv;

        public HolderRecord(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            noPkTv = itemView.findViewById(R.id.noPkTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            fixBtn = itemView.findViewById(R.id.fixBtn);
            delBtn = itemView.findViewById(R.id.delBtn);


        }
    }
}
