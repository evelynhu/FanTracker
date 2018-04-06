package com.mii.android.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mii.android.R;
import com.mii.android.ui.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_about;
    }

    private void setupViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // back button pressed
            onBackPressed();
        });
    }

}
