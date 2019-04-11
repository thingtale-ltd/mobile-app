package com.thingtale.mobile_app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static java.lang.Math.min;

public class ConfigDeviceActivity extends AppCompatActivity {
    private static final String TAG = ConfigDeviceActivity.class.getSimpleName();

    private WifiManager wifiManager;
    private List<ScanResult> wifiScanResultList;

    public static String serializeConfig(String ssid, String key_mgmt, String password) throws JSONException {
        JSONObject jsonMain = new JSONObject();

        jsonMain.put("type", "wifi-config");

        jsonMain.put("ssid", ssid);
        jsonMain.put("key_mgmt", key_mgmt);
        jsonMain.put("key", password);

        return jsonMain.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set activity title
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_device);
        setTitle(R.string.title_config_device);

        // request permissions
        int permission = ActivityCompat.checkSelfPermission(ConfigDeviceActivity.this, Manifest.permission.ACCESS_WIFI_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "wifi access permission not granted");
            // We don't have permission so prompt the user
            int ACCESS_WIFI_STATE = 1;
            ActivityCompat.requestPermissions(ConfigDeviceActivity.this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    ACCESS_WIFI_STATE);
        }

        permission = ActivityCompat.checkSelfPermission(ConfigDeviceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "coarse location access permission not granted");
            // We don't have permission so prompt the user
            int ACCESS_COARSE_LOCATION = 2;
            ActivityCompat.requestPermissions(ConfigDeviceActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION);
        }

        // setup wifi scanner
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        wifiScanResultList = wifiManager.getScanResults();

                        findViewById(R.id.btn_scan_wifi).setEnabled(true);

                        // trigger another scan
                        wifiManager.startScan();
                    }
                },
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        wifiManager.startScan();

        // handle "scan wifi button"
        findViewById(R.id.btn_scan_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Wifi needs to be enabled
                // if it is disabled, propose to activate it
                if (!wifiManager.isWifiEnabled()) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    wifiManager.setWifiEnabled(true);

                                    findViewById(R.id.btn_scan_wifi).setEnabled(false);
                                    Toast.makeText(getApplicationContext(), getString(R.string.wait_scanning_wifi_AP), Toast.LENGTH_LONG).show();

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // nothing to do
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage(R.string.question_enable_wifi).
                            setPositiveButton(android.R.string.yes, dialogClickListener).
                            setNegativeButton(android.R.string.no, dialogClickListener).
                            show();

                    return;
                }

                if (wifiScanResultList == null) {
                    findViewById(R.id.btn_scan_wifi).setEnabled(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.wait_scanning_wifi_AP), Toast.LENGTH_LONG).show();
                } else {
                    final String[] ssids = new String[wifiScanResultList.size()];
                    for (int i = 0; i < ssids.length; i++) {
                        ssids[i] = wifiScanResultList.get(i).SSID;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Wifi list");
                    builder.setItems(ssids, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ScanResult sr = wifiScanResultList.get(which);

                            ((EditText) findViewById(R.id.et_ssid)).setText(sr.SSID);
                            ((EditText) findViewById(R.id.et_key_mgmt)).setText(sr.capabilities);
                        }
                    });
                    builder.show();
                }
            }
        });

        // handle fields change
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateQRCode();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        ((EditText) findViewById(R.id.et_ssid)).addTextChangedListener(textWatcher);
        ((EditText) findViewById(R.id.et_key_mgmt)).addTextChangedListener(textWatcher);
        ((EditText) findViewById(R.id.et_wifi_key)).addTextChangedListener(textWatcher);
    }

    protected void updateQRCode() {
        final String ssid = ((EditText) findViewById(R.id.et_ssid)).getText().toString();
        final String key_mgmgt = ((EditText) findViewById(R.id.et_key_mgmt)).getText().toString();
        final String password = ((EditText) findViewById(R.id.et_wifi_key)).getText().toString();

        try {
            final String configStr = serializeConfig(ssid, key_mgmgt, password);
            Bitmap qrCodeBitmap = QRCode.from(configStr).bitmap();

            ImageView imgView = findViewById(R.id.img_qrcode_display);

            // scale bitmap without interpolation
            final int mw = imgView.getWidth();
            final int mh = imgView.getHeight();
            qrCodeBitmap = Bitmap.createScaledBitmap(qrCodeBitmap, min(mw, mh), min(mh, mw), false);

            imgView.setImageBitmap(qrCodeBitmap);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
            Toast.makeText(getApplicationContext(), "QR Code could not be built", Toast.LENGTH_SHORT).show();
        }
    }
}
