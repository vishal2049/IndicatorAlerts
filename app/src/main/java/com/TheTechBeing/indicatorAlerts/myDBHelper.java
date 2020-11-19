package com.TheTechBeing.indicatorAlerts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class myDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mIndicatorsDB";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_EMA1 = "EMA1";
    private static final String COLUMN_EMA2 = "EMA2";
    private static final String COLUMN_CB_ID = "id";
    private static final String TABLE_CHECKBOX_TIME = "checkBoxTime";
    private static final String COLUMN_CTIME = "cbtime";
    public static ArrayList<String> coinsTableList;


    public myDBHelper(@Nullable Context context, ArrayList<String> coins) {
        super(context, DATABASE_NAME, null, 1);
        coinsTableList = coins;
        context.openOrCreateDatabase(DATABASE_NAME,context.MODE_PRIVATE,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String coin : coinsTableList)
            db.execSQL("CREATE TABLE IF NOT EXISTS " + coin + " (" + COLUMN_TIME+ " TEXT PRIMARY KEY, " + COLUMN_EMA1 + " REAL, " + COLUMN_EMA2 + " REAL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHECKBOX_TIME + " (" +COLUMN_CB_ID+" INTEGER PRIMARY KEY, "+ COLUMN_CTIME + " TEXT" +");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String coin : coinsTableList)
            db.execSQL("DROP TABLE IF EXISTS " + coin);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKBOX_TIME);
        onCreate(db);
    }

    public boolean addData(String symbol, String time, double EMA1, double EMA2) {
        SQLiteDatabase mdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_EMA1, EMA1);
        values.put(COLUMN_EMA2, EMA2);
        long result = mdb.insert(symbol, null, values);
        return result != -1;
    }
    public boolean addDataMA1(String symbol, String time, double MA1) {
        SQLiteDatabase mdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_EMA1, MA1);
        long result = mdb.insert(symbol, null, values);
        return result != -1;
    }
    public boolean addDataMA2(String symbol, String time, double MA2) {
        SQLiteDatabase mdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_EMA2, MA2);
        long result = mdb.insert(symbol, null, values);
        return result != -1;
    }

    public boolean addDataTime(String symbol, String time) {
        SQLiteDatabase mdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        long result = mdb.insert(symbol, null, values);
        return result != -1;
    }

    public boolean addCheckBoxTime(String ctime,int id) {
        SQLiteDatabase mdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CB_ID, id);
        values.put(COLUMN_CTIME, ctime);
        long result = mdb.insert(TABLE_CHECKBOX_TIME, null, values);
        return result != -1;
    }
    public boolean isTimeSelected(String ctime) {
        SQLiteDatabase mdb = this.getReadableDatabase();
        String query = "SELECT "+COLUMN_CTIME+" FROM " + TABLE_CHECKBOX_TIME + " WHERE " + COLUMN_CTIME + " LIKE '" + ctime + "'";
        Cursor c = mdb.rawQuery(query, null);
//        Log.d("mytag","getcount= "+c.getCount());
        c.moveToNext();
//        Log.d("mytag","string= "+c.getString(0));
        return c.getString(0).equals(ctime); //query will return 1 if it finds the value
    }
    public ArrayList<String> getCBTimeList() {
        ArrayList<String> tList = new ArrayList<>();
        SQLiteDatabase mdb = this.getReadableDatabase();
        String query = "SELECT "+COLUMN_CTIME+" FROM " + TABLE_CHECKBOX_TIME;
        Cursor c = mdb.rawQuery(query, null);
        while(c.moveToNext() && c.getCount()>0){
            tList.add(c.getString(0));
//            Log.d("mytag","(timeList updated!)stored Time is= "+c.getString(0));
        }
        return tList;
    }
    public ArrayList<String> getTimeList(String symbol) {
        ArrayList<String> tList = new ArrayList<>();
        SQLiteDatabase mdb = this.getReadableDatabase();
        String query = "SELECT "+COLUMN_TIME+" FROM " + symbol;
        Cursor c = mdb.rawQuery(query, null);
        while(c.moveToNext() && c.getCount()>0){
            tList.add(c.getString(0));
            Log.d("mytag","getTimeList()= "+c.getString(0));
        }
        return tList;
    }

    public double getEMA(String symbol, String time, String EMA) {
        SQLiteDatabase mdb = this.getReadableDatabase();
        String query = "SELECT " + EMA + " FROM " + symbol + " WHERE " + COLUMN_TIME + " LIKE '" + time + "'";
        Cursor c = mdb.rawQuery(query, null);
        c.moveToNext();
        return c.getDouble(0);
    }
    public Cursor getEMACursor(String symbol, String time, String EMA) {
        SQLiteDatabase mdb = this.getReadableDatabase();
        String query = "SELECT " + EMA + " FROM " + symbol + " WHERE " + COLUMN_TIME + " LIKE '" + time + "'";
        return mdb.rawQuery(query, null);
    }
    public boolean removeCheckBoxTime(String ctime,String id){
        SQLiteDatabase mdb = this.getWritableDatabase();
        int r = mdb.delete(TABLE_CHECKBOX_TIME,COLUMN_CB_ID+"=? and "+COLUMN_CTIME+"=? ",new String[] {id,ctime});
        return r != -1;
    }

    public void updateEMADataBase(String symbol, String time, double EMA1, double EMA2){
        String t = "'"+time+"'";
        SQLiteDatabase mdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMA1,EMA1);
        values.put(COLUMN_EMA2,EMA2);
        mdb.execSQL("UPDATE "+symbol+" SET "+COLUMN_EMA1+"="+EMA1+", "+COLUMN_EMA2+"="+EMA2+" WHERE "+COLUMN_TIME+"="+t);
    }
    public void updateEMADataBase1(String symbol, String time, double EMA1){
        String t = "'"+time+"'";
        SQLiteDatabase mdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMA1,EMA1);
        mdb.execSQL("UPDATE "+symbol+" SET "+COLUMN_EMA1+"="+EMA1 +" WHERE "+COLUMN_TIME+"="+t);
    }
    public void updateEMADataBase2(String symbol, String time, double EMA2){
        String t = "'"+time+"'";
        SQLiteDatabase mdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMA2,EMA2);
        mdb.execSQL("UPDATE "+symbol+" SET "+COLUMN_EMA2+"="+EMA2+" WHERE "+COLUMN_TIME+"="+t);
    }
}
