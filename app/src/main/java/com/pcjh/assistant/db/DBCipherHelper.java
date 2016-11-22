package com.pcjh.assistant.db;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by 单志华 on 2016/11/19.
 */
public class DBCipherHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "conactdb";//数据库名字
    public static final String DB_PWD="whoislcj";//数据库密码
    public static final String TABLE_NAME_RConact ="rconact" ;
    private static final int DB_VERSION = 1;// 数据库版本

    public DBCipherHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        //不可忽略的 进行so库加载
        SQLiteDatabase.loadLibs(context);
    }

    public DBCipherHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 创建数据库
     * @param db
     */
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
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            Log.e(TAG, "onCreate " + TABLE_NAME_RConact + "Error" + e.toString());
            return;
        }
    }

    /**
     * 数据库升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
