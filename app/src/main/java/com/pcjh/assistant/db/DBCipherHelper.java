package com.pcjh.assistant.db;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by szhua on 2016/11/19.
 */
public class DBCipherHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "conactdb";//数据库名字
    public static final String DB_PWD="whoislcj";//数据库密码
    public static final String TABLE_NAME_RConact ="rconact" ;
    private static final int DB_VERSION = 1;// 数据库版本
    public static final String TABLE_NAME_MSG_ID ="msgId" ;
    public static final String TABLE_NAME_MSG_ID_PRE_SEND ="msgIdPreSend" ;
    public static final String TABLE_NAME_SERVICE_USER_INFO ="infos" ;

    public DBCipherHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        //不可忽略的 进行so库加载
        SQLiteDatabase.loadLibs(context);
    }
    public DBCipherHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }
    /* 创建数据库*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        createTable(db);
    }

    private void createTable(SQLiteDatabase db){

        String sql = "create table rconact( username  default  ''  PRIMARY KEY   , "  +
                "alias varchar(30) null ," +
                "nikcname varchar(20)  null ," +
                "type varchar(20) not null ," +
                "conRemark varchar(20)  null ," +
                "contactLabelIds text null ) ;";
         String sql2 ="create table msgId (uin primary key ," +
                 "msgId varchar(20) null ) ;" ;
         String sql3 ="create table msgIdPreSend (uin primary key ," +
                "msgId varchar(20) null ) ;";

         String sql4 ="create table infos ( uin string primary key , token string ,password varchar(20) ,dbPath string ,wxid string ,username string )" ;

        try {

            db.execSQL(sql);
            db.execSQL(sql2);
            db.execSQL(sql3);
            db.execSQL(sql4);

        } catch (SQLException e) {

            Log.e(TAG, "onCreate " + TABLE_NAME_RConact + "Error" + e.toString());

            return;
        }
    }

    /*数据库升级*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
