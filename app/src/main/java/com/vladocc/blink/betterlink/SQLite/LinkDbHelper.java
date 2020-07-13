package com.vladocc.blink.betterlink.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Voyager on 07.09.2017.
 */

public class LinkDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "links.db";
    public static final String TABLE_NAME = "linkDB";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_CODE = "qr_code";
    public static final String COLUMN_PREFIX = "prefix";
    public static final String COLUMN_PREFIX_TEXT = "prefix_text";

    public static final int DATABASE_VERSION = 14;

    public static final String[] DEFAULT_PROJECTION = {LinkDbHelper.COLUMN_NAME,
            LinkDbHelper.COLUMN_TYPE,
            LinkDbHelper.COLUMN_PREFIX_TEXT,
            LinkDbHelper.COLUMN_LINK,
            LinkDbHelper.COLUMN_IMAGE,
            BaseColumns._ID};

    private static LinkDbHelper instance = null;

    public static LinkDbHelper getInstance() {
        return instance;
    }

    public static void createInstance(Context context) {
        instance = new LinkDbHelper(context);
    }

    private LinkDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_LINK + " TEXT NOT NULL, "
                + COLUMN_IMAGE + " INTEGER DEFAULT " + android.R.drawable.ic_menu_view + ", "
                + COLUMN_CODE + " BLOB, "
                + COLUMN_PREFIX + " INTEGER, "
                + COLUMN_PREFIX_TEXT + " TEXT NOT NULL" + ");";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getCursor(String... columns) {
        return getReadableDatabase().query(TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor getCursor() {
        return getCursor(DEFAULT_PROJECTION);
    }

    public Cursor getEverythingAbout(int index) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + LinkDbHelper.TABLE_NAME + " WHERE _id = " + getId(index), null);
    }

    public String[] getNames() {
        Cursor cursor = getCursor(COLUMN_NAME, BaseColumns._ID);

        int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        int count = cursor.getCount();

        String[] names = new String[count];
        int i = 0;
        while (cursor.moveToNext()){
            names[i] = cursor.getString(nameIndex);
            i++;
        }

        cursor.close();

        return names;
    }

    public String getStringData(int index, String column) {
        Cursor cursor = getCursor(column, BaseColumns._ID);

        cursor.moveToPosition(index);
        int codeColumnIndex = cursor.getColumnIndex(column);
        String result = cursor.getString(codeColumnIndex);

        cursor.close();

        return result;
    }

    public int getIntData(int index, String column) {
        Cursor cursor = getCursor(column, BaseColumns._ID);

        cursor.moveToPosition(index);
        int codeColumnIndex = cursor.getColumnIndex(column);
        int result = cursor.getInt(codeColumnIndex);

        cursor.close();

        return result;
    }

    public byte[] getCode(int index) {
        Cursor cursor = getCursor(COLUMN_CODE, BaseColumns._ID);

        cursor.moveToPosition(index);
        int codeColumnIndex = cursor.getColumnIndex(LinkDbHelper.COLUMN_CODE);
        byte[] code = cursor.getBlob(codeColumnIndex);

        cursor.close();

        return code;
    }

    public int getId(int index) {
        Cursor cursor = getCursor(BaseColumns._ID);
        cursor.moveToPosition(index);
        int column = cursor.getColumnIndex(BaseColumns._ID);
        return cursor.getInt(column);
    }

    public void removeEntry(int index) {
        getReadableDatabase().delete(LinkDbHelper.TABLE_NAME, "_id=" + getId(index), null);
    }

    public void insert(ContentValues values) {
        getWritableDatabase().insert(LinkDbHelper.TABLE_NAME, null, values);
    }

    public void update(ContentValues values, int index) {
        getWritableDatabase().update(LinkDbHelper.TABLE_NAME, values, "_id=" + getId(index), null);
    }
}
