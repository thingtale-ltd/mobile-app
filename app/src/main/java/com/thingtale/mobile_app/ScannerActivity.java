package com.thingtale.mobile_app;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import de.klimek.scanner.OnCameraErrorCallback;
import de.klimek.scanner.OnDecodedCallback;
import de.klimek.scanner.ScannerView;

public class ScannerActivity extends AppCompatActivity implements OnDecodedCallback, OnCameraErrorCallback {
    private static final String TAG = ScannerActivity.class.getSimpleName();

    private ScannerView scanner;
    private boolean cameraPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // force portrait mode
        setContentView(R.layout.activity_scanner);
        setTitle(R.string.title_activity_scanner);

        scanner = findViewById(R.id.scanner);
        scanner.setOnDecodedCallback(this);
        scanner.setOnCameraErrorCallback(this);

        // get permission
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        cameraPermissionGranted = cameraPermission == PackageManager.PERMISSION_GRANTED;
        if (!cameraPermissionGranted) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    Permission.CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permission.CAMERA && grantResults.length > 0) {
            cameraPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraPermissionGranted) {
            scanner.startScanning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.stopScanning();
    }

    @Override
    public void onDecoded(String decodedData) {
        Toast.makeText(this, decodedData, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraError(Exception error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        Log.wtf(TAG, error.getMessage());
    }
}
