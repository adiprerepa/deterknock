package com.example.deterknock;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MDNSEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView mdnsEntryName;

    public MDNSEntryViewHolder(View itemView) {
        super(itemView);
        this.mdnsEntryName = itemView.findViewById(R.id.mdnsEntryName);
        this.mdnsEntryName.setOnClickListener(this);
    }

    public void bindData(final MDNSData data) {
        this.mdnsEntryName.setText(data.getName());
    }

    @Override
    public void onClick(View v) {
        Log.d("FUCKSHIT", "onClick:laksnjf ");
        String esp8266Name = mdnsEntryName.getText().toString();
        Intent intent = new Intent(v.getContext(), ESP8266Activity.class);
        intent.putExtra(MainActivity.IP_KEY, esp8266Name);
        v.getContext().startActivity(intent);
    }
}
