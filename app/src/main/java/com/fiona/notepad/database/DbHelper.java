package com.fiona.notepad.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fiona on 15-12-22.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "notepad.db";    //数据库名字
    private static final int version = 1;               //数据库版本

    private static DbHelper dbHelper = null;
    private static SQLiteDatabase db;                   //数据库对象

    public DbHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    /**
     * 获得数据库对象 db
     *
     * @param context
     * @return
     */
    public static SQLiteDatabase getSQLiteDatabase(Context context) {
        if (null == dbHelper) {
            dbHelper = new DbHelper(context);
        }
        db = dbHelper.getWritableDatabase();
        return db;
    }

    /**
     * 创建数据库时执行
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库表：note
        db.execSQL(Note.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
