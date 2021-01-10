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

    public void setDiscoveryListener(ESPDiscoveryListener discoveryListener) {
        this.discoveryListener = discoveryListener;
    }

    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(TAG, "onResolveFailed: "+ errorCode);
        resolveNextInQueue();
    }

    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {
        this.discoveryListener.getResolvedNsdServices().add(serviceInfo);
        // add to list or something
        Log.d(TAG, "Service Resolution Success: " + serviceInfo.toString());
//        this.discoveryListener.getItems().add(new MDNSData(serviceInfo.getServiceName(), serviceInfo.getHost(), serviceInfo.getPort()));
        // if this doesnt work try using notifyItemChanged(int, object)
        this.discoveryListener.getActivity().runOnUiThread(() -> {
            Log.d(TAG, "run: " + "YOOTOTOTO");
            discoveryListener.getItems().add(new MDNSData(serviceInfo.getServiceName(), serviceInfo.getHost(), serviceInfo.getPort()));
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
