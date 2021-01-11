package com.example.deterknock;

import android.app.Activity;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ESPDiscoveryListener implements NsdManager.DiscoveryListener {

    public static String TAG = "Esp8266DiscoveryListener";
    private AtomicBoolean resolveListenerBusy = new AtomicBoolean(false);
    private ConcurrentLinkedQueue<NsdServiceInfo> pendingNsdServices = new ConcurrentLinkedQueue<>();
    private List<NsdServiceInfo> resolvedNsdServices = Collections.synchronizedList(new ArrayList<>());
    private NsdManager manager;
    private MDNSAdapter adapter;
    private List<MDNSData> items;
    private Activity activity;

    public ESPDiscoveryListener(NsdManager manager, List<MDNSData> items, MDNSAdapter adapter, Activity activity) {
        this.manager = manager;
        this.items = items;
        this.adapter = adapter;
        this.activity = activity;
    }

    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.e(TAG, "Start Discovery failed: Error code:" + errorCode);
        manager.stopServiceDiscovery(this);
    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.e(TAG, "Stop Discovery failed: Error code:" + errorCode);
        manager.stopServiceDiscovery(this);
    }

    @Override
    public void onDiscoveryStarted(String serviceType) {
        Log.d(TAG, "Service discovery started");
    }

    @Override
    public void onDiscoveryStopped(String serviceType) {
        Log.i(TAG, "Discovery stopped: " + serviceType);
    }

    @Override
    public void onServiceFound(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "onServiceFound: " + serviceInfo.toString());
        if (resolveListenerBusy.compareAndSet(false, true)) {
            this.manager.resolveService(serviceInfo, new ESPResolveListener(this));
        } else {
            pendingNsdServices.add(serviceInfo);
        }
    }

    @Override
    public void onServiceLost(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "onServiceLost: " + serviceInfo.toString());
        Iterator<NsdServiceInfo> iterator = pendingNsdServices.iterator();
        while(iterator.hasNext())
            if (iterator.next().getServiceName().equals(serviceInfo.getServiceName()))
                iterator.remove();

        synchronized (resolvedNsdServices) {
            iterator = resolvedNsdServices.iterator();
            while (iterator.hasNext())
                if (iterator.next().getServiceName().equals(serviceInfo.getServiceName()))
                    iterator.remove();
        }
    }

    public AtomicBoolean getResolveListenerBusy() {
        return resolveListenerBusy;
    }

    public ConcurrentLinkedQueue<NsdServiceInfo> getPendingNsdServices() {
        return pendingNsdServices;
    }

    public List<NsdServiceInfo> getResolvedNsdServices() {
        return resolvedNsdServices;
    }

    public NsdManager getManager() {
        return manager;
    }

    public MDNSAdapter getAdapter() {
        return adapter;
    }

    public List<MDNSData> getItems() {
        Log.d(TAG, "getItems: " + this.items.size());
        return items;
    }

    public void setItems(List<MDNSData> items) {
        this.items = items;
    }

    public Activity getActivity() {
        return activity;
    }
}
