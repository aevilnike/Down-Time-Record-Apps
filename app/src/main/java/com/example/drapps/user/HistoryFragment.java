package com.example.drapps.user;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drapps.R;
import com.example.drapps.adapter.AdapterHistory;
import com.example.drapps.adapter.AdapterRecord;
import com.example.drapps.model.ModelRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private RecyclerView recordRv;
    private ArrayList<ModelRecord> recordList;
    private AdapterHistory adapterHistory;
    private ImageButton backBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Context context; // Store the context
    private EditText searchEditText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context; // Store the context when fragment is attached
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null; // Release the context when fragment is detached
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_submit, container, false);

        recordRv = view.findViewById(R.id.recordRv);
        backBtn = view.findViewById(R.id.backBtn);
        searchEditText = view.findViewById(R.id.searchEditText);

        // 1. Obtain the UID of the authenticated user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userUid = currentUser.getUid();

            // 2. Pass the UID to the loadHistory() method
            loadHistory(userUid);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterHistory.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

        private void loadHistory(String userUid) {
            recordList = new ArrayList<>();
            DatabaseReference userRecordRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userUid)
                    .child("UserRecord");

            userRecordRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot recordSnapshot) {
                    recordList.clear();
                    for (DataSnapshot ds: recordSnapshot.getChildren()) {
                        ModelRecord modelRecord = ds.getValue(ModelRecord.class);
                        recordList.add(modelRecord);
                    }
                    adapterHistory = new AdapterHistory(context, recordList);
                    recordRv.setAdapter(adapterHistory);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });
        }
}
