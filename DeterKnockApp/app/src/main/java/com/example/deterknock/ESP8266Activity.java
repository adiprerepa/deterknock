package com.example.deterknock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ESP8266Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esp8266);

        Intent intent = getIntent();
        String ip = "http://" + intent.getStringExtra(MainActivity.IP_KEY) + ":" + 8080;
        // port 8080
        // /stateChange lcd_msg priority str

        Button stateChangeButton = findViewById(R.id.submitStateChangeButton);
        stateChangeButton.setOnClickListener(v -> {
            EditText lcdMessageEditText = findViewById(R.id.lcdStatusInput);
            EditText priorityEditText = findViewById(R.id.priorityInput);
            int priority = Integer.parseInt(priorityEditText.getText().toString());
            if (priority < 1 || priority > 4) {
                Toast.makeText(getApplicationContext(), "Priority needs to be int from 1-5", Toast.LENGTH_SHORT).show();
                priorityEditText.setText("");
                return;
            }
            new StateChangeAsyncTask(this).execute(ip, lcdMessageEditText.getText().toString(), String.valueOf(priority));
        });
    }
}