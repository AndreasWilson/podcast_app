package com.example.wilson.podcast_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by andre on 18.03.2017.
 */

public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "PodCastDB.db";
    public static final String TABLE_NAME = "PodCasts";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMG = "image";
    public static final String KEY_URL = "url";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IMG + " TEXT,"
                + KEY_URL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long createItem(iTunesItem item) {
        long c;

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, item.getId());
        values.put(KEY_NAME, item.getName());
        values.put(KEY_IMG, item.getImg());
        values.put(KEY_URL, item.getUrl());

        c = database.insert(TABLE_NAME, null, values);
        database.close();
        return c;
    }

    public ArrayList<iTunesItem> getAllItems() {
        String query = "SELECT * FROM " + TABLE_NAME;
        ArrayList<iTunesItem> iTunesItems = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(KEY_ID));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String img = c.getString(c.getColumnIndex(KEY_IMG));
                String url = c.getString(c.getColumnIndex(KEY_URL));

                iTunesItem tunesItem = new iTunesItem();
                tunesItem.setId(id);
                tunesItem.setName(name);
                tunesItem.setImg(img);
                tunesItem.setUrl(url);

                //Log.v("DBHelper: ", "Name: " + name);
                //Log.v("DBHelper: ", "Code: " + id);
                //Log.v("DBHelper: ", "Email: " + img);
                //Log.v("DBHelper: ", "Address: " + url);

                iTunesItems.add(tunesItem);
            }
        }
        return iTunesItems;
    }
    public iTunesItem getItemsByID(int id)
    {
        String query = "SELECT * FROM " + TABLE_NAME+ " WHERE "+ KEY_ID + " = " +id;
        iTunesItem tunesItem = new iTunesItem();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);

        if (c.getCount() > 0) {

            c.moveToFirst();
            int idCode = c.getInt(c.getColumnIndex(KEY_ID));
            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String img = c.getString(c.getColumnIndex(KEY_IMG));
            String url = c.getString(c.getColumnIndex(KEY_URL));

            tunesItem.setId(idCode);
            tunesItem.setName(name);
            tunesItem.setImg(img);
            tunesItem.setUrl(url);

            Log.v("DBHelper: ", "Name: " + name);
            Log.v("DBHelper: ", "Code: " + idCode);
            Log.v("DBHelper: ", "Email: " + img);
            Log.v("DBHelper: ", "Address: " + url);


        }
        return tunesItem;
    }
    public void removeSingleContact(String title) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + KEY_NAME + "= '" + title + "'");
        System.out.println("Deleted");
        //Close the database
        database.close();
    }
}
