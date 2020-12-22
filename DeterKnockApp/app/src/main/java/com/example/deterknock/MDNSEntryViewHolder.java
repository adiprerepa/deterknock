package com.example.deterknock;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MDNSEntryViewHolder extends RecyclerView.ViewHolder {

    TextView mdnsEntryName;

    public MDNSEntryViewHolder(View itemView) {
        super(itemView);
        mdnsEntryName = itemView.findViewById(R.id.mdnsEntryName);
    }

    public void bindData(final MDNSData data) {
        this.mdnsEntryName.setText(data.getName());
    }
}
