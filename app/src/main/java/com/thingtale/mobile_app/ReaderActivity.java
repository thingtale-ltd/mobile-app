package com.thingtale.mobile_app;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.thingtale.mobile_app.content.ContentData;
import com.thingtale.mobile_app.content.Database;

import java.io.IOException;

public class ReaderActivity extends AppCompatActivity {
    private static final String TAG = ReaderActivity.class.getSimpleName();

    private static MediaPlayer mediaPlayer;
    private static String soundFilePath;

    private ContentData cData;

    private SeekBar seekBar;
    private Handler seekbarUpdateHandler = new Handler();
    private Runnable seekbarUpdater = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekbarUpdateHandler.postDelayed(this, 750);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        setTitle(R.string.title_activity_reader);

        seekBar = findViewById(R.id.seekBar);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMediaPlayer();
            }
        });

        findViewById(R.id.btn_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseMediaPlayer();
            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMediaPlayer();
            }
        });

        try {
            int idx = getIntent().getExtras().getInt("content_idx");
            cData = Database.load().get(idx);

            // reading book: <bookName>, page <pageNum>
            setTitle(getString(R.string.title_activity_reader) + ": " + cData.getBookName() + ", " + getString(R.string.page) + " " + cData.getPageNum());
        } catch (NullPointerException e) {
            Log.e(TAG, "no intent, cannot select which content to read");
            finish();
        }

        final String fileFromIntent = Environment.getExternalStorageDirectory() + "/thingtale/content/sound/" + cData.getAudioFile();
        Log.d(TAG, "going to read: " + fileFromIntent);

        if (mediaPlayer == null || !soundFilePath.equals(fileFromIntent)) {
            soundFilePath = "";

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setLooping(false);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish();
                }
            });
        }

        if (fileFromIntent.equals(soundFilePath)) {
            startMediaPlayer();
        } else {
            soundFilePath = fileFromIntent;

            try {
                mediaPlayer.setDataSource(soundFilePath);
            } catch (IOException e) {
                Toast.makeText(this, soundFilePath + " not found", Toast.LENGTH_LONG).show();
            }

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startMediaPlayer();
                }
            });
            mediaPlayer.prepareAsync();
        }
    }

    private void startMediaPlayer() {
        seekBar.setMax(mediaPlayer.getDuration());
        findViewById(R.id.btn_start).setVisibility(View.GONE);
        findViewById(R.id.btn_pause).setVisibility(View.VISIBLE);

        mediaPlayer.start();
        seekbarUpdateHandler.postDelayed(seekbarUpdater, 0);
    }

    private void stopMediaPlayer() {
        mediaPlayer.stop();
        seekbarUpdateHandler.removeCallbacks(seekbarUpdater);
        finish();
    }

    private void pauseMediaPlayer() {
        findViewById(R.id.btn_start).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_pause).setVisibility(View.GONE);

        mediaPlayer.pause();
        seekbarUpdateHandler.removeCallbacks(seekbarUpdater);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            startMediaPlayer();
        } else {
            pauseMediaPlayer();
        }
    }
}
