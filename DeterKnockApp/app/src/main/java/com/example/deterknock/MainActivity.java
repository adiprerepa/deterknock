package com.example.deterknock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String DNS_KEY = "com.example.deterknock.dns";
    public static final String IP_KEY = "com.example.deterknock.ip";
    public static final String PORT_KEY = "com.example.deterknock.port";
    public static final String SERVICE_TYPE ="_esp8266door._tcp.";
    // https://github.com/willowtreeapps/SimpleRecyclerViewDemo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<MDNSData> items = new ArrayList<>();
        MDNSAdapter adapter = new MDNSAdapter(items);
        RecyclerView recyclerView = findViewById(R.id.mdns_entries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        NsdManager manager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        ESPDiscoveryListener discoveryListener = new ESPDiscoveryListener(manager, items, adapter, this);
        manager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    private List<MDNSData> getSavedMdnsItems() {
        // SharedPreferences get with some key parse json list
        List<MDNSData> testData = new ArrayList<>();
//        testData.add(new MDNSData("aditya_door.local"));
//        testData.add(new MDNSData("bar"));
        return testData;
    }
}