package com.example.deterknock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String IP_KEY = "com.example.deterknock.ip";

    // https://github.com/willowtreeapps/SimpleRecyclerViewDemo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MDNSAdapter adapter = new MDNSAdapter(getSavedMdnsItems());
        RecyclerView recyclerView = findViewById(R.id.mdns_entries);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private List<MDNSData> getSavedMdnsItems() {
        // SharedPreferences get with some key parse json list
        List<MDNSData> testData = new ArrayList<>();
        testData.add(new MDNSData("aditya_door.local"));
        testData.add(new MDNSData("bar"));
        return testData;
    }
}