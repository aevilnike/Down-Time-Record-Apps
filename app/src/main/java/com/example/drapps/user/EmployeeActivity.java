package com.example.drapps.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drapps.Constants;
import com.example.drapps.Login;
import com.example.drapps.R;
import com.example.drapps.adapter.UploadListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private Uri image_uri;
    private ImageView imageEvidence;
    private EditText startTimeEditText;
    private EditText finishTimeEditText;
    private EditText dateEditText;
    public static final int RESULT_IMAGE = 10;
    private Button btnUpload,submitBtn,logoutBtn;
    private RecyclerView mRecyclerView;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    private UploadListAdapter mAdapter;
    private String adminUid;
    private TextView nameTv,NoPekerjaTv;
    private TextInputLayout linesubEt,teamEt,acEt;
    //Equipment vars
    private CheckBox checkBoxECT,checkBoxSAIM;
    //Checkbox ECT
    private CheckBox pinBrokenCb,lwbCb,clCb,ofiCb,dmCb,chbCb;
    //Checkbox SAIM
    private CheckBox dataCorruptCb,cpuSCb,rampCb,psbCb,lwbSCb,pbCb;
    private EditText saimOtherEt,ectOtherEt;
    private TextInputLayout startTimeLayout,finishTimeLayout,dateLayout;
    private LinearLayout ectLayout, saimLayout;
    //private StorageReference mStorageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private StorageReference mStorageReference;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private String[] downloadImageUrls; // Declare the array
    int totalItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        ectLayout = findViewById(R.id.ectLayout);
        saimLayout = findViewById(R.id.saimLayout);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        finishTimeEditText = findViewById(R.id.finishTimeEditText);
        dateEditText = findViewById(R.id.dateEditText);
        nameTv = findViewById(R.id.nameTv);
        NoPekerjaTv = findViewById(R.id.NoPekerjaTv);
        linesubEt = findViewById(R.id.linesubEt);
        teamEt = findViewById(R.id.teamEt);
        acEt = findViewById(R.id.acEt);
        checkBoxECT = findViewById(R.id.checkBoxECT);
        checkBoxSAIM = findViewById(R.id.checkBoxSAIM);
        pinBrokenCb = findViewById(R.id.pinBrokenCb);
        lwbCb = findViewById(R.id.lwbCb);
        clCb = findViewById(R.id.clCb);
        ofiCb = findViewById(R.id.ofiCb);
        dmCb = findViewById(R.id.dmCb);
        chbCb = findViewById(R.id.chbCb);
        dataCorruptCb = findViewById(R.id.dataCorruptCb);
        cpuSCb = findViewById(R.id.cpuSCb);
        rampCb = findViewById(R.id.rampCb);
        psbCb = findViewById(R.id.psbCb);
        lwbSCb = findViewById(R.id.lwbSCb);
        pbCb = findViewById(R.id.pbCb);
        saimOtherEt = findViewById(R.id.saimOtherEt);
        ectOtherEt = findViewById(R.id.ectOtherEt);
        startTimeLayout = findViewById(R.id.startTimeLayout);
        finishTimeLayout = findViewById(R.id.finishTimeLayout);
        dateLayout = findViewById(R.id.dateLayout);
        /*btnUpload = findViewById(R.id.btnUpload);
        mRecyclerView = findViewById(R.id.recycler_upload);*/
        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        mAdapter = new UploadListAdapter(fileNameList,fileDoneList);
        submitBtn = findViewById(R.id.submitBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        imageEvidence = findViewById(R.id.imageEvidence);

        /*mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mStorageReference = FirebaseStorage.getInstance().getReference();*/
        //setup progress dialog
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //get uid of the shop from intent
        adminUid = "KOhpmn6uACX5i1mLFiP9a67RBeX2";

        checkUser();

        imageEvidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick img
                //showImagePickDialog();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeOffline();
            }
        });
        /*btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_IMAGE);
            }
        });*/
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });
        // Set a listener for the ECT CheckBox
        checkBoxECT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the visibility of problemECTView based on the checked state
                ectLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        // Set a listener for the SAIM CheckBox
        checkBoxSAIM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the visibility of problemSAIMView based on the checked state
                saimLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void makeMeOffline() {
        //after logout, make user offline
        progressDialog.setMessage("Logging Out...");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        checkUser();
                        Toast.makeText(EmployeeActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        Toast.makeText(EmployeeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(EmployeeActivity.this, Login.class));
            finish();
        } else {
            loadMyInfo();
        }
    }
    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data from db
                            String name = ""+ds.child("name").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String email = ""+ds.child("email").getValue();
                            String employeeNumber = ""+ds.child("employeeNumber").getValue();
                            String uid = ""+ds.child("uid").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();

                            //set data to ui
                            nameTv.setText(name);
                            NoPekerjaTv.setText(employeeNumber);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private String username, empno, linesub, teamm, act, ect, saim;
    private String pbECT, lwbECT, clECT, issueECT, dmECT, chbECT,otherECT;
    private String dcSAIM, cpuSAIM, ramSAIM, psSAIM, lwbSAIM, pbSAIM,otherSAIM;
    private String st, ft, sd;
    private int pbECTCount, lwbECTCount, clECTCount, issueECTCount, dmECTCount, chbECTCount,otherECTCount;
    private int dcSAIMCount, cpuSAIMCount, ramSAIMCount, psSAIMCount, lwbSAIMCount, pbSAIMCount,otherSAIMCount;
    private void inputData() {
        username = nameTv.getText().toString().trim();
        empno = NoPekerjaTv.getText().toString().trim();
        linesub = linesubEt.getEditText().getText().toString().trim();
        teamm = teamEt.getEditText().getText().toString().trim();
        act = acEt.getEditText().getText().toString().trim();
        // Check if the CheckBox is checked
        if (checkBoxECT.isChecked()) {
            // Get the text from the CheckBox
            ect = checkBoxECT.getText().toString();
            // Now you have the text from the CheckBox
            Log.d("CheckBoxText", ect); // Log the text to see in Logcat
        } else {
            // CheckBox is not checked
            // Handle the case when CheckBox is unchecked
        }
        // Check if the CheckBox is checked
        if (checkBoxSAIM.isChecked()) {
            // Get the text from the CheckBox
            saim = checkBoxSAIM.getText().toString();
            // Now you have the text from the CheckBox
            Log.d("CheckBoxText", saim); // Log the text to see in Logcat
        } else {
            // CheckBox is not checked
            // Handle the case when CheckBox is unchecked
        }
        // Check if ectLayout is visible
        if (ectLayout.getVisibility() == View.VISIBLE){
            if (pinBrokenCb.isChecked()) {
                pbECT = pinBrokenCb.getText().toString();
            }
            if (lwbCb.isChecked()) {
                lwbECT = lwbCb.getText().toString();
            }
            if (clCb.isChecked()) {
                clECT = clCb.getText().toString();
            }
            if (ofiCb.isChecked()) {
                issueECT = ofiCb.getText().toString();
            }
            if (dmCb.isChecked()) {
                dmECT = dmCb.getText().toString();
            }
            if (chbCb.isChecked()) {
                chbECT = chbCb.getText().toString();
            }
            otherECT = ectOtherEt.getText().toString().trim();
        }
        if (saimLayout.getVisibility() == View.VISIBLE){
            if (dataCorruptCb.isChecked()) {
                dcSAIM = dataCorruptCb.getText().toString();
            }
            if (cpuSCb.isChecked()) {
                cpuSAIM = cpuSCb.getText().toString();
            }
            if (rampCb.isChecked()) {
                ramSAIM = rampCb.getText().toString();
            }
            if (psbCb.isChecked()) {
                psSAIM = psbCb.getText().toString();
            }
            if (lwbSCb.isChecked()) {
                lwbSAIM = lwbSCb.getText().toString();
            }
            if (pbCb.isChecked()) {
                pbSAIM = pbCb.getText().toString();
            }
            otherSAIM = saimOtherEt.getText().toString().trim();
        }
        st = startTimeLayout.getEditText().getText().toString().trim();
        ft = finishTimeLayout.getEditText().getText().toString().trim();
        sd = dateLayout.getEditText().getText().toString().trim();

        saveToDatabase();
    }

    private void saveToDatabase() {
        progressDialog.setMessage("Submitting record..");
        progressDialog.show();
        final String timestamp = ""+System.currentTimeMillis();
        equipmentProblem();

        // Update the submission count
        DatabaseReference userRecordCountRef = FirebaseDatabase.getInstance().getReference("UserSubmissionCounts/" + firebaseAuth.getUid() + "/submissionCount");
        userRecordCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String count = dataSnapshot.getValue(String.class);
                    int countInt = Integer.parseInt(count);
                    countInt++;
                    // Update the submission count
                    userRecordCountRef.setValue(String.valueOf(countInt));
                } else {
                    // The submission count doesn't exist, set it to 1
                    userRecordCountRef.setValue("1");
                }
                // Proceed to save the record to the database
                saveRecordToDatabase(timestamp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
            }
        });
    }
    private void saveRecordToDatabase(String timestamp) {
        // Your existing code to save the record goes here...
        progressDialog.setMessage("Saving record..");
        progressDialog.show();
        //timestamp = ""+System.currentTimeMillis();
        equipmentProblem();

        if (image_uri==null) {
            /*if (totalItems == 0) {*/
            //Upload without image
            HashMap<String, Object> haspMap = new HashMap<>();
            haspMap.put("userRecordId",""+timestamp);
            haspMap.put("employeeName",""+username);
            haspMap.put("employeeNo",""+empno);
            haspMap.put("linesub",""+linesub);
            haspMap.put("teamMember",""+teamm);
            haspMap.put("action",""+act);
            //Equipment Selected
            haspMap.put("ect",""+ect);
            haspMap.put("saim",""+saim);
            //ECT Problem
            haspMap.put("pbEct1",""+pbECT);
            haspMap.put("pbEct2",""+lwbECT);
            haspMap.put("pbEct3",""+clECT);
            haspMap.put("pbEct4",""+issueECT);
            haspMap.put("pbEct5",""+dmECT);
            haspMap.put("pbEct6",""+chbECT);
            haspMap.put("pbEct7",""+otherECT);
            //SAIM Problem
            haspMap.put("pbSaim1",""+dcSAIM);
            haspMap.put("pbSaim2",""+cpuSAIM);
            haspMap.put("pbSaim3",""+ramSAIM);
            haspMap.put("pbSaim4",""+psSAIM);
            haspMap.put("pbSaim5",""+lwbSAIM);
            haspMap.put("pbSaim6",""+pbSAIM);
            haspMap.put("pbSaim7",""+otherSAIM);
            //time/date
            haspMap.put("startTime",""+st);
            haspMap.put("finishTime",""+ft);
            haspMap.put("date",""+sd);
            haspMap.put("evidence",""+"");//no img, set empty
            haspMap.put("timestamp",""+timestamp);
            haspMap.put("uid",""+firebaseAuth.getUid());
            haspMap.put("sendBy", ""+firebaseAuth.getUid());
            haspMap.put("sendTo", ""+adminUid);

            //Add into firebase
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("UserRecord").child(timestamp).setValue(haspMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //added to db
                            progressDialog.dismiss();
                            Toast.makeText(EmployeeActivity.this, "Record added", Toast.LENGTH_SHORT).show();
                            clearData();
                            prepareNotificationMessage(timestamp);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding to db
                            progressDialog.dismiss();
                            Toast.makeText(EmployeeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        } else {
            //save with image
            //name and path of image
            String filePathAndNAme = "evidences/" + ""+firebaseAuth.getUid();

            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndNAme);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downlaodImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                //setup data to save
                                HashMap<String, Object> haspMap = new HashMap<>();
                                haspMap.put("userRecordId",""+timestamp);
                                haspMap.put("employeeName",""+username);
                                haspMap.put("employeeNo",""+empno);
                                haspMap.put("linesub",""+linesub);
                                haspMap.put("teamMember",""+teamm);
                                haspMap.put("action",""+act);
                                //Equipment Selected
                                haspMap.put("ect",""+ect);
                                haspMap.put("saim",""+saim);
                                //ECT Problem
                                haspMap.put("pbEct1",""+pbECT);
                                haspMap.put("pbEct2",""+lwbECT);
                                haspMap.put("pbEct3",""+clECT);
                                haspMap.put("pbEct4",""+issueECT);
                                haspMap.put("pbEct5",""+dmECT);
                                haspMap.put("pbEct6",""+chbECT);
                                haspMap.put("pbEct7",""+otherECT);
                                //SAIM Problem
                                haspMap.put("pbSaim1",""+dcSAIM);
                                haspMap.put("pbSaim2",""+cpuSAIM);
                                haspMap.put("pbSaim3",""+ramSAIM);
                                haspMap.put("pbSaim4",""+psSAIM);
                                haspMap.put("pbSaim5",""+lwbSAIM);
                                haspMap.put("pbSaim6",""+pbSAIM);
                                haspMap.put("pbSaim7",""+otherSAIM);
                                //time/date
                                haspMap.put("startTime",""+st);
                                haspMap.put("finishTime",""+ft);
                                haspMap.put("date",""+sd);
                                /*for (int i = 0; i < totalItems; i++) {
                                    haspMap.put("evidence"+i,""+downloadImageUrls[i]);
                                }*/
                                haspMap.put("timestamp",""+timestamp);
                                haspMap.put("uid",""+firebaseAuth.getUid());
                                haspMap.put("sendBy", ""+firebaseAuth.getUid());
                                haspMap.put("sendTo", ""+adminUid);
                                haspMap.put("evidenceImage",""+downlaodImageUri);//url upload image
                                //save to db
                                //Add into firebase
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("UserRecord").child(timestamp).setValue(haspMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(EmployeeActivity.this, "Record added", Toast.LENGTH_SHORT).show();
                                                clearData();
                                                prepareNotificationMessage(timestamp);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed adding to db
                                                progressDialog.dismiss();
                                                Toast.makeText(EmployeeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EmployeeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }
    private void equipmentProblem() {
        String timestamp = "" + "1712042272431";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference equipmentRef = FirebaseDatabase.getInstance().getReference("Equipment");


        if (checkBoxECT.isChecked()) {
            updateProblemCount(equipmentRef, timestamp, "ECT");
        }
        if (checkBoxSAIM.isChecked()) {
            updateProblemCount(equipmentRef, timestamp, "SAIM");
        }

        // ECT Problem
        if (pinBrokenCb.isChecked()) {
            updateProblemCount(reference.child("ECTTable"), timestamp, "PinBroken");
        }
        if (lwbCb.isChecked()) {
            updateProblemCount(reference.child("ECTTable"), timestamp, "LeadWireBroken");
        }
        if (clCb.isChecked()) {
            updateProblemCount(reference.child("ECTTable"), timestamp, "CheckerLoose");
        }
        if (ofiCb.isChecked()) {
            updateProblemCount(reference.child("ECTTable"), timestamp, "Issue");
        }
        if (dmCb.isChecked()) {
            updateProblemCount(reference.child("ECTTable"), timestamp, "DataMissing");
        }
        if (chbCb.isChecked()) {
            updateProblemCount(reference.child("ECTTable"), timestamp, "CheckerHandleBroken");
        }
        if (chbCb.isChecked()) {
            updateProblemCount(reference.child("ECTTable"), timestamp, "CheckerHandleBroken");
        }
        // Check EditTexts
        if (!ectOtherEt.getText().toString().isEmpty()) {
            updateProblemCount(reference.child("ECTTable"), timestamp, "OtherProblem");
        }
        if (!saimOtherEt.getText().toString().isEmpty()) {
            updateProblemCount(reference.child("SAIMTable"), timestamp, "OtherProblem");
        }
        // SAIM Problem
        if (dataCorruptCb.isChecked()) {
            updateProblemCount(reference.child("SAIMTable"), timestamp, "DataCorrupt");
        }
        if (cpuSCb.isChecked()) {
            updateProblemCount(reference.child("SAIMTable"), timestamp, "CpuCantOn");
        }
        if (rampCb.isChecked()) {
            updateProblemCount(reference.child("SAIMTable"), timestamp, "RamProblem");
        }
        if (psbCb.isChecked()) {
            updateProblemCount(reference.child("SAIMTable"), timestamp, "PowerSupplyBroken");
        }
        if (lwbSCb.isChecked()) {
            updateProblemCount(reference.child("SAIMTable"), timestamp, "LeadWireBroken");
        }
        if (pbCb.isChecked()) {
            updateProblemCount(reference.child("SAIMTable"), timestamp, "SaimPinBroken");
        }
    }
    private void updateProblemCount(DatabaseReference tableReference, String timestamp, String problemType) {
        tableReference.child(timestamp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> hashMap = new HashMap<>();
                String countStr = dataSnapshot.child(problemType).getValue(String.class);
                if (countStr != null && !countStr.isEmpty()) {
                    // Convert the count string to integer and increment
                    int count = Integer.parseInt(countStr);
                    count++;
                    // Update the count in the database as a string
                    hashMap.put(problemType, String.valueOf(count));
                } else {
                    // Handle the case when count is null or empty
                    Log.e("DataSnapshot", "Count value is null or empty for problemType: " + problemType);
                }

                hashMap.put("recordId", timestamp);
                hashMap.put("timestamp", timestamp);

                // Update the problem count in the table
                tableReference.child(timestamp).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EmployeeActivity.this, problemType + " count updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EmployeeActivity.this, "Failed to update " + problemType + " count: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
            }
        });
    }
    private void clearData() {
    }
    public void showStartTimePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        if (hourOfDay < 12) {
                            amPm = "AM";
                        } else {
                            amPm = "PM";
                        }
                        startTimeEditText.setText(String.format("%02d:%02d %s", (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12, minute, amPm));
                    }
                }, hour, minute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }

    public void showFinishTimePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        if (hourOfDay < 12) {
                            amPm = "AM";
                        } else {
                            amPm = "PM";
                        }
                        finishTimeEditText.setText(String.format("%02d:%02d %s", (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12, minute, amPm));
                    }
                }, hour, minute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }

    public void showDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                        dateEditText.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Save single image
        if (resultCode==RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get picked image
                image_uri = data.getData();
                //set to imageview
                imageEvidence.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview
                imageEvidence.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        //Save multiple image but without specific user
        /*if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK){
            if (data.getClipData() != null){
                totalItems = data.getClipData().getItemCount();
                // Initialize the array with the total number of items
                downloadImageUrls = new String[totalItems];
                for (int i = 0; i < totalItems; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    String fileName = getFileName(fileUri);
                    fileNameList.add(fileName);
                    fileDoneList.add("Uploading");
                    mAdapter.notifyDataSetChanged();

                    // Upload each file into Firebase storage separately
                    uploadFileToFirebaseStorage(fileUri, i);
                }
            } else if (data.getData() != null){
                Toast.makeText(this, "Selected Single File", Toast.LENGTH_SHORT).show();
            }
        }*/
    }
    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                        image_uri = result.getData().getData();
                        //set to imageview
                        imageEvidence.setImageURI(image_uri);
                    } else {
                        Toast.makeText(EmployeeActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void uploadFileToFirebaseStorage(Uri fileUri, int index) {
        // Name and path of image
        String filePathAndName = "images/" + firebaseAuth.getUid() + "/" + getFileName(fileUri);

        // Upload image to Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get URL of uploaded image
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Handle the download URL (e.g., save it to the array)
                        String downloadUrl = uri.toString();
                        // Save the download URL to the array at the specified index
                        downloadImageUrls[index] = downloadUrl;
                        // Check if all images have been uploaded and their URLs retrieved
                        if (index == totalItems - 1) {
                            // All images uploaded, you can now use the array of download URLs
                            // For example, you can save the array of download URLs to the database
                            //saveImageDownloadUrlsToDatabase();
                            progressDialog.dismiss();
                            Toast.makeText(EmployeeActivity.this, "Images uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EmployeeActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    @SuppressLint("Range")
    public String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private void requestPermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(EmployeeActivity.this, "Permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        PermissionListener dialogPermissionListener =
                                DialogOnDeniedPermissionListener.Builder
                                        .withContext(EmployeeActivity.this)
                                        .withTitle("Read External Storage permission")
                                        .withMessage("Read External Storage  permission is needed")
                                        .withButtonText(android.R.string.ok)
                                        .withIcon(R.mipmap.ic_launcher)
                                        .build();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void prepareNotificationMessage(String problemUid){
        //when user places order, send notification to seller
        //prepare data for notification
        Log.e("FCM Notification Error", "Error: " + problemUid);
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "PROBLEM OCCUR!";
        String NOTIFICATION_MESSAGE = "There's new problem has occur";
        String NOTIFICATION_TYPE = "Problem";

        //prepare json (what to send and where to send)
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("userUid", firebaseAuth.getUid()); //since we are logged in as buyer to place order to so current user uid is buyer uid
            notificationBodyJo.put("adminUid", adminUid);
            notificationBodyJo.put("problemUid", problemUid);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);//to all who subscribed to this topic
            notificationJo.put("data", notificationBodyJo);
        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo, problemUid);
    }

    private void sendFcmNotification(JSONObject notificationJo, String problemUid) {
        //send volley request
        Log.e("FCM Notification Error", "Error: " + problemUid + notificationJo);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start order details activity
                //after placing order open order details page
                //open order details, we need to keys there, orderId, orderTo
                Log.e("FCM Notification Error", "Error: " + response + notificationJo);
                Intent intent = new Intent(EmployeeActivity.this, EmployeeActivity.class);
                intent.putExtra("sendTo", adminUid);
                intent.putExtra("problemUid", problemUid);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FCM Notification Error", "Error: " + error.getMessage());
                //if failed sending fcm, still start order details activity
                Intent intent = new Intent(EmployeeActivity.this, EmployeeActivity.class);
                intent.putExtra("sendTo", adminUid);
                intent.putExtra("problemUid", problemUid);
                startActivity(intent);
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