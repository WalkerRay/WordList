package com.example.wordslist;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import databace.MyDatabaseHelper;

public class MyContentProvider extends ContentProvider {

    public static final int AllWords_DIR = 0;
    public static final int NewWords_DIR = 1;

    public static final String AUTHORITY = "com.example.wordslist.provider";

    public static UriMatcher uriMatcher;

    private MyDatabaseHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "AllWords", AllWords_DIR);
        uriMatcher.addURI(AUTHORITY, "NewWords", NewWords_DIR);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MyDatabaseHelper(getContext(), "WordList.db", null, 2);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selecton, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case AllWords_DIR:
                cursor = db.query("AllWords", projection, selecton, selectionArgs, null, null, sortOrder);
                break;
            case NewWords_DIR:
                cursor = db.query("NewWords", projection, selecton, selectionArgs, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case AllWords_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.wordslist.provider.AllWords";
            case NewWords_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.wordslist.provider.NewWords";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case AllWords_DIR:
                long addAllWordId = db.insert("AllWords", null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/AllWords/" + addAllWordId);
                break;
            case NewWords_DIR:
                long addNewWordId = db.insert("NewWords", null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/NewWords/" + addNewWordId);
                break;
            default:
                break;
        }
        return uriReturn;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deleteRows = 0;
        switch (uriMatcher.match(uri)){
            case AllWords_DIR:
                deleteRows = db.delete("AllWords", selection, selectionArgs);
                break;
            case NewWords_DIR:
                deleteRows = db.delete("NewWords", selection, selectionArgs);
                break;
        }
        return deleteRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updateRows = 0;
        switch (uriMatcher.match(uri)){
            case AllWords_DIR:
                updateRows = db.update("AllWords", values, selection, selectionArgs);
                break;
            case NewWords_DIR:
                updateRows = db.update("NewWords", values, selection, selectionArgs);
                break;
            default:
                break;
        }
        return updateRows;
    }
}
