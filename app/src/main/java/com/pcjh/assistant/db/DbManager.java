package com.pcjh.assistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szhua on 2016/10/18.
 */
public class DbManager {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DbManager(Context context)
    {
        helper = new DatabaseHelper(context);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void addLabel(List<Label> labels){
        db.beginTransaction(); // 开始事务
        try
        {
            for (Label label : labels)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_LABEL +"('labelid','labelname')"
                        + " VALUES(?, ?)", new Object[]{label.getLabelID(),
                        label.getLabelName()});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }


    public void addRconact(List<RConact> conacts){
        //  Log.i("users",userses.toString()) ;
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (RConact con : conacts)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_RConact +"('username','alias','nikcname','type','contactLabelIds','talker')"
                        + " VALUES(?, ?, ?, ?, ?,? )", new Object[]{con.getTalker(),
                        con.getAlias(), con.getNickname(),con.getType(),con.getContactLabelIds(),con.getTalker()});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }


    /**
     * add persons
     *
     * @param userses
     */
    public void add(List<Users> userses)
    {
      //  Log.i("users",userses.toString()) ;
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (Users user : userses)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_User +"('nickName','face','dbPath','password','uin')"
                        + " VALUES(?, ?, ?, ?, ? )", new Object[]{user.getNickName(),
                        user.getFace(), user.getDbPath(),user.getPassword(),user.getUin() });
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }

    /**
     * update person's age
     *
     * @param person
     */
    public void updateUser(Users person)
    {
        ContentValues cv = new ContentValues();
        cv.put("nickName", person.getNickName());
        db.update(DatabaseHelper.TABLE_NAME_User, cv, "nickName = ?",
                new String[] { person.getNickName() });
    }



    public void deleteConnacts(ArrayList<RConact> rConacts){
        for (RConact rConact : rConacts) {
            db.delete(DatabaseHelper.TABLE_NAME_RConact,"username = ?",new String []{rConact.getUsername()}) ;
        }
    }

    public void deleteLabels(ArrayList<Label> labels){
        for (Label label : labels) {
            db.delete(DatabaseHelper.TABLE_NAME_RConact,"labelid = ?",new String []{label.getLabelID()}) ;
        }
    }







    /**
     * delete old person
     *
     * @param person
     */
    public void deleteOldPerson(Users person)
    {
        db.delete(DatabaseHelper.TABLE_NAME_User, "uin == ?",
                new String[] { String.valueOf(person.getUin()) });
    }

    public List<Label> queryLabels (){

        ArrayList<Label> lables =new ArrayList<>() ;
        Cursor c = queryTheCursor(DatabaseHelper.TABLE_NAME_LABEL);
        while (c.moveToNext())
        {
            Label lable =new Label() ;
            lable.setLabelID(c.getString(c.getColumnIndex("labelid")));
            lable.setLabelName(c.getString(c.getColumnIndex("labelname")));
            lables.add(lable);
        }
        c.close();
        return lables;
    }



    public List<RConact> quryForRconacts(){
        ArrayList<RConact> rConacts =new ArrayList<RConact>() ;

        Cursor c = queryTheCursor(DatabaseHelper.TABLE_NAME_RConact);
        while (c.moveToNext())
        {
            RConact rConact = new RConact();
            rConact.setUsername(c.getString(c.getColumnIndex("talker")));

            rConact.setType(c.getString(c.getColumnIndex("type")));

            rConact.setTalker(c.getString(c.getColumnIndex("talker")));

            if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("contactLabelIds")))){
            rConact.setContactLabelIds(c.getString(c.getColumnIndex("contactLabelIds")));
            }

            if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("alias")))){
            rConact.setAlias(c.getString(c.getColumnIndex("alias")));}
            else{
                rConact.setAlias(c.getString(c.getColumnIndex("talker")));
            }
            rConact.setNickname(c.getString(c.getColumnIndex("nikcname")));
            rConacts.add(rConact) ;
        }
        c.close();
        return rConacts;
    }
    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public  List<Users> query()
    {
        ArrayList<Users> persons = new ArrayList<Users>();
        Cursor c = queryTheCursor(DatabaseHelper.TABLE_NAME_User);
        while (c.moveToNext())
        {
            Users person = new Users();
            person.id = c.getInt(c.getColumnIndex("id"));
            person.nickName = c.getString(c.getColumnIndex("nickName"));
            person.face =c.getString(c.getColumnIndex("face")) ;
            person.uin =c.getString(c.getColumnIndex("uin")) ;
            person.dbPath =c.getString(c.getColumnIndex("dbPath")) ;
            person.password =c.getString(c.getColumnIndex("password")) ;
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor(String tableName)
    {
      //  Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + tableName,
                null);
        return c;
    }
    /**
     * close database
     */
    public void closeDB()
    {
        // 释放数据库资源
        db.close();
    }
}
