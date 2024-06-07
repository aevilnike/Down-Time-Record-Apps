package com.example.drapps;

import android.widget.Filter;

import com.example.drapps.adapter.AdapterHistory;
import com.example.drapps.model.ModelRecord;

import java.util.ArrayList;

public class FilterHistory extends Filter {
    private AdapterHistory adapterHistory;
    private ArrayList<ModelRecord> filterListHistory;

    public FilterHistory(AdapterHistory adapterHistory, ArrayList<ModelRecord> filterListHistory) {
        this.adapterHistory = adapterHistory;
        this.filterListHistory = filterListHistory;
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
            for (int i = 0; i < filterListHistory.size(); i++) {
                //check, search by tittle and category
                if (filterListHistory.get(i).getDate().toUpperCase().contains(charSequence) ||
                        filterListHistory.get(i).getUserRecordId().toUpperCase().contains(charSequence)) {
                    //add filtered data to list
                    filterModels.add(filterListHistory.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;
        } else {
            //search filed empty, not searching, return original/all/complete list
            results.count = filterListHistory.size();
            results.values = filterListHistory;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterHistory.recordList = (ArrayList<ModelRecord>) filterResults.values;
        //refresh adapter
        adapterHistory.notifyDataSetChanged();
    }
}
