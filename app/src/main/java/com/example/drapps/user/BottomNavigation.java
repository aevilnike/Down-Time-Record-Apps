package com.example.drapps.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.drapps.R;
import com.example.drapps.admin.AdminActivity;
import com.example.drapps.admin.RecordActivity;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.ibrahimsn.lib.SmoothBottomBar;

public class BottomNavigation extends AppCompatActivity {
    private SmoothBottomBar bottomBar;
    public FragmentContainerView containerView;
    private static final int RECORD_ITEM_INDEX = 0;
    private static final int HISTORY_ITEM_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        // Replace current fragment with RecordFragment



        bottomBar = findViewById(R.id.bottomBar);
        containerView = findViewById(R.id.container);
        bottomBar.setOnItemSelectedListener((Function1<Integer, Unit>) itemIndex -> {
            switch (itemIndex) {
                case RECORD_ITEM_INDEX:
                    // Replace current fragment with RecordFragment
                    replaceFragment(new RecordFragment());
                    break;
                case HISTORY_ITEM_INDEX:
                    // Replace current fragment with HistoryFragment
                    replaceFragment(new HistoryFragment());
                    break;
            }
            return null;
        });
        // Add the following code to check the intent for fragment to open
        Intent intent = getIntent();
        String fragmentToOpen = intent.getStringExtra("fragmentToOpen");
        if (fragmentToOpen != null && fragmentToOpen.equals("HistoryFragment")) {
            // Open HistoryFragment
            bottomBar.setItemActiveIndex(HISTORY_ITEM_INDEX);
            replaceFragment(new HistoryFragment());

        } else {
            bottomBar.setItemActiveIndex(RECORD_ITEM_INDEX);
            replaceFragment(new RecordFragment());
        }
    }

    // Method to replace the current fragment with a new one
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
