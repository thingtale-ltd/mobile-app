package com.thingtale.mobile_app.content;

import android.graphics.Bitmap;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

public class ContentData {
    private static final String TAG = ContentData.class.getSimpleName();

    private String isbn;
    private String bookName;
    private String author;
    private String language;
    private String audioFile;
    private int pageNum;
    private int level;

    public ContentData() {
    }

    public ContentData(String isbn, String bookName, String author, String language, String audioFile, int pageNum, int level) {
        this.isbn = isbn;
        this.bookName = bookName;
        this.author = author;
        this.language = language;
        this.audioFile = audioFile;
        this.pageNum = pageNum;
        this.level = level;
    }

    public static ContentData fromJson(String jsonStr) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonStr);

        final String isbn = jsonObj.getString("isbn");
        final String bookName = jsonObj.getString("bookName");
        final String author = jsonObj.getString("author");
        final String language = jsonObj.getString("language");
        final String audioFile = jsonObj.getString("audioFile");
        final int pageNum = jsonObj.getInt("pageNum");
        final int level = jsonObj.getInt("level");

        return new ContentData(isbn, bookName, author, language, audioFile, pageNum, level);
    }

    public static ContentData fromCSVLine(String line) {
        String[] split = line.split(";");

        if (split.length != 7) {
            return null;
        }

        return new ContentData(split[0], split[1], split[2], split[3], split[4], Integer.parseInt(split[5]), Integer.parseInt(split[6]));
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String toStrID() {
        return this.isbn + ";" + this.pageNum;
    }

    public String toCSVLine() {
        String csvResult = "";

        csvResult += this.isbn + ";";
        csvResult += this.bookName + ";";
        csvResult += this.author + ";";
        csvResult += this.language + ";";
        csvResult += this.audioFile + ";";
        csvResult += this.pageNum + ";";
        csvResult += this.level;

        return csvResult;
    }

    public Bitmap getBitmap(int size) {
        return QRCode.from(toStrID()).withSize(size, size).withErrorCorrection(ErrorCorrectionLevel.H).bitmap();
    }
}
