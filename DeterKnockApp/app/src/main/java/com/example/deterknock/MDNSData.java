package com.example.deterknock;

import androidx.annotation.NonNull;

public class MDNSData {

    private String name;

    public MDNSData(String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull final String name) {
        this.name = name;
    }
}
