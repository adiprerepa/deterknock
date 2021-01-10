package com.example.deterknock;

import androidx.annotation.NonNull;

import java.net.InetAddress;

public class MDNSData {

    private String name;
    private InetAddress ip;
    private int port;

    public MDNSData(String name, InetAddress ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull final String name) {
        this.name = name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
