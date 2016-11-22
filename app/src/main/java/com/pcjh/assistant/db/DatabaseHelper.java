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
    public static final String TABLE_NAME_TAG ="tag";
    public static final String TABLE_NAME_MATARIAL_COLLECT = "collectmatrial" ;
    public static final String TABLE_NAME_UPDATE_TIME ="uoload_time" ;
    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String sql = "create table user( id integer PRIMARY KEY  autoincrement , nickName varchar(30) ," +
                "password varchar(60) not null ," +
                "dbPath varchar(60) not null ," +
                "face varchar(40) null ," +
                "uin varchar(40) not null);";
        db.execSQL(sql);

        String sql2 = "create table rconact( username  default  ''  PRIMARY KEY   , "  +
                "alias varchar(30) null ," +
                "nikcname varchar(20) not null ," +
                "type varchar(20) not null ," +
                "contactLabelIds text null ) ;";
        db.execSQL(sql2);


        String sql5 ="create table tag ( id integer PRIMARY KEY  autoincrement , " +
                "type varchar(10) not null ," +
                "name varchar (60) not null  " +
                ") ;" ;
        db.execSQL(sql5);

        String sql6 ="create table collectmatrial ( id integer PRIMARY KEY  autoincrement , " +
                "createtime varchar(20) not null ," +
                "material_id varchar(10) not null ," +
                "json  text not null"+
                ") ;" ;
        db.execSQL(sql6);


        String sql7 ="create table uoload_time ( id integer PRIMARY KEY  autoincrement , " +
                "lastCreateTime varchar(20) not null ," +
                "uin varchar(10) not null " +
                ") ;" ;
        db.execSQL(sql7);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
