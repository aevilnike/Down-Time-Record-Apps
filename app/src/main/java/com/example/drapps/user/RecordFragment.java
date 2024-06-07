package com.example.drapps.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.example.drapps.adapter.ImageAdapter;
import com.example.drapps.adapter.UploadListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFragment extends Fragment {

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private List<Uri> imageList;
    private List<Uri> imageUris = new ArrayList<>();
    private List<Uri> selectedImageUris = new ArrayList<>();
    private ImageView imageEvidence;
    private EditText startTimeEditText;
    private EditText finishTimeEditText;
    private EditText dateEditText;
    private Button submitBtn,logoutBtn,btnAddImage;
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
    private TextInputLayout startTimeLayout,finishTimeLayout;
    private LinearLayout ectLayout, saimLayout;
    //private StorageReference mStorageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerViewImages;
    private Activity mActivity;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        ectLayout = view.findViewById(R.id.ectLayout);
        saimLayout = view.findViewById(R.id.saimLayout);
        startTimeEditText = view.findViewById(R.id.startTimeEditText);
        finishTimeEditText = view.findViewById(R.id.finishTimeEditText);
        dateEditText = view.findViewById(R.id.dateEditText);
        nameTv = view.findViewById(R.id.nameTv);
        NoPekerjaTv = view.findViewById(R.id.NoPekerjaTv);
        linesubEt = view.findViewById(R.id.linesubEt);
        teamEt = view.findViewById(R.id.teamEt);
        acEt = view.findViewById(R.id.acEt);
        checkBoxECT = view.findViewById(R.id.checkBoxECT);
        checkBoxSAIM = view.findViewById(R.id.checkBoxSAIM);
        pinBrokenCb = view.findViewById(R.id.pinBrokenCb);
        lwbCb = view.findViewById(R.id.lwbCb);
        clCb = view.findViewById(R.id.clCb);
        ofiCb = view.findViewById(R.id.ofiCb);
        dmCb = view.findViewById(R.id.dmCb);
        chbCb = view.findViewById(R.id.chbCb);
        dataCorruptCb = view.findViewById(R.id.dataCorruptCb);
        cpuSCb = view.findViewById(R.id.cpuSCb);
        rampCb = view.findViewById(R.id.rampCb);
        psbCb = view.findViewById(R.id.psbCb);
        lwbSCb = view.findViewById(R.id.lwbSCb);
        pbCb = view.findViewById(R.id.pbCb);
        saimOtherEt = view.findViewById(R.id.saimOtherEt);
        ectOtherEt = view.findViewById(R.id.ectOtherEt);
        startTimeLayout = view.findViewById(R.id.startTimeLayout);
        finishTimeLayout = view.findViewById(R.id.finishTimeLayout);
        dateEditText = view.findViewById(R.id.dateEditText);
        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        mAdapter = new UploadListAdapter(fileNameList,fileDoneList);
        submitBtn = view.findViewById(R.id.submitBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        btnAddImage = view.findViewById(R.id.btnAddImage);
        recyclerViewImages = view.findViewById(R.id.recyclerViewImages);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //get uid of the shop from intent
        adminUid = "KOhpmn6uACX5i1mLFiP9a67RBeX2";
        checkUser();

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageUris();
            }
        });
        // Set onClickListener to show DatePicker when dateEditText is clicked
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTimePicker();
            }
        });
        finishTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFinishTimePicker();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeOffline();
            }
        });
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

        return view;
    }
    private List<Uri> getImageUris() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
        return null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                if (data != null) {
                    Uri imageUri = data.getData();
                    selectedImageUris.add(imageUri);
                    // Add the selected image to the RecyclerView
                    addImageToRecyclerView(imageUri);
                }
            }
        }
    }
    ImageAdapter imageAdapter;
    private void addImageToRecyclerView(Uri imageUri) {

        // Make the RecyclerView visible if it was hidden
        recyclerViewImages.setVisibility(View.VISIBLE);

        // Create a new list to hold the images (if not already initialized)
        if (imageList == null) {
            imageList = new ArrayList<>();
        }

        // Add the selected image URI to the list
        imageList.add(imageUri);

        // Notify the adapter of the RecyclerView about the data change
        if (imageAdapter == null) {
            imageAdapter = new ImageAdapter(imageList);
            recyclerViewImages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewImages.setAdapter(imageAdapter);
        } else {
            imageAdapter.notifyDataSetChanged();
        }
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
                        Toast.makeText(getActivity(), "Signed out successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getActivity(), Login.class));
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
    private void inputData() {
        // Disable user interaction
        if (getActivity() != null) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
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
        st = startTimeEditText.getText().toString().trim();
        ft = finishTimeEditText.getText().toString().trim();
        sd = dateEditText.getText().toString().trim();
        imageUris = getImageUris();

        // Pass the timestamp and imageUris to saveRecordToDatabase()
        final String timestamp = "" + System.currentTimeMillis();
        saveRecordToDatabase(timestamp, selectedImageUris);
    }
    private void saveRecordToDatabase(String timestamp, List<Uri> imageUris) {
        progressDialog.setMessage("Saving record..");
        progressDialog.show();
        equipmentProblem();

        // Check if there are images to upload
        if (imageUris == null || imageUris.isEmpty()) {
            // No images to upload, proceed to save record without images
            saveRecordWithoutImages(timestamp);
            return; // Exit method after saving record
        }

        // Upload each image to Firebase Storage and get download URLs
            List<String> downloadUrls = new ArrayList<>();
            AtomicInteger uploadCount = new AtomicInteger(0);
            for (Uri imageUri : imageUris) {
                // Name and path of image
                String filePathAndName = "evidences/" + firebaseAuth.getUid() + "/" + UUID.randomUUID().toString();

                // Upload image to Firebase Storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                storageReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Get download URL of uploaded image
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                downloadUrls.add(uri.toString());
                                // Check if all images have been uploaded
                                if (uploadCount.incrementAndGet() == imageUris.size()) {
                                    // All images uploaded, proceed to save record with image URLs
                                    saveRecordWithImages(timestamp, downloadUrls);
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Handle upload failure
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

        // Add a completion listener to progressDialog to re-enable user interaction
        progressDialog.setOnDismissListener(dialog -> {
            // Re-enable user interaction
            if (getActivity() != null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }
    private void saveRecordWithImages(String timestamp, List<String> imageUrls) {
        // Your code to save record with image URLs goes here...
        // For example:
        // Construct HashMap with image URLs and other record details
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userRecordId", timestamp);
        // Add other record details...
        hashMap.put("employeeName",""+username);
        hashMap.put("employeeNo",""+empno);
        hashMap.put("linesub",""+linesub);
        hashMap.put("teamMember",""+teamm);
        hashMap.put("action",""+act);
        //Equipment Selected
        hashMap.put("ect",""+ect);
        hashMap.put("saim",""+saim);
        //ECT Problem
        hashMap.put("pbEct1",""+pbECT);
        hashMap.put("pbEct2",""+lwbECT);
        hashMap.put("pbEct3",""+clECT);
        hashMap.put("pbEct4",""+issueECT);
        hashMap.put("pbEct5",""+dmECT);
        hashMap.put("pbEct6",""+chbECT);
        hashMap.put("pbEct7",""+otherECT);
        //SAIM Problem
        hashMap.put("pbSaim1",""+dcSAIM);
        hashMap.put("pbSaim2",""+cpuSAIM);
        hashMap.put("pbSaim3",""+ramSAIM);
        hashMap.put("pbSaim4",""+psSAIM);
        hashMap.put("pbSaim5",""+lwbSAIM);
        hashMap.put("pbSaim6",""+pbSAIM);
        hashMap.put("pbSaim7",""+otherSAIM);
        //time/date
        hashMap.put("startTime",""+st);
        hashMap.put("finishTime",""+ft);
        hashMap.put("date",""+sd);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("sendBy", ""+firebaseAuth.getUid());
        hashMap.put("sendTo", ""+adminUid);
        for (int i = 0; i < imageUrls.size(); i++) {
            hashMap.put("evidenceImage" + i, imageUrls.get(i));
        }
        // Add other record details...
        // Save HashMap to Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("UserRecord").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    // Record saved successfully
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Record added", Toast.LENGTH_SHORT).show();
                    Context context = getContext();
                    if (context != null) {
                        Intent intent = new Intent(getActivity(), HistoryDetails.class);
                        intent.putExtra("recordUid", timestamp);
                        intent.putExtra("userId", firebaseAuth.getUid());
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Log.e("RecordFragment", "Context is null");
                    }

                    prepareNotificationMessage(timestamp);
                })
                .addOnFailureListener(e -> {
                    // Failed to save record
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed to save record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void saveRecordWithoutImages(String timestamp) {
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
                        Toast.makeText(getActivity(), "Record added", Toast.LENGTH_SHORT).show();
                        // Open the history fragment with bottom navigation tabs
                        Intent intent = new Intent(requireContext(), HistoryDetails.class);
                        intent.putExtra("recordUid", timestamp);
                        intent.putExtra("userId", firebaseAuth.getUid());
                        startActivity(intent);
                        getActivity().finish();
                        prepareNotificationMessage(timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed adding to db
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                                Toast.makeText(getActivity(), problemType + " count updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to update " + problemType + " count: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
            }
        });
    }
    public void showStartTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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
                }, hour, minute, DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.show();
    }
    public void showFinishTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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
                }, hour, minute, DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.show();
    }
    // Method to show the DatePicker
    private void showDatePicker() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog and set the current date as the default date
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date to the EditText
                        dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();
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
            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo, problemUid);
    }
    private void sendFcmNotification(JSONObject notificationJo, String problemUid) {
        //send volley request
        Log.e("FCM Notification Error", "Error: " + problemUid + notificationJo);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start HistoryDetails activity

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FCM Notification Error", "Error: " + error.getMessage());
                //if failed sending fcm, still start HistoryDetails activity

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
        //enqueue the volley request
        Volley.newRequestQueue(requireActivity()).add(jsonObjectRequest);
    }
}
