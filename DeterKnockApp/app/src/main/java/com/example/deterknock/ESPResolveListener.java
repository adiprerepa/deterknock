package com.example.deterknock;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class ESPResolveListener implements NsdManager.ResolveListener {

    static String TAG = "ResolveListener";

    private ESPDiscoveryListener discoveryListener;

    public ESPResolveListener(ESPDiscoveryListener discoveryListener) {
        this.discoveryListener = discoveryListener;
    }

    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(TAG, "onResolveFailed: "+ errorCode);
    }

    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "Service Discovery Success: " + serviceInfo.toString());
    }
}
