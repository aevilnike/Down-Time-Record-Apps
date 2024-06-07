package com.example.drapps.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drapps.R;
import com.example.drapps.adapter.AdapterRecord;
import com.example.drapps.admin.RecordActivity;
import com.example.drapps.model.ModelRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubmitActivity extends AppCompatActivity {

    private RecyclerView recordRv;
    private ArrayList<ModelRecord> recordList;
    private AdapterRecord adapterRecord;
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        recordRv = findViewById(R.id.recordRv);
        backBtn = findViewById(R.id.backBtn);

        loadHistory();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadHistory() {
        recordList = new ArrayList<>();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                recordList.clear();
                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                    String userUid = userSnapshot.getKey(); // Get the UID of each user

                    DatabaseReference userRecordRef = usersRef.child(userUid).child("UserRecord");

                    userRecordRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot recordSnapshot) {
                            if (recordSnapshot.exists()) {
                                for (DataSnapshot ds: recordSnapshot.getChildren()) {//loop get all the order on current user
                                    ModelRecord modelRecord = ds.getValue(ModelRecord.class);
                                    //add to list
                                    recordList.add(modelRecord); //put into orderList array
                                }
                                //setup adapter
                                adapterRecord = new AdapterRecord(SubmitActivity.this, recordList);
                                //set to recyclerview
                                recordRv.setAdapter(adapterRecord);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled if needed
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }
}