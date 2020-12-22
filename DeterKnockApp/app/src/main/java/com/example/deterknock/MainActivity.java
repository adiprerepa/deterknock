package com.example.deterknock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // https://github.com/willowtreeapps/SimpleRecyclerViewDemo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MDNSAdapter adapter = new MDNSAdapter(getSavedMdnsItems());
        RecyclerView recyclerView = findViewById(R.id.mdns_entries);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private List<MDNSData> getSavedMdnsItems() {
        // SharedPreferences get with some key parse json list
        return new ArrayList<>();
    }
}