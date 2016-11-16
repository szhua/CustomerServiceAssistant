package com.pcjh.assistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;


import com.mengma.asynchttp.JsonUtil;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.LabelConact;
import com.pcjh.assistant.entity.Matrial;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.Tag;
import com.pcjh.assistant.entity.Users;

import java.io.IOException;
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

    public void addLabelConact(List<LabelConact> labelConacts){
        db.beginTransaction();

        try
        {
            for (LabelConact labelConact : labelConacts)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_LABELCONACT +"('labelid','labelname','username' ,'alias')"
                        + " VALUES(?, ? ,? , ? )", new Object[]{labelConact.getLabel().getLabelID(),
                        labelConact.getLabel().getLabelName(), labelConact.getRconact().getTalker() ,labelConact.getRconact().getAlias()});
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


    public void addCollectMatrial(Matrial matrial ,String createtime){
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_MATARIAL_COLLECT +"( 'createtime' , 'material_id','json' )"
                    + " VALUES(? , ? , ? )", new Object[]{createtime, matrial.getId(), JsonUtil.pojo2json(matrial)});
            db.setTransactionSuccessful(); // 设置事务成功完成
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("szhua",e.toString());
        }finally {
            db.endTransaction();
        }
    }

    public void addUpdateTime(String uin ,String updatetime){
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_UPDATE_TIME +"( 'lastCreateTime' , 'uin' )"
                    + " VALUES(? , ? )", new Object[]{updatetime, uin});
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction();
        }
    }

    public void addTags(List<Tag> tags){
        db.beginTransaction();
        try
        {
            for (Tag tag : tags)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_TAG +"( 'type' , 'name' )"
                        + " VALUES(? , ?)", new Object[]{tag.getType(), tag.getName()});
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

    /**
     * 更新用户的上传聊天记录的时间 ;
     * @param uin
     * @param updatetime
     */
    public void updateUpdateTime(String uin ,String updatetime){
        ContentValues cv =new ContentValues() ;
        cv.put("lastCreateTime",updatetime);
        db.update(DatabaseHelper.TABLE_NAME_UPDATE_TIME,cv,"uin = ?",new String[]{uin});
    }


    public String getUpdateTime(String uin){
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME_UPDATE_TIME +" where uin = "+uin ,
                null);
        String updateTime ="" ;
        while (c.moveToNext())
        {
         updateTime =c.getString(c.getColumnIndex("lastCreateTime"));
        }
        c.close();
        return  updateTime ;
    }

    public void deleteConnacts(ArrayList<RConact> rConacts){
        for (RConact rConact : rConacts) {
            db.delete(DatabaseHelper.TABLE_NAME_RConact,"username = ?",new String []{rConact.getUsername()}) ;
        }
    }

    public void deleteTags (ArrayList<Tag> tags){
        for (Tag tag : tags) {
            db.delete(DatabaseHelper.TABLE_NAME_TAG,"name = ?" ,new String[]{tag.getName()});
        }
    }

    public void deleteLabelConacts(ArrayList<LabelConact> labelConacts){

        for (LabelConact labelConact : labelConacts) {
            db.delete(DatabaseHelper.TABLE_NAME_LABELCONACT,"username = ? and labelid = ? ",new String []{labelConact.getRconact().getUsername(),labelConact.getLabel().getLabelID()}) ;
        }
    }

    public void deleteMatrial(Matrial matrial){
        db.delete(DatabaseHelper.TABLE_NAME_MATARIAL_COLLECT,"material_id = ?" ,new String[]{matrial.getId()});
    }

    public List<Matrial> queryCollectMatrials(String date){

        ArrayList<Matrial> matrials =new ArrayList<>() ;
        String sql ="SELECT * FROM " + DatabaseHelper.TABLE_NAME_MATARIAL_COLLECT +" where createtime = '"+date+"'" ;
        Cursor c = db.rawQuery(sql,
                null);
        while (c.moveToNext())
        {
            Log.i("szhua","cc");
            Matrial matrial =null;
            String json =c.getString(c.getColumnIndex("json")) ;
            try {
             matrial =JsonUtil.json2pojo(json,Matrial.class) ;
            } catch (IOException e){
                e.printStackTrace();
            }
            if (matrial!=null) {
                matrials.add(matrial);
            }
        }
        c.close();
        return  matrials ;
    }

    public List<Matrial> queryCollectMatrials(){
        ArrayList<Matrial> matrials =new ArrayList<>() ;
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME_MATARIAL_COLLECT  ,
                null);
        while (c.moveToNext())
        {
            Matrial matrial1 =null;
            String json =c.getString(c.getColumnIndex("json")) ;
            String createtime =c.getString(c.getColumnIndex("createtime"));
            Log.i("szhua",createtime);
            try {
            matrial1 =JsonUtil.json2pojo(json,Matrial.class) ;
            } catch (IOException e){
                e.printStackTrace();
            }
            if (matrial1!=null) {
                matrials.add(matrial1);
            }
        }
        c.close();
        return  matrials ;
    }

    public boolean checkIsCollect(String id){
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME_MATARIAL_COLLECT +" where material_id = "+ id,
                null);
        ArrayList<Matrial> matrials =new ArrayList<>() ;
        while (c.moveToNext())
        {
            Matrial matrial = null;
            String  json =c.getString(c.getColumnIndex("json")) ;
            try {
            matrial=   JsonUtil.json2pojo(json,Matrial.class) ;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (matrial!=null) {
                matrials.add(matrial);
            }
        }
        c.close();
        if(matrials.size()>0){
            return  true ;
        }
        return  false ;
    }




    public void deleteLabels(ArrayList<Label> labels){
        for (Label label : labels) {
            db.delete(DatabaseHelper.TABLE_NAME_LABEL,"labelid = ?",new String []{label.getLabelID()});
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


    public List<Tag> queryTag(){

        ArrayList<Tag> tags =new ArrayList<Tag>() ;
        Cursor c = queryTheCursor(DatabaseHelper.TABLE_NAME_TAG);
        while (c.moveToNext())
        {
            Tag tag =new Tag() ;
            String name =c.getString(c.getColumnIndex("name")) ;
            String type =c.getString(c.getColumnIndex("type")) ;
            tag.setName(name);
            tag.setType(type);
            tags.add(tag) ;
        }
        c.close();
        return tags;
    }


    public List<LabelConact> queryLabelConact(){

        ArrayList<LabelConact> labelConacts =new ArrayList<>() ;
        Cursor c = queryTheCursor(DatabaseHelper.TABLE_NAME_LABELCONACT);
        while (c.moveToNext())
        {
            LabelConact lableCon =new LabelConact() ;
            Label label =new Label() ;
            RConact rConact =new RConact() ;
            label.setLabelID(c.getString(c.getColumnIndex("labelid")));
            label.setLabelName(c.getString(c.getColumnIndex("labelname")));
            rConact.setUsername(c.getString(c.getColumnIndex("username")));
            if(TextUtils.isEmpty(c.getString(c.getColumnIndex("alias")))){
            rConact.setAlias(c.getString(c.getColumnIndex("username")));
            }else{
            rConact.setAlias(c.getString(c.getColumnIndex("alias")));}
            lableCon.setLabel(label);
            lableCon.setRconact(rConact);
            labelConacts.add(lableCon);
        }
        c.close();
        return labelConacts;
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
