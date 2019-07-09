package com.thingtale.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thingtale.mobile_app.content.Database;
import com.thingtale.mobile_app.content.QRCodeData;

import java.util.List;

public class ContentEditActivity extends AppCompatActivity {
    private static final String TAG = ContentEditActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set activity title
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_edit);
        setTitle(R.string.title_activity_content_edit);

        try {
            int idx = getIntent().getExtras().getInt("content_idx");
            QRCodeData c = Database.load(getApplicationContext()).get(idx);
            setQRCodeData(c);
        } catch (NullPointerException e) {
            // no extra, ignoring
        }

        // handle button click
        Button clickButton = findViewById(R.id.btn_submit);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QRCodeData qrCodeData = getQRCodeData();
                if (qrCodeData == null) {
                    return;
                }

                List<QRCodeData> contentList = Database.load(getApplicationContext());

                try {
                    int idx = getIntent().getExtras().getInt("content_idx");
                    contentList.set(idx, getQRCodeData());
                } catch (NullPointerException e) {
                    // no extra, appending a new content
                    contentList.add(getQRCodeData());
                }

                Database.save(getApplicationContext(), contentList);

                // notify edit finished
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private QRCodeData getQRCodeData() {
        QRCodeData qrCodeData = new QRCodeData();

        qrCodeData.setIsbn(((EditText) findViewById(R.id.editISBN)).getText().toString());
        qrCodeData.setAuthor(((EditText) findViewById(R.id.editAuthor)).getText().toString());
        qrCodeData.setBookName(((EditText) findViewById(R.id.editBookName)).getText().toString());
        qrCodeData.setLanguage(((EditText) findViewById(R.id.editLanguage)).getText().toString());
        qrCodeData.setAudioFile(((EditText) findViewById(R.id.editAudioFile)).getText().toString());

        final String pageNumStr = ((EditText) findViewById(R.id.editPageNum)).getText().toString();
        try {
            final int pageNum = Integer.parseInt(pageNumStr);
            qrCodeData.setPageNum(pageNum);
        } catch (NumberFormatException e) {
            Toast.makeText(getBaseContext(), "invald field: Page Number (must be a number)", Toast.LENGTH_SHORT).show();
            return null;
        }

        final String levelStr = ((EditText) findViewById(R.id.editLevel)).getText().toString();
        try {
            final int level = Integer.parseInt(levelStr);
            qrCodeData.setLevel(level);
        } catch (NumberFormatException e) {
            Toast.makeText(getBaseContext(), "invald field: Level (must be a number)", Toast.LENGTH_SHORT).show();
            return null;
        }

        return qrCodeData;
    }

    private void setQRCodeData(QRCodeData qrCodeData) {
        ((EditText) findViewById(R.id.editISBN)).setText(qrCodeData.getIsbn());
        ((EditText) findViewById(R.id.editAuthor)).setText(qrCodeData.getAuthor());
        ((EditText) findViewById(R.id.editBookName)).setText(qrCodeData.getBookName());
        ((EditText) findViewById(R.id.editLanguage)).setText(qrCodeData.getLanguage());
        ((EditText) findViewById(R.id.editAudioFile)).setText(qrCodeData.getAudioFile());

        ((EditText) findViewById(R.id.editPageNum)).setText(Integer.toString(qrCodeData.getPageNum()));
        ((EditText) findViewById(R.id.editLevel)).setText(Integer.toString(qrCodeData.getLevel()));
    }
}
