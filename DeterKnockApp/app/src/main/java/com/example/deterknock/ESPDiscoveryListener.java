package com.example.deterknock;

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
    private ESPResolveListener resolveListener;

    public ESPDiscoveryListener(NsdManager manager, ESPResolveListener resolveListener) {
        this.manager = manager;
        this.resolveListener = resolveListener;
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
        if (resolveListenerBusy.compareAndSet(false, true) && serviceInfo.getServiceName().equals("aditya_door")) {
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
}
