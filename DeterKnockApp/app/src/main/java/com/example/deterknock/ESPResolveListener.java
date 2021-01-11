package com.example.deterknock;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ESPResolveListener implements NsdManager.ResolveListener {

    static String TAG = "ResolveListener";

    private ESPDiscoveryListener discoveryListener;

    public ESPResolveListener(ESPDiscoveryListener discoveryListener) {
        this.discoveryListener = discoveryListener;
    }

    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(TAG, "onResolveFailed: "+ errorCode);
        resolveNextInQueue();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {
        this.discoveryListener.getResolvedNsdServices().add(serviceInfo);
        Log.d(TAG, "Service Resolution Success: " + serviceInfo.toString());
        this.discoveryListener.getActivity().runOnUiThread(() -> {
            Log.d(TAG, "run: " + "YOOTOTOTO");
            discoveryListener.getItems().add(new MDNSData(serviceInfo.getServiceName(), serviceInfo.getHost(), serviceInfo.getPort()));
            List<MDNSData> items = discoveryListener.getItems();
            Set<String> seenIps = new HashSet<>();
            // im a fucking genius thats what
            for (int i = items.size() - 1; i >= 0; i--) {
                if (!seenIps.add(items.get(i).getIp().toString())) {
                    Log.d(TAG, "onServiceResolved: Removed Duplicate MDNS Entry: " + items.remove(i).toString());
                }
            }
            discoveryListener.setItems(items);
            Log.d(TAG, "size of list: " + discoveryListener.getItems().size());
            discoveryListener.getAdapter().notifyDataSetChanged();
        });
        resolveNextInQueue();

    }

    private void resolveNextInQueue() {
        NsdServiceInfo nextNdsService = this.discoveryListener.getPendingNsdServices().poll();
        if (nextNdsService != null)
            // try replacing "this" with new ESPResolveListener
            this.discoveryListener.getManager().resolveService(nextNdsService, this);
        else
            this.discoveryListener.getResolveListenerBusy().set(false);
    }
}
