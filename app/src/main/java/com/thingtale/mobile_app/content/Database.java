package com.thingtale.mobile_app.content;

import android.os.Environment;
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

    private static void enforceDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/thingtale/" + CONTENT_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private static String getFilePath() {
        return Environment.getExternalStorageDirectory() + "/thingtale/" + DB_FILENAME;
    }

    public static List<ContentData> load() {
        List<ContentData> contentList = new ArrayList<>();

        try {
            enforceDirectory();
            final BufferedReader br = new BufferedReader(new FileReader(getFilePath()));
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

    public static int findContent(List<ContentData> contentDataList, String strID) {
        final String[] fields = strID.split(";");
        if (fields.length != 2)
            return -1;

        final String isbn = fields[0];
        final int pageNum = Integer.parseInt(fields[1]);

        for (int i = 0; i < contentDataList.size(); i++) {
            final ContentData c = contentDataList.get(i);

            if (c.getIsbn().equals(isbn) && c.getPageNum() == pageNum)
                return i;
        }

        return -1;
    }

    public static void save(List<ContentData> contentList) {
        try {
            enforceDirectory();
            final BufferedWriter bw = new BufferedWriter(new FileWriter(getFilePath()));
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
