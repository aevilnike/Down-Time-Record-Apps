package com.example.drapps.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drapps.Login;
import com.example.drapps.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {
    Button todoB;
    TextView usernameTv,accType;
    ImageButton logOutB;
    PieChart pieChart;
    List<PieEntry> pieEntryList;
    List<BarEntry> barEntryList;
    private HorizontalBarChart barChart;
    List<BarEntry> barEntryListPbEct;
    private HorizontalBarChart barChartPbEct;
    List<BarEntry> barEntryListPbSaim;
    private HorizontalBarChart barChartPbSaim;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    Map<String, BarEntry> barEntryMapPbEct;
    Map<String, BarEntry> barEntryMapPbSaim;
    Map<String, BarEntry> barEntryMapEq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        todoB = findViewById(R.id.todoB);
        logOutB = findViewById(R.id.logOutB);
        usernameTv = findViewById(R.id.textView2);
        accType = findViewById(R.id.textView3);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();

        //PieChart initialize
        pieEntryList = new ArrayList<>();
        pieChart = findViewById(R.id.pieChart);
        //HorizontalBarChart Eq initialize
        barEntryList = new ArrayList<>();
        barChart = findViewById(R.id.barChart);
        setupBarChart();
        //HorizontalBarChart PbEct initialize
        barEntryListPbEct = new ArrayList<>();
        barChartPbEct = findViewById(R.id.barChartPbEct);
        setupBarPbEctChart();
        //HorizontalBarChart PbSaim initialize
        barEntryListPbSaim = new ArrayList<>();
        barChartPbSaim = findViewById(R.id.barChartPbSaim);
        setupBarPbSaimChart();

        // Initialize the bar entry map
        barEntryMapPbEct = new HashMap<>();
        fetchChartEctDataByRecordId("1712042272431");
        barEntryMapPbSaim = new HashMap<>();
        fetchChartSaimDataByRecordId("1712042272431");
        barEntryMapEq = new HashMap<>();
        fetchChartEqDataByRecordId("1712042272431");
        fetchSubmissionCountsAndNames();

        usernameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, Register.class));
            }
        });
        todoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, RecordActivity.class));
            }
        });
        logOutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Logging out");
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
                                Toast.makeText(AdminActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed creating account
                                progressDialog.dismiss();
                                Toast.makeText(AdminActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void fetchSubmissionCountsAndNames() {
        DatabaseReference userSubmissionCountsRef = FirebaseDatabase.getInstance().getReference("UserSubmissionCounts");

        userSubmissionCountsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();

                        // Fetch submission count for this user
                        String submissionCountStr = userSnapshot.child("submissionCount").getValue(String.class);
                        int submissionCount = 0;
                        if (submissionCountStr != null) {
                            try {
                                submissionCount = Integer.parseInt(submissionCountStr);
                            } catch (NumberFormatException e) {
                                // Handle the case where the string cannot be parsed as an integer
                                e.printStackTrace(); // Or handle it in a different way, such as showing an error message
                            }
                        }

                        // Fetch account name for this user
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        int finalSubmissionCount = submissionCount;
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String accountName = dataSnapshot.child("name").getValue(String.class);
                                    // Add the account name and submission count to the pie chart
                                    setPieValue(finalSubmissionCount, accountName);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle onCancelled as needed
                                Toast.makeText(AdminActivity.this, "Failed to fetch user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "No data found in UserSubmissionCounts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled as needed
                Toast.makeText(AdminActivity.this, "Failed to fetch submission counts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchChartEctDataByRecordId(String recordId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("ECTTable");
        Query query = userRef.orderByChild("recordId").equalTo(recordId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String checkerHandleBrokenStr = snapshot.child("CheckerHandleBroken").getValue(String.class);
                        String checkerLooseStr = snapshot.child("CheckerLoose").getValue(String.class);
                        String dataMissingStr = snapshot.child("DataMissing").getValue(String.class);
                        String issueStr = snapshot.child("Issue").getValue(String.class);
                        String leadWireBrokenStr = snapshot.child("LeadWireBroken").getValue(String.class);
                        String otherProblemStr = snapshot.child("OtherProblem").getValue(String.class);
                        String pinBrokenStr = snapshot.child("PinBroken").getValue(String.class);

                        if (checkerHandleBrokenStr != null) {
                            // Convert string values to integers
                            int checkerHandleBroken = Integer.parseInt(checkerHandleBrokenStr != null ? checkerHandleBrokenStr : "0");
                            int checkerLoose = Integer.parseInt(checkerLooseStr != null ? checkerLooseStr : "0");
                            int dataMissing = Integer.parseInt(dataMissingStr != null ? dataMissingStr : "0");
                            int issue = Integer.parseInt(issueStr != null ? issueStr : "0");
                            int leadWireBroken = Integer.parseInt(leadWireBrokenStr != null ? leadWireBrokenStr : "0");
                            int otherProblem = Integer.parseInt(otherProblemStr != null ? otherProblemStr : "0");
                            int pinBroken = Integer.parseInt(pinBrokenStr != null ? pinBrokenStr : "0");

                            // Clear the existing list before adding new entries
                            barEntryListPbEct.clear();
                            setBarPbEctValue(checkerHandleBroken, checkerLoose, dataMissing, issue, leadWireBroken, otherProblem, pinBroken);
                            setupBarPbEctChart();
                        } else {
                            // Handle case where data is missing or null
                            Toast.makeText(AdminActivity.this, "Data is missing for record ID: " + recordId, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "No data found for record ID: " + recordId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled as needed
                Toast.makeText(AdminActivity.this, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchChartSaimDataByRecordId(String recordId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("SAIMTable");
        Query query = userRef.orderByChild("recordId").equalTo(recordId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String dataCorruptStr = snapshot.child("DataCorrupt").getValue(String.class);
                        String cpuStr = snapshot.child("CpuCantOn").getValue(String.class);
                        String ramProblemStr = snapshot.child("RamProblem").getValue(String.class);
                        String powerSupplyBrokenStr = snapshot.child("PowerSupplyBroken").getValue(String.class);
                        String leadWireBrokenStr = snapshot.child("LeadWireBroken").getValue(String.class);
                        String otherProblemStr = snapshot.child("OtherProblem").getValue(String.class);
                        String pinBrokenStr = snapshot.child("SaimPinBroken").getValue(String.class);

                        if (dataCorruptStr != null) {
                            // Convert string values to integers
                            int dataCorrupt = Integer.parseInt(dataCorruptStr != null ? dataCorruptStr : "0");
                            int cpu = Integer.parseInt(cpuStr != null ? cpuStr : "0");
                            int ramProblem = Integer.parseInt(ramProblemStr != null ? ramProblemStr : "0");
                            int powerSupplyBroken = Integer.parseInt(powerSupplyBrokenStr != null ? powerSupplyBrokenStr : "0");
                            int leadWireBroken = Integer.parseInt(leadWireBrokenStr != null ? leadWireBrokenStr : "0");
                            int otherProblem = Integer.parseInt(otherProblemStr != null ? otherProblemStr : "0");
                            int pinBroken = Integer.parseInt(pinBrokenStr != null ? pinBrokenStr : "0");

                            // Clear the existing list before adding new entries
                            barEntryListPbSaim.clear();
                            setBarPbSaimValue(dataCorrupt, cpu, ramProblem, powerSupplyBroken, leadWireBroken, otherProblem, pinBroken);
                            setupBarPbSaimChart();
                        } else {
                            // Handle case where data is missing or null
                            Toast.makeText(AdminActivity.this, "Data is missing for record ID: " + recordId, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "No data found for record ID: " + recordId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled as needed
                Toast.makeText(AdminActivity.this, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchChartEqDataByRecordId(String recordId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Equipment");
        Query query = userRef.orderByChild("recordId").equalTo(recordId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String ectStr = snapshot.child("ECT").getValue(String.class);
                        String saimStr = snapshot.child("SAIM").getValue(String.class);

                        if (ectStr != null) {
                            // Convert string values to integers
                            int ect = Integer.parseInt(ectStr != null ? ectStr : "0");
                            int saim = Integer.parseInt(saimStr != null ? saimStr : "0");

                            // Clear the existing list before adding new entries
                            barEntryList.clear();
                            setBarValue(ect, saim);
                            setupBarChart();
                        } else {
                            // Handle case where data is missing or null
                            Toast.makeText(AdminActivity.this, "Data is missing for record ID: " + recordId, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "No data found for record ID: " + recordId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled as needed
                Toast.makeText(AdminActivity.this, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(AdminActivity.this, Login.class));
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

                            //set data to ui
                            usernameTv.setText("Register User");
                            accType.setText(accountType);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    //HorizontalBar PbSaim method
    private void setBarPbSaimValue(int dataCorrupt, int cpu, int ramProblem, int powerSupplyBroken, int leadWireBroken, int otherProblem, int pinBroken) {
        barEntryListPbSaim.add(new BarEntry(1, dataCorrupt)); // Example data
        barEntryListPbSaim.add(new BarEntry(2, cpu)); // Example data
        barEntryListPbSaim.add(new BarEntry(3, ramProblem)); // Example data
        barEntryListPbSaim.add(new BarEntry(4, powerSupplyBroken)); // Example data
        barEntryListPbSaim.add(new BarEntry(5, leadWireBroken)); // Example data
        barEntryListPbSaim.add(new BarEntry(6, pinBroken)); // Example data
        barEntryListPbSaim.add(new BarEntry(7, otherProblem)); // Example data
    }
    private void setupBarPbSaimChart() {
        BarDataSet barDataSet = new BarDataSet(barEntryListPbSaim, "Bar Chart");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(barDataSet);
        barChartPbSaim.setData(barData);

        barChartPbSaim.getDescription().setEnabled(false);
        barChartPbSaim.getLegend().setEnabled(false);

        // Customize X-axis labels
        XAxis xAxis = barChartPbSaim.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // Customize Y-axis labels (category names)
        YAxis leftAxis = barChartPbSaim.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < barEntryListPbSaim.size()) {
                    BarEntry entry = barEntryListPbSaim.get(index);
                    // Return an empty string for Y-axis labels (category names)
                    return "";
                }
                return "";
            }
        });

        // Add a label below the chart for category names
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < barEntryListPbSaim.size()) {
                    switch (index) {
                        case 1:
                            return "DC";
                        case 2:
                            return "CPU";
                        case 3:
                            return "RAM";
                        case 4:
                            return "PSB";
                        case 5:
                            return "LWB";
                        case 6:
                            return "PB";
                        case 0:
                            return "etc";
                        default:
                            return "";
                    }
                }
                return "etc";
            }
        });

        barChartPbEct.animateY(1000);
        barChartPbEct.invalidate(); // Refresh chart
    }
    //HorizontalBar PbEct method
    private void setupBarPbEctChart() {
        BarDataSet barDataSet = new BarDataSet(barEntryListPbEct, "Bar Chart");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(barDataSet);
        barChartPbEct.setData(barData);

        barChartPbEct.getDescription().setEnabled(false);
        barChartPbEct.getLegend().setEnabled(false);

        // Customize X-axis labels
        XAxis xAxis = barChartPbEct.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // Customize Y-axis labels (category names)
        YAxis leftAxis = barChartPbEct.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < barEntryListPbEct.size()) {
                    BarEntry entry = barEntryListPbEct.get(index);
                    // Return an empty string for Y-axis labels (category names)
                    return "";
                }
                return "";
            }
        });

        // Add a label below the chart for category names
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < barEntryListPbEct.size()) {
                    switch (index) {
                        case 1:
                            return "CHP";
                        case 2:
                            return "CL";
                        case 3:
                            return "DM";
                        case 4:
                            return "O.F";
                        case 5:
                            return "LWB";
                        case 6:
                            return "PB";
                        case 0:
                            return "etc";
                        default:
                            return "";
                    }
                }
                return "etc";
            }
        });

        barChartPbEct.animateY(1000);
        barChartPbEct.invalidate(); // Refresh chart
    }
    private void setBarPbEctValue(int checkerHandleBroken, int checkerLoose, int dataMissing, int issue, int leadWireBroken, int otherProblem, int pinBroken) {
        barEntryListPbEct.clear();
        barEntryListPbEct.add(new BarEntry(1, checkerHandleBroken)); // Example data
        barEntryListPbEct.add(new BarEntry(2, checkerLoose)); // Example data
        barEntryListPbEct.add(new BarEntry(3, dataMissing)); // Example data
        barEntryListPbEct.add(new BarEntry(4, issue)); // Example data
        barEntryListPbEct.add(new BarEntry(5, leadWireBroken)); // Example data
        barEntryListPbEct.add(new BarEntry(6, pinBroken)); // Example data
        barEntryListPbEct.add(new BarEntry(7, otherProblem)); // Example data
    }
    //HorizontalBar method
    private void setupBarChart() {

        BarDataSet barDataSet = new BarDataSet(barEntryList, "Bar Chart");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Customize X-axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // Customize Y-axis labels (category names)
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < barEntryList.size()) {
                    BarEntry entry = barEntryList.get(index);
                    // Return an empty string for Y-axis labels (category names)
                    return "";
                }
                return "";
            }
        });

        // Add a label below the chart for category names
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < barEntryList.size()) {
                    switch (index) {
                        case 1:
                            return "ECT";
                        case 2:
                            return "SAIM";
                        default:
                            return "";
                    }
                }
                return "SAIM";
            }
        });

        barChart.animateY(1000);
        barChart.invalidate(); // Refresh chart
    }
    private void setBarValue(int ect, int saim) {
        barEntryList.add(new BarEntry(1, ect)); // Example data
        barEntryList.add(new BarEntry(2, saim)); // Example data
    }
    private void setPieValue(int submissionCount, String username) {
        // Add the username and submission count to the pie chart
        pieEntryList.add(new PieEntry(submissionCount, username));
        // Refresh the pie chart
        setupPieChart();
    }
    private void setupPieChart() {
        // Calculate total submission count
        int totalSubmissionCount = 0;
        for (PieEntry entry : pieEntryList) {
            totalSubmissionCount += entry.getValue();
        }

        // Create a list to hold the pie entries with percentages
        List<PieEntry> pieEntryListWithPercentage = new ArrayList<>();

        // Calculate percentage and add to the new list
        for (PieEntry entry : pieEntryList) {
            float percentage = (entry.getValue() * 100.0f) / totalSubmissionCount;
            // Calculate the percentage out of the total of 100%
            float roundedPercentage = Math.round(percentage * 100.0f) / 100.0f;
            String label = entry.getLabel() + " (" + roundedPercentage + "%)";
            pieEntryListWithPercentage.add(new PieEntry(entry.getValue(), label));
        }

        // Set up the pie chart with the new list containing percentages
        PieDataSet pieDataSet = new PieDataSet(pieEntryListWithPercentage, "User Submissions");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(14f);
        pieDataSet.setValueFormatter(new PercentFormatter());

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelTextSize(14f); // Set custom text size
        pieChart.invalidate(); // Refresh chart
    }



}