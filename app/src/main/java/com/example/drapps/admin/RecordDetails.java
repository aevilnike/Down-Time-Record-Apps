package com.example.drapps.admin;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drapps.R;
import com.example.drapps.adapter.ImageAdapter;
import com.example.drapps.user.HistoryDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecordDetails extends AppCompatActivity {
    private ImageView imageEvidence;
    private ImageButton closeBtn;
    private TextView nameTv,dateTv,startTimeTv,endTimeTv,linesubTv,equipmentTv,actionTv,teamTv;
    private TextView pbTv1,pbTv2,pbTv3,pbTv4,pbTv5,pbTv6,pbTv7,pbTv8,pbTv9,pbTv10,pbTv11,pbTv12,pbTv13,pbTv14;
    private TextView noTv1,noTv2,noTv3,noTv4,noTv5,noTv6,noTv7,noTv8,noTv9,noTv10,noTv11,noTv12,noTv13,noTv14;
    private String recordUid, userId;
    private TableLayout tableLayout;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerViewImages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);

        nameTv = findViewById(R.id.nameTv);
        dateTv = findViewById(R.id.dateTv);
        startTimeTv = findViewById(R.id.startTimeTv);
        endTimeTv = findViewById(R.id.endTimeTv);
        linesubTv = findViewById(R.id.linesubTv);
        equipmentTv = findViewById(R.id.equipmentTv);
        actionTv = findViewById(R.id.actionTv);
        teamTv = findViewById(R.id.teamTv);
        closeBtn = findViewById(R.id.closeBtn);
        //imageRecyclerView = findViewById(R.id.imageRecyclerView);
        tableLayout = findViewById(R.id.tableLayout);
        imageEvidence = findViewById(R.id.imageEvidence);
        //pb
        pbTv1 = findViewById(R.id.pbTv1);
        pbTv2 = findViewById(R.id.pbTv2);
        pbTv3 = findViewById(R.id.pbTv3);
        pbTv4 = findViewById(R.id.pbTv4);
        pbTv5 = findViewById(R.id.pbTv5);
        pbTv6 = findViewById(R.id.pbTv6);
        pbTv7 = findViewById(R.id.pbTv7);
        pbTv8 = findViewById(R.id.pbTv8);
        pbTv9 = findViewById(R.id.pbTv9);
        pbTv10 = findViewById(R.id.pbTv10);
        pbTv11 = findViewById(R.id.pbTv11);
        pbTv12 = findViewById(R.id.pbTv12);
        pbTv13 = findViewById(R.id.pbTv13);
        pbTv14 = findViewById(R.id.pbTv14);

        noTv1 = findViewById(R.id.noTv1);
        noTv2 = findViewById(R.id.noTv2);
        noTv3 = findViewById(R.id.noTv3);
        noTv4 = findViewById(R.id.noTv4);
        noTv5 = findViewById(R.id.noTv5);
        noTv6 = findViewById(R.id.noTv6);
        noTv7 = findViewById(R.id.noTv7);
        noTv8 = findViewById(R.id.noTv8);
        noTv9 = findViewById(R.id.noTv9);
        noTv10 = findViewById(R.id.noTv10);
        noTv11 = findViewById(R.id.noTv11);
        noTv12 = findViewById(R.id.noTv12);
        noTv13 = findViewById(R.id.noTv13);
        noTv14 = findViewById(R.id.noTv14);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);

        //get uid of the shop from intent
        recordUid = getIntent().getStringExtra("recordUid");
        userId = getIntent().getStringExtra("userId");
        System.out.println(recordUid);
        System.out.println(userId);
        firebaseAuth = FirebaseAuth.getInstance();
        
        loadUserRecordDetails();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
    String evidenceImage;
    private void loadUserRecordDetails() {
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
                        String employeeName = String.valueOf(recordSnapshot.child("employeeName").getValue());
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
                        evidenceImage = String.valueOf(recordSnapshot.child("evidenceImage").getValue());
                        loadImagesFromFirebase(recordSnapshot);

                        // Set data to TextViews, handling null values
                        nameTv.setText(employeeName != null ? employeeName : "");
                        dateTv.setText(date != null ? date : "");
                        startTimeTv.setText(startTime != null ? startTime : "");
                        endTimeTv.setText(finishTime != null ? finishTime : "");
                        linesubTv.setText(linesub != null ? linesub : "");
                        actionTv.setText(action != null ? action : "");
                        teamTv.setText(teamMember != null ? teamMember : "");
                        try {
                            Picasso.get().load(evidenceImage).into(imageEvidence);
                        } catch (Exception e) {
                            imageEvidence.setImageResource(R.drawable.baseline_image_24);
                        }
                        // Create arrays for pbEct and pbSaim TextViews
                        TextView[] pbEctTextViews = {pbTv1, pbTv2, pbTv3, pbTv4, pbTv5, pbTv6, pbTv7};
                        TextView[] pbSaimTextViews = {pbTv8, pbTv9, pbTv10, pbTv11, pbTv12, pbTv13, pbTv14};
                        TextView[] noTvEct = {noTv1, noTv2, noTv3, noTv4, noTv5, noTv6, noTv7};
                        TextView[] noTvSaim = {noTv8, noTv9, noTv10, noTv11, noTv12, noTv13, noTv14};
                        // Initialize row counter
                        int rowCounter = 1;
                        String[] pbEct = {pbEct1, pbEct2, pbEct3, pbEct4, pbEct5, pbEct6, pbEct7};
                        // Loop through pbEct TextViews
                        for (int i = 0; i < pbEctTextViews.length; i++) {
                            if ("null".equals(pbEct[i])) {
                                // If data is "null", make TextViews gone
                                pbEctTextViews[i].setVisibility(View.GONE);
                                noTvEct[i].setVisibility(View.GONE);
                            } else if (pbEct[i] != null && !pbEct[i].isEmpty()) {
                                // Set data to pbEct TextView
                                pbEctTextViews[i].setText(pbEct[i]);

                                // Set row number to noTv
                                noTvEct[i].setText(String.valueOf(rowCounter++));

                                // Make TextViews visible
                                pbEctTextViews[i].setVisibility(View.VISIBLE);
                                noTvEct[i].setVisibility(View.VISIBLE);
                            } else {
                                // Make TextViews gone if they have no data
                                pbEctTextViews[i].setVisibility(View.GONE);
                                noTvEct[i].setVisibility(View.GONE);
                            }
                        }

                        // Reset row counter
                        rowCounter = 1;
                        String[] pbSaim = {pbSaim1, pbSaim2, pbSaim3, pbSaim4, pbSaim5, pbSaim6, pbSaim7};
                        // Loop through pbSaim TextViews
                        for (int i = 0; i < pbSaimTextViews.length; i++) {
                            if ("null".equals(pbSaim[i])) {
                                // If data is "null", make TextViews gone
                                pbSaimTextViews[i].setVisibility(View.GONE);
                                noTvSaim[i].setVisibility(View.GONE);
                            } else if (pbSaim[i] != null && !pbSaim[i].isEmpty()) {
                                // Set data to pbSaim TextView
                                pbSaimTextViews[i].setText(pbSaim[i]);

                                // Set row number to noTv
                                noTvSaim[i].setText(String.valueOf(rowCounter++));

                                // Make TextViews visible
                                pbSaimTextViews[i].setVisibility(View.VISIBLE);
                                noTvSaim[i].setVisibility(View.VISIBLE);
                            } else {
                                // Make TextViews gone if they have no data
                                pbSaimTextViews[i].setVisibility(View.GONE);
                                noTvSaim[i].setVisibility(View.GONE);
                            }
                        }

                        if (!ect.isEmpty() && !saim.isEmpty()) {
                            equipmentTv.setText(ect + " & " + saim);
                        } else if (!ect.isEmpty()) {
                            equipmentTv.setText(saim);
                        } else if (!saim.isEmpty()) {
                            equipmentTv.setText(ect);
                        } else {
                            equipmentTv.setText("");
                        }

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
        List<String> imageUrls = new ArrayList<>();
        // Assuming the image URLs are stored as evidenceImage0, evidenceImage1, ...
        for (DataSnapshot childSnapshot : recordSnapshot.getChildren()) {
            String key = childSnapshot.getKey();
            if (key.startsWith("evidenceImage")) {
                String imageUrl = String.valueOf(childSnapshot.getValue());
                imageUrls.add(imageUrl);
            }
        }

        // Load images into RecyclerView
        addImageToRecyclerView(imageUrls);
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
            recyclerViewImages.setLayoutManager(new LinearLayoutManager(RecordDetails.this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewImages.setAdapter(imageAdapter);
        } else {
            imageAdapter.notifyDataSetChanged();
        }
    }

}