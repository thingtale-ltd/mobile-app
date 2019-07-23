package com.thingtale.mobile_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.thingtale.mobile_app.content.ContentData;
import com.thingtale.mobile_app.content.Database;

import java.util.List;

import de.klimek.scanner.OnCameraErrorCallback;
import de.klimek.scanner.OnDecodedCallback;
import de.klimek.scanner.ScannerView;

public class ScannerActivity extends AppCompatActivity implements OnDecodedCallback, OnCameraErrorCallback {
    private static final String TAG = ScannerActivity.class.getSimpleName();

    private ScannerView scanner;
    private boolean cameraPermissionGranted;

    private List<ContentData> listContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // force portrait mode
        setContentView(R.layout.activity_scanner);
        setTitle(R.string.title_activity_scanner);

        scanner = findViewById(R.id.scanner);
        scanner.setOnDecodedCallback(this);
        scanner.setOnCameraErrorCallback(this);

        // get camera permission
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        cameraPermissionGranted = cameraPermission == PackageManager.PERMISSION_GRANTED;
        if (!cameraPermissionGranted) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    Permission.CAMERA);
        }

        listContent = Database.load();
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
        scanner.stopScanning();

        final int idx = Database.findContent(listContent, decodedData);

        if (idx < 0) {
            final String errorMsg = "content not found in database";
            Log.e(TAG, errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();

            scanner.startScanning();
        } else {
            Intent intent = new Intent(ScannerActivity.this, ReaderActivity.class);
            intent.putExtra("content_idx", idx);
            startActivityForResult(intent, 3);
        }
    }

    @Override
    public void onCameraError(Exception error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        Log.wtf(TAG, error.getMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3) { // if reader activity returned
            scanner.startScanning();
        }
    }
}
