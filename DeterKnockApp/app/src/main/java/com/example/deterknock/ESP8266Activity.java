package com.example.deterknock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ESP8266Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esp8266);

        Intent intent = getIntent();
        String ip = intent.getStringExtra(MainActivity.IP_KEY);
    }
}