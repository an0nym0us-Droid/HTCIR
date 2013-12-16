package com.collectirdata.src;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.htc.htcircontrol.HtcIrData;

public class DatabaseHandler extends SQLiteOpenHelper{
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "remotes.db";
    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
 
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    
    public void createTable(String tablename){
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + tablename + "(func_id INTEGER, rc INTEGER, freq INTEGER, data TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
        db.close();
    }
    
    public void insertIntoTable(String tablename,Integer func_id, Integer rc, Integer freq, String data){
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values =  new ContentValues();
    	values.put("func_id", func_id);
    	values.put("rc", rc);
    	values.put("freq", freq);
    	values.put("data", data);
    	Cursor pre = db.rawQuery("SELECT func_id FROM "+tablename+" WHERE func_id=?", new String[]{func_id.toString()});
    	if(pre.moveToFirst()){
    		Log.e("UPDATING", func_id.toString());
    		db.update(tablename, values, "func_id=?", new String[]{func_id.toString()});
    		return;
    	}
    	else{
    		Log.e("CREATING",func_id.toString());
    	}
    	
  
    	db.insert(tablename, null, values);
    	
    	db.close();
    }
    
    public ArrayList<String> getExistingRemotes(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor cursor = db.rawQuery("SELECT name from sqlite_master WHERE type=?", new String[]{"table"});
    	cursor.moveToFirst();
    	ArrayList<String> tablenames = new ArrayList<String>();
    	while(cursor.moveToNext()){
    		tablenames.add(cursor.getString(0));
    	}
    	return tablenames;
    	
    }
    
    public HtcIrData returnHTCIR(String tablename, Integer func_id){
    	
    	Log.e("DB:",tablename+":"+func_id);
    	
    	HtcIrData irdata = null;
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	Cursor cursor = db.query(tablename, new String[]{"rc","freq","data"}, "func_id=?", new String[]{func_id.toString()},null,null,null);
    	if(cursor.moveToFirst()){
    		
    		int rc,freq;
        	String sData;
        	
        	rc=cursor.getInt(0);
        	freq=cursor.getInt(1);
        	sData=cursor.getString(2);
        	
        	String[] sDataArray = sData.split(",");
        	
        	int[] data = new int[sDataArray.length];
        	for(int i=0;i<sDataArray.length;i++){
        		data[i] = Integer.parseInt(sDataArray[i]);
        	}
        	
            irdata = new HtcIrData(rc, freq, data);
    	}
    	
    	
    	
    	return irdata;
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
        // Create tables again
        onCreate(db);
    }
}
