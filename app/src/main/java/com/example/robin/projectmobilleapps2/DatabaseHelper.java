package com.example.robin.projectmobilleapps2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Robin on 17/12/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "locations.db";
    private static final String TABLE_NAME = "location_table";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "name";
    private static final String COL_LATITUDE = "latitude";
    private static final String COL_LONGDITUDE = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_NAME + " TEXT," + COL_LATITUDE + " TEXT, " + COL_LONGDITUDE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String name,String latitude, String longitude)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues CV = new ContentValues();

        CV.put(COL_NAME,name);
        CV.put(COL_LATITUDE,latitude);
        CV.put(COL_LONGDITUDE,longitude);

        long result = db.insert(TABLE_NAME, null, CV);

        if(result == -1)
            return false;
        return true;
    }
    public Boolean isDataInDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if(res.getColumnCount()<= 0){
            return false;
        }
        return true;
    }
    public Cursor getDataAt(int position){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = " + position,null);
        return res;
    }
}
