package com.thingtale.mobile_app.content;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static final String CONTENT_DIR = "content/";
    public static final String DB_FILENAME = CONTENT_DIR + "database.csv";
    private static final String TAG = Database.class.getSimpleName();

    private static void enforceDirectory(Context context) {
        File directory = new File(context.getFilesDir() + "/" + CONTENT_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private static String getFilePath(Context context) {
        return context.getFilesDir() + "/" + DB_FILENAME;
    }

    public static List<ContentData> load(Context context) {
        List<ContentData> contentList = new ArrayList<>();

        try {
            enforceDirectory(context);
            final BufferedReader br = new BufferedReader(new FileReader(getFilePath(context)));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }

                ContentData c = ContentData.fromCSVLine(line);
                if (c == null) {
                    Log.e(TAG, "could not read \"" + line + "\" csv line in content database. ignoring it.");
                    continue;
                }

                contentList.add(c);
            }

            return contentList;
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            Log.wtf(TAG, e);
            e.printStackTrace();
        }

        return contentList;
    }

    public static void save(Context context, List<ContentData> contentList) {
        try {
            enforceDirectory(context);
            final BufferedWriter bw = new BufferedWriter(new FileWriter(getFilePath(context)));
            bw.write("#isbn;bookname;author;language;audioFile;pageNum;level\n");

            for (ContentData c : contentList) {
                bw.write(c.toCSVLine() + "\n");
            }

            bw.close();
        } catch (IOException e) {
            Log.wtf(TAG, e);
            e.printStackTrace();
        }
    }
}
