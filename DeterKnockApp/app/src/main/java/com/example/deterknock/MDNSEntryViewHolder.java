package com.example.deterknock;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MDNSEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView mdnsEntryName;
    private MDNSData data;

    public MDNSEntryViewHolder(View itemView) {
        super(itemView);
        this.mdnsEntryName = itemView.findViewById(R.id.mdnsEntryName);
        this.mdnsEntryName.setOnClickListener(this);
    }

    public void bindData(final MDNSData data) {
        this.mdnsEntryName.setText(data.getName());
        this.data = data;
    }

    @Override
    public void onClick(View v) {
        String esp8266Name = mdnsEntryName.getText().toString();
        Intent intent = new Intent(v.getContext(), ESP8266Activity.class);
        intent.putExtra(MainActivity.DNS_KEY, esp8266Name);
        intent.putExtra(MainActivity.IP_KEY, this.data.getIp().toString());
        intent.putExtra(MainActivity.PORT_KEY, this.data.getPort());
        v.getContext().startActivity(intent);
    }
}
