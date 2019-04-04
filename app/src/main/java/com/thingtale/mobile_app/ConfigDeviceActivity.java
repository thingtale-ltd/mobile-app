package com.thingtale.mobile_app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Math.min;

public class ConfigDeviceActivity extends AppCompatActivity {
    private static final String TAG = ConfigDeviceActivity.class.getSimpleName();

    public static String serializeConfig(String ssid, String password) throws JSONException {
        JSONObject jsonMain = new JSONObject();
        jsonMain.put("type", "config");

        JSONObject jsonConfig = new JSONObject();
        jsonConfig.put("ssid", ssid);
        jsonConfig.put("password", password);
        jsonMain.put("config", jsonConfig);

        return jsonMain.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set activity title
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_device);
        setTitle(R.string.title_config_device);

        // handle fields change
        ((EditText) findViewById(R.id.edit_ssid)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateQRCode();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // mandatory, but unneeded method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // mandatory, but unneeded method
            }
        });

        ((EditText) findViewById(R.id.edit_wifi_key)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateQRCode();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // mandatory, but unneeded method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // mandatory, but unneeded method
            }
        });
    }

    protected void updateQRCode() {
        final String ssid = ((EditText) findViewById(R.id.edit_ssid)).getText().toString();
        final String password = ((EditText) findViewById(R.id.edit_wifi_key)).getText().toString();

        try {
            final String configStr = serializeConfig(ssid, password);
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
