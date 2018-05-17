package com.octopus.myownsqlite.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.octopus.myownsqlite.dao.NormalUserDao;


public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public MySQLiteOpenHelper(Context context) {
        super(context, "androidSql.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        NormalUserDao.T.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
