package com.example.drapps.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drapps.R;
import com.example.drapps.adapter.EntryAdapter;
import com.example.drapps.adapter.ImageAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryDetails extends AppCompatActivity {
    private String recordUid, userId;
    private ImageView backBtn;
    private RecyclerView rcEct;
    private RecyclerView rcSaim;
    private TextView recordUidTv, dateTv, startTimeTv, finishTimeTv, lineSubTv, equipmentTv, actionTv, teamMemberTv;
    private ImageView imageEvidence;
    private RecyclerView recyclerViewImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        backBtn = findViewById(R.id.backBtn);
        recordUidTv = findViewById(R.id.recordUidTv);
        teamMemberTv = findViewById(R.id.teamMemberTv);
        actionTv = findViewById(R.id.actionTv);
        equipmentTv = findViewById(R.id.equipmentTv);
        lineSubTv = findViewById(R.id.lineSubTv);
        finishTimeTv = findViewById(R.id.finishTimeTv);
        startTimeTv = findViewById(R.id.startTimeTv);
        dateTv = findViewById(R.id.dateTv);
        //imageEvidence = findViewById(R.id.imageEvidence);
        rcEct = findViewById(R.id.rcEct);
        rcSaim = findViewById(R.id.rcSaim);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);

        //get uid of the shop from intent
        recordUid = getIntent().getStringExtra("recordUid");
        userId = getIntent().getStringExtra("userId");
        if (recordUid != null && userId != null) {
            loadHistoryDetails();
        } else {
        }
        System.out.println(recordUid);
        System.out.println(userId);

        loadHistoryDetails();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryDetails.this, BottomNavigation.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("fragmentToOpen", "HistoryFragment");
                startActivity(intent);
                finish();
            }
        });

    }

    private void loadHistoryDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("UserRecord");

        // Use addListenerForSingleValueEvent instead of addValueEventListener
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming you have a way to determine which record to display based on recordUid
                    DataSnapshot recordSnapshot = snapshot.child(recordUid);
                    if (recordSnapshot.exists()) {
                        // Retrieve data for the selected record
                        String userRecordId = String.valueOf(recordSnapshot.child("userRecordId").getValue());
                        String date = String.valueOf(recordSnapshot.child("date").getValue());
                        String startTime = String.valueOf(recordSnapshot.child("startTime").getValue());
                        String finishTime = String.valueOf(recordSnapshot.child("finishTime").getValue());
                        String linesub = String.valueOf(recordSnapshot.child("linesub").getValue());
                        String action = String.valueOf(recordSnapshot.child("action").getValue());
                        String teamMember = String.valueOf(recordSnapshot.child("teamMember").getValue());
                        String ect = String.valueOf(recordSnapshot.child("ect").getValue());
                        String saim = String.valueOf(recordSnapshot.child("saim").getValue());
                        String pbEct1 = String.valueOf(recordSnapshot.child("pbEct1").getValue());
                        String pbEct2 = String.valueOf(recordSnapshot.child("pbEct2").getValue());
                        String pbEct3 = String.valueOf(recordSnapshot.child("pbEct3").getValue());
                        String pbEct4 = String.valueOf(recordSnapshot.child("pbEct4").getValue());
                        String pbEct5 = String.valueOf(recordSnapshot.child("pbEct5").getValue());
                        String pbEct6 = String.valueOf(recordSnapshot.child("pbEct6").getValue());
                        String pbEct7 = String.valueOf(recordSnapshot.child("pbEct7").getValue());
                        String pbSaim1 = String.valueOf(recordSnapshot.child("pbSaim1").getValue());
                        String pbSaim2 = String.valueOf(recordSnapshot.child("pbSaim2").getValue());
                        String pbSaim3 = String.valueOf(recordSnapshot.child("pbSaim3").getValue());
                        String pbSaim4 = String.valueOf(recordSnapshot.child("pbSaim4").getValue());
                        String pbSaim5 = String.valueOf(recordSnapshot.child("pbSaim5").getValue());
                        String pbSaim6 = String.valueOf(recordSnapshot.child("pbSaim6").getValue());
                        String pbSaim7 = String.valueOf(recordSnapshot.child("pbSaim7").getValue());
                        loadImagesFromFirebase(recordSnapshot);

                        // Set data to TextViews, handling null values
                        recordUidTv.setText(userRecordId != null ? userRecordId : "");
                        dateTv.setText(date != null ? date : "");
                        startTimeTv.setText(startTime != null ? startTime : "");
                        finishTimeTv.setText(finishTime != null ? finishTime : "");
                        lineSubTv.setText(linesub != null ? linesub : "");
                        actionTv.setText(action != null ? action : "");
                        teamMemberTv.setText(teamMember != null ? teamMember : "");

                        // Set equipment value based on ect and saim
                        if (!ect.isEmpty() && !saim.isEmpty() && !ect.equals("null") && !saim.equals("null")) {
                            equipmentTv.setText(ect + " & " + saim);
                        } else if (saim.equals("null")) {
                            equipmentTv.setText(ect);
                        } else if (ect.equals("null")) {
                            equipmentTv.setText(saim);
                        } else {
                            equipmentTv.setText("");
                        }
                        // Check for "null" values and set the text to empty if encountered
                        if (equipmentTv.getText().toString().equals("null")) {
                            equipmentTv.setText("");
                        }
                        // Populate RecyclerViews with pbEct and pbSaim values
                        populateRecyclerViewWithEntries(Arrays.asList(pbEct1, pbEct2, pbEct3, pbEct4, pbEct5, pbEct6, pbEct7), rcEct);
                        populateRecyclerViewWithEntries(Arrays.asList(pbSaim1, pbSaim2, pbSaim3, pbSaim4, pbSaim5, pbSaim6, pbSaim7), rcSaim);

                    } else {
                        Log.d("FirebaseData", "Selected record does not exist");
                    }
                } else {
                    Log.d("FirebaseData", "Data does not exist for UserRecord");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error loading user record details: " + error.getMessage());
            }
        });
    }
    private void loadImagesFromFirebase(DataSnapshot recordSnapshot) {
        if (recordSnapshot != null) {
            List<String> imageUrls = new ArrayList<>();
            // Assuming the image URLs are stored as evidenceImage0, evidenceImage1, ...
            for (DataSnapshot childSnapshot : recordSnapshot.getChildren()) {
                String key = childSnapshot.getKey();
                if (key != null && key.startsWith("evidenceImage")) {
                    String imageUrl = String.valueOf(childSnapshot.getValue());
                    imageUrls.add(imageUrl);
                }
            }

            // Load images into RecyclerView
            addImageToRecyclerView(imageUrls);
        } else {
            Log.e("HistoryDetails", "recordSnapshot is null");
        }
    }
    ImageAdapter imageAdapter;
    private List<Uri> imageList;
    private void addImageToRecyclerView(List<String> imageUrls) {
        // Make the RecyclerView visible if it was hidden
        recyclerViewImages.setVisibility(View.VISIBLE);

        // Create a new list to hold the image URIs (if not already initialized)
        if (imageList == null) {
            imageList = new ArrayList<>();
        }

        // Clear the existing image list
        imageList.clear();

        // Convert image URLs to URI and add them to the list
        for (String imageUrl : imageUrls) {
            imageList.add(Uri.parse(imageUrl));
        }

        // Notify the adapter of the RecyclerView about the data change
        if (imageAdapter == null) {
            imageAdapter = new ImageAdapter(imageList);
            recyclerViewImages.setLayoutManager(new LinearLayoutManager(HistoryDetails.this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewImages.setAdapter(imageAdapter);
        } else {
            imageAdapter.notifyDataSetChanged();
        }
    }
    private void populateRecyclerViewWithEntries(List<String> entries, RecyclerView recyclerView) {
        if (recyclerView != null && entries != null) {
            // Filter out null entries
            List<String> filteredEntries = new ArrayList<>();
            for (String entry : entries) {
                if (entry != null && !"null".equals(entry)) {
                    filteredEntries.add(entry);
                }
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            EntryAdapter adapter = new EntryAdapter(filteredEntries);
            recyclerView.setAdapter(adapter);
        }
    }

}
