package com.thingtale.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thingtale.mobile_app.content.ContentData;
import com.thingtale.mobile_app.content.Database;

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
            ContentData c = Database.load().get(idx);
            setQRCodeData(c);
        } catch (NullPointerException e) {
            // no extra, ignoring
        }

        // handle button click
        Button clickButton = findViewById(R.id.btn_submit);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ContentData contentData = getQRCodeData();
                if (contentData == null) {
                    return;
                }

                List<ContentData> contentList = Database.load();

                try {
                    int idx = getIntent().getExtras().getInt("content_idx");
                    contentList.set(idx, getQRCodeData());
                } catch (NullPointerException e) {
                    // no extra, appending a new content
                    contentList.add(getQRCodeData());
                }

                Database.save(contentList);

                // notify edit finished
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private ContentData getQRCodeData() {
        ContentData contentData = new ContentData();

        contentData.setIsbn(((EditText) findViewById(R.id.editISBN)).getText().toString());
        contentData.setAuthor(((EditText) findViewById(R.id.editAuthor)).getText().toString());
        contentData.setBookName(((EditText) findViewById(R.id.editBookName)).getText().toString());
        contentData.setLanguage(((EditText) findViewById(R.id.editLanguage)).getText().toString());
        contentData.setAudioFile(((EditText) findViewById(R.id.editAudioFile)).getText().toString());

        final String pageNumStr = ((EditText) findViewById(R.id.editPageNum)).getText().toString();
        try {
            final int pageNum = Integer.parseInt(pageNumStr);
            contentData.setPageNum(pageNum);
        } catch (NumberFormatException e) {
            Toast.makeText(getBaseContext(), "invald field: Page Number (must be a number)", Toast.LENGTH_SHORT).show();
            return null;
        }

        final String levelStr = ((EditText) findViewById(R.id.editLevel)).getText().toString();
        try {
            final int level = Integer.parseInt(levelStr);
            contentData.setLevel(level);
        } catch (NumberFormatException e) {
            Toast.makeText(getBaseContext(), "invald field: Level (must be a number)", Toast.LENGTH_SHORT).show();
            return null;
        }

        return contentData;
    }

    private void setQRCodeData(ContentData contentData) {
        ((EditText) findViewById(R.id.editISBN)).setText(contentData.getIsbn());
        ((EditText) findViewById(R.id.editAuthor)).setText(contentData.getAuthor());
        ((EditText) findViewById(R.id.editBookName)).setText(contentData.getBookName());
        ((EditText) findViewById(R.id.editLanguage)).setText(contentData.getLanguage());
        ((EditText) findViewById(R.id.editAudioFile)).setText(contentData.getAudioFile());

        ((EditText) findViewById(R.id.editPageNum)).setText(Integer.toString(contentData.getPageNum()));
        ((EditText) findViewById(R.id.editLevel)).setText(Integer.toString(contentData.getLevel()));
    }
}
