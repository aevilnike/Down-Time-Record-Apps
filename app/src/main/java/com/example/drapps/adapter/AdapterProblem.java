package com.example.drapps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drapps.R;
import com.example.drapps.model.ModelRecord;

import java.util.ArrayList;

public class AdapterProblem extends RecyclerView.Adapter<AdapterProblem.HolderProblem>{

    private Context context;
    private ArrayList<ModelRecord> recordList;

    public AdapterProblem(Context context, ArrayList<ModelRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
    }



    @NonNull
    @Override
    public HolderProblem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_problemlist, parent, false);
        return new HolderProblem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProblem holder, int position) {
        //Get data
        ModelRecord modelRecord = recordList.get(position);
        final String uid = modelRecord.getUid();
        String recordUid = modelRecord.getUserRecordId();
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

        //Set data
        for(int i=1; i<=14; i++){
            holder.noTv.setText(i+".");
        }

        holder.pbTv.setText(pbEct1);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class HolderProblem extends RecyclerView.ViewHolder {

        TextView noTv,pbTv;
        public HolderProblem(@NonNull View itemView) {
            super(itemView);

        }
    }
}
