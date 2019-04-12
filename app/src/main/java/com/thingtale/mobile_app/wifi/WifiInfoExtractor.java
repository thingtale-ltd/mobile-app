package com.thingtale.mobile_app.wifi;

import android.net.wifi.ScanResult;
import android.util.Log;

public class WifiInfoExtractor {
    private static final String TAG = WifiInfoExtractor.class.getSimpleName();

    public static String getKeyMgmt(ScanResult sr) {
        final String capabilities = sr.capabilities.toUpperCase();
        Log.d(TAG, capabilities);

        if (capabilities.contains("WPA-PSK")) {
            return "WPA-PSK";
        } else if (capabilities.contains("WPA2-PSK")) {
            return "WPA2-PSK";
        }

        return "NONE";
    }
}
