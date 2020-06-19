package com.androidpytorch89.audio_pytorch;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper  extends SQLiteOpenHelper {

    private static  final int DATABASE_VERSION=1;
    private static final  String DataBase_Name="Record";
    private static final  String key_Cough="Cough";
    private static final  String key_Sneeze="Sneeze";
    private static final  String key_other="Other";

    public DatabaseHelper(tensor context)
    {
        super(context,DataBase_Name,null,DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql="CREATE TABLE Record(_id INTEGER PRIMARY KEY AUTOINCREMENT, PROBLEM TEXT, COUNT REAL)";
        db.execSQL(sql);

        //insert
        insertData("Cough",0,db);
        insertData("Sneeze",0,db);
        insertData("Other",0,db);



    }
    private void insertData( String problem, Integer co, SQLiteDatabase Database)
    {
        ContentValues values= new ContentValues();
        values.put("PROBLEM",problem);

        values.put("COUNT",co);
        Database.insert("RECORD",null,values);





    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}