package com.example.deterknock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MDNSAdapter extends RecyclerView.Adapter<MDNSEntryViewHolder> {

    List<MDNSData> dnsData;

    public MDNSAdapter(final List<MDNSData> data) {
        this.dnsData = data;
    }

    @NonNull
    @Override
    public MDNSEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mdns_entry, parent, false);
        return new MDNSEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MDNSEntryViewHolder holder, int position) {
        holder.bindData(dnsData.get(position));
    }

    @Override
    public int getItemCount() {
        return this.dnsData.size();
    }
}
