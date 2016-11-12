package com.pcjh.assistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by szhua on 2016/10/18.
 */
public class DatabaseHelper  extends SQLiteOpenHelper{

    public static final String DB_NAME = "User.db"; //数据库名称
    private static final int version = 1; //数据库版本
    public static final String TABLE_NAME_User ="user" ;
    public static final String TABLE_NAME_RConact ="rconact" ;
    public static final String TABLE_NAME_LABEL ="label" ;
    public static final String TABLE_NAME_LABELCONACT ="lc" ;
    public static final String TABLE_NAME_TAG ="tag";
    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
     //   String sql1 ="CREATE TABLE person (personid integer primary key autoincrement, name varchar(20))" ;
        String sql = "create table user( id integer PRIMARY KEY  autoincrement , nickName varchar(30) ," +
                "password varchar(60) not null ," +
                "dbPath varchar(60) not null ," +
                "face varchar(40) null ," +
                "uin varchar(40) not null);";
        db.execSQL(sql);

        String sql2 = "create table rconact( id integer PRIMARY KEY  autoincrement , "  +
                "username varchar(30)  , " +
                "alias varchar(30) null ," +
                "nikcname varchar(20) not null ," +
                "type varchar(20) not null ," +
                "talker varchar(20) not null ,"+
                "contactLabelIds text null ) ;";


        db.execSQL(sql2);


        String sql4 ="create table lc ( id integer PRIMARY KEY  autoincrement , " +
                "labelid varchar(10) not null ," +
                "labelname varchar (60) not null , " +
                "username  varchar (60) not null , " +
                "alias varchar (60) not null "  +
                ") ;" ;

        db.execSQL(sql4);


        String sql5 ="create table tag ( id integer PRIMARY KEY  autoincrement , " +
                "type varchar(10) not null ," +
                "name varchar (60) not null  " +
                ") ;" ;

        db.execSQL(sql5);


        String sql3 ="create table label ( id integer PRIMARY KEY  autoincrement , " +
                "labelid varchar(10) not null ," +
                "labelname varchar (60) not null "  +
                ") ;" ;
        db.execSQL(sql3);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
