package com.example.drapps.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drapps.Constants;
import com.example.drapps.R;
import com.example.drapps.adapter.AdapterRecord;
import com.example.drapps.model.ModelRecord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    public String problemUid,sendBy;
    private ImageButton backBtn, delAllBtn,exportToExcelBtn;
    private TextView employeeCountTv,dataCountTv,fixedCountTv;
    private RecyclerView recordRv;
    private ArrayList<ModelRecord> recordList;
    private AdapterRecord adapterRecord;
    private FirebaseAuth firebaseAuth;
    private EditText searchEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        recordRv = findViewById(R.id.recordRv);
        backBtn = findViewById(R.id.backBtn);
        employeeCountTv = findViewById(R.id.employeeCountTv);
        dataCountTv = findViewById(R.id.dataCountTv);
        fixedCountTv = findViewById(R.id.fixedCountTv);
        searchEditText = findViewById(R.id.searchEditText);
        delAllBtn = findViewById(R.id.delAllBtn);
        exportToExcelBtn = findViewById(R.id.exportToExcelBtn);
        //get data from intent
        problemUid = getIntent().getStringExtra("problemUid");
        sendBy = getIntent().getStringExtra("sendBy");

        firebaseAuth = FirebaseAuth.getInstance();

        loadRecord();
        loadCounts();
        delAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                builder.setTitle("Delete All Records");
                builder.setMessage("Are you sure you want to delete all records?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Call the method to delete all records
                        deleteRecordsForAllUsers();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        exportToExcelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToExcel();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterRecord.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void exportToExcel() {
        try {
            // Create a new Excel sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Records");
            //Create styles
            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);

            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Employee No.");
            headerRow.createCell(1).setCellValue("Employee Name");
            headerRow.createCell(2).setCellValue("Date");
            headerRow.createCell(3).setCellValue("Start Time");
            headerRow.createCell(4).setCellValue("Finish Time");
            headerRow.createCell(5).setCellValue("Linesub");
            headerRow.createCell(6).setCellValue("Action");
            headerRow.createCell(7).setCellValue("Team Member");
            headerRow.createCell(8).setCellValue("ECT");
            headerRow.createCell(9).setCellValue("SAIM");
            headerRow.createCell(10).setCellValue("PB ECT1");
            headerRow.createCell(11).setCellValue("PB ECT2");
            headerRow.createCell(12).setCellValue("PB ECT3");
            headerRow.createCell(13).setCellValue("PB ECT4");
            headerRow.createCell(14).setCellValue("PB ECT5");
            headerRow.createCell(15).setCellValue("PB ECT6");
            headerRow.createCell(16).setCellValue("PB ECT7");
            headerRow.createCell(17).setCellValue("PB SAIM1");
            headerRow.createCell(18).setCellValue("PB SAIM2");
            headerRow.createCell(19).setCellValue("PB SAIM3");
            headerRow.createCell(20).setCellValue("PB SAIM4");
            headerRow.createCell(21).setCellValue("PB SAIM5");
            headerRow.createCell(22).setCellValue("PB SAIM6");
            headerRow.createCell(23).setCellValue("PB SAIM7");
            headerRow.createCell(24).setCellValue("Evidence Image");

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                    final int[] rowNum = {1};
                    for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                        String userUid = userSnapshot.getKey(); // Get the UID of each user
                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(userUid)
                                .child("UserRecord");

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                                        // Retrieve data for each record
                                        String employeeName = String.valueOf(recordSnapshot.child("employeeName").getValue());
                                        String employeeNo = String.valueOf(recordSnapshot.child("employeeNo").getValue());
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
                                        String evidenceImage = String.valueOf(recordSnapshot.child("evidenceImage").getValue());

                                        // Add data rows
                                        Row row = sheet.createRow(rowNum[0]++);
                                        row.createCell(0).setCellValue(employeeNo);
                                        row.createCell(1).setCellValue(employeeName);
                                        row.createCell(2).setCellValue(date);
                                        row.createCell(3).setCellValue(startTime);
                                        row.createCell(4).setCellValue(finishTime);
                                        row.createCell(5).setCellValue(linesub);
                                        row.createCell(6).setCellValue(action);
                                        row.createCell(7).setCellValue(teamMember);
                                        row.createCell(8).setCellValue(ect);
                                        row.createCell(9).setCellValue(saim);
                                        row.createCell(10).setCellValue(pbEct1);
                                        row.createCell(11).setCellValue(pbEct2);
                                        row.createCell(12).setCellValue(pbEct3);
                                        row.createCell(13).setCellValue(pbEct4);
                                        row.createCell(14).setCellValue(pbEct5);
                                        row.createCell(15).setCellValue(pbEct6);
                                        row.createCell(16).setCellValue(pbEct7);
                                        row.createCell(17).setCellValue(pbSaim1);
                                        row.createCell(18).setCellValue(pbSaim2);
                                        row.createCell(19).setCellValue(pbSaim3);
                                        row.createCell(20).setCellValue(pbSaim4);
                                        row.createCell(21).setCellValue(pbSaim5);
                                        row.createCell(22).setCellValue(pbSaim6);
                                        row.createCell(23).setCellValue(pbSaim7);
                                        row.createCell(24).setCellValue(evidenceImage);

                                        // Apply style to header row
                                        for (int i = 0; i <= 24; i++) {
                                            headerRow.getCell(i).setCellStyle(headerStyle);
                                        }
                                    }

                                    // Save the workbook to a file
                                    File file = new File(getExternalCacheDir(), "AllRecords.xlsx");
                                    try (FileOutputStream fos = new FileOutputStream(file)) {
                                        workbook.write(fos);
                                        fos.close();
                                        workbook.close();
                                        // Share the Excel sheet
                                        shareExcelFile(file);
                                    } catch (FileNotFoundException e) {
                                        throw new RuntimeException(e);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("FirebaseError", "Error loading user record details: " + error.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Error loading user records: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void shareExcelFile(File file) {
        if (file != null && file.exists()) {
            // Create an intent to share the file
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/vnd.ms-excel");
            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Excel File"));
        } else {
            Log.d("ShareExcel", "File not found");
        }
    }

    private void deleteRecordsForAllUsers() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Read all users
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userUid = userSnapshot.getKey();
                    DatabaseReference userRecordRef = usersRef.child(userUid).child("UserRecord");

                    // Remove all records for this user
                    userRecordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot recordSnapshot : dataSnapshot.getChildren()) {
                                // Retrieve image URLs associated with the record
                                List<String> imageUrls = new ArrayList<>();
                                for (DataSnapshot childSnapshot : recordSnapshot.getChildren()) {
                                    String key = childSnapshot.getKey();
                                    if (key.startsWith("evidenceImage")) {
                                        String imageUrl = String.valueOf(childSnapshot.getValue());
                                        imageUrls.add(imageUrl);
                                    }
                                }

                                // Delete images associated with the record from storage
                                for (String imageUrl : imageUrls) {
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Image deleted successfully
                                                Log.d("DeleteRecords", "Image deleted successfully: " + imageUrl);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle failure to delete image
                                                Log.e("DeleteRecords", "Failed to delete image: " + imageUrl, e);
                                            }
                                        });
                                    }
                                }

                                // Remove the record from the database
                                recordSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Record deleted successfully
                                        Log.d("DeleteRecords", "Record deleted successfully");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle failure to delete the record
                                        Log.e("DeleteRecords", "Failed to delete record: " + e.getMessage(), e);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                            Log.e("DeleteRecords", "Failed to read user records: " + databaseError.getMessage(), databaseError.toException());
                        }
                    });
                }

                // Notify the user
                Toast.makeText(RecordActivity.this, "Records and associated images for all users deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("DeleteRecords", "Failed to read users: " + databaseError.getMessage(), databaseError.toException());
            }
        });
    }
    private void loadRecord() {
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
                                recordList.clear(); // Clear the list before adding new data
                                for (DataSnapshot ds: recordSnapshot.getChildren()) {
                                    ModelRecord modelRecord = ds.getValue(ModelRecord.class);
                                    recordList.add(modelRecord); // Add each record to the list
                                }

                                // Setup adapter
                                adapterRecord = new AdapterRecord(RecordActivity.this, recordList);
                                // Set adapter to recyclerview
                                recordRv.setAdapter(adapterRecord);

                                // Set click listeners for buttons inside the adapter
                                adapterRecord.setOnItemClickListener(new AdapterRecord.OnItemClickListener() {
                                    @Override
                                    public void onFixClick(int position) {
                                        handleFixButtonClick(position);
                                        Toast.makeText(RecordActivity.this, "Record Fixed", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onDeleteClick(int position) {
                                        handleDeleteButtonClick(position);
                                        Toast.makeText(RecordActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }
    private void handleDeleteButtonClick(int position) {
        ModelRecord modelRecord = recordList.get(position);
        String recordUid = modelRecord.getUserRecordId(); // Assuming modelRecord contains the record's UID
        String uid = modelRecord.getUid();
        Log.d("DeleteButtonClick", "Deleting record with UID: " + recordUid);

        DatabaseReference userRecordRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("UserRecord")
                .child(recordUid);

        // Remove the user's record from the database
        userRecordRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Record deleted successfully
                Log.d("DeleteRecord", "Record deleted successfully");
                // Record deleted successfully
                // Remove the record from the local list and update the UI
                recordList.remove(position);
                adapterRecord.notifyItemRemoved(position);
                adapterRecord.notifyItemRangeChanged(position, recordList.size());
                recreate();
                Toast.makeText(RecordActivity.this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the failure to delete the record
                Log.e("DeleteRecord", "Failed to delete record: " + e.getMessage(), e);
                Toast.makeText(RecordActivity.this, "Failed to delete record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFixButtonClick(int position) {
        ModelRecord modelRecord = recordList.get(position);
        String recordUid = modelRecord.getUserRecordId(); // Assuming modelRecord contains the record's UID
        String uid = modelRecord.getUid();
        Log.d("FixButtonClick", "Fix record with UID: " + recordUid);
        sendBy = uid;
        DatabaseReference userRecordRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("UserRecord")
                .child(recordUid);
        String message = "Problem has now fixed";
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
        prepareNotificationMessage(recordUid,message);
    }

    long totalRecordCount = 0;
    private void loadCounts() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        // Employee count
        userRef.orderByChild("accountType").equalTo("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long employeeCount = snapshot.getChildrenCount();
                employeeCountTv.setText(String.valueOf(employeeCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        // Initialize a variable to hold the total count


        // User Record Count
        userRef.orderByChild("accountType").equalTo("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                for (DataSnapshot user : userSnapshot.getChildren()) {
                    String userId = user.getKey();
                    DatabaseReference userRecordRef = FirebaseDatabase.getInstance().getReference("Users")
                            .child(userId).child("UserRecord");

                    // Get UserRecord count for each user
                    userRecordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot recordSnapshot) {
                            // Increment the total count with each user's record count
                            totalRecordCount += recordSnapshot.getChildrenCount();

                            // Update the count text after all users have been processed
                            String countText = String.valueOf(totalRecordCount);
                            dataCountTv.setText(countText);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void prepareNotificationMessage(String problemUid, String message){
        //when seller change order status InProgress/Cancelled/Completed, send notification to buyer
        //prepare data for notification
        Log.e("FCM Notification Error", "Error: " + problemUid + message);
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "PROBLEM REPLY " + problemUid;
        String NOTIFICATION_MESSAGE = "" + message;
        String NOTIFICATION_TYPE = "Status";

        //prepare json (what to send and where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("userUid", sendBy);
            notificationBodyJo.put("adminUid", firebaseAuth.getUid()); //since we are logged in as seller to change order so current user uid is seller uid
            notificationBodyJo.put("problemUid", problemUid);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);//to all who subscribed to this topic
            notificationJo.put("data", notificationBodyJo);
        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo);
    }
    private void sendFcmNotification(JSONObject notificationJo) {
        //send volley request

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //notification sent

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //notification failed
                Log.e("FCM Notification Error", "Error: " + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //put required headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key=" + Constants.FCM_KEY);
                return headers;
            }
        };
        //enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}