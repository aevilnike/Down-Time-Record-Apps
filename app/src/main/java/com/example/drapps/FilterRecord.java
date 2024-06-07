package com.example.drapps;

import android.widget.Filter;

import com.example.drapps.adapter.AdapterRecord;
import com.example.drapps.model.ModelRecord;

import java.util.ArrayList;

public class FilterRecord extends Filter {
    private AdapterRecord adapterRecord;
    private ArrayList<ModelRecord> filterList;

    public FilterRecord(AdapterRecord adapterRecord, ArrayList<ModelRecord> filterList) {
        this.adapterRecord = adapterRecord;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if (charSequence != null && charSequence.length() > 0) {
            //search filed not empty, searching something, perform search

            //change to upper case, to make case insensitive
            charSequence = charSequence.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelRecord> filterModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                //check, search by tittle and category
                if (filterList.get(i).getEmployeeName().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getDate().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getEmployeeNo().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getUserRecordId().toUpperCase().contains(charSequence)) {
                    //add filtered data to list
                    filterModels.add(filterList.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;

        } else {
            //search filed empty, not searching, return original/all/complete list
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterRecord.recordList = (ArrayList<ModelRecord>) filterResults.values;
        //refresh adapter
        adapterRecord.notifyDataSetChanged();
    }
}
