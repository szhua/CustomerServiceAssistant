package com.pcjh.assistant.db;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.pcjh.assistant.entity.RConact;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import java.util.HashMap;

/**
 * Created by szhua on 2016/11/19.
 */
public class DBCipherManager {
    private static final String TAG = "DatabaseManager";
    // 静态引用
    private volatile static DBCipherManager mInstance;
    // DatabaseHelper
    private DBCipherHelper dbHelper;

    private DBCipherManager(Context context) {
        dbHelper = new DBCipherHelper(context.getApplicationContext());

    }
    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBCipherManager getInstance(Context context) {
        DBCipherManager inst = mInstance;
        if (inst == null) {
            synchronized (DBCipherManager.class) {
                inst = mInstance;
                if (inst == null) {
                    //这进行初始化，初始化和地址引用的时间是不确定的，可能初始化没完成，但是有了引用， 这就会不为空===错误就会出现 ；
                    inst = new DBCipherManager(context);
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 测试开启事务批量插入
     */
    public void insertDatasByTransaction(HashMap<String,RConact> rConactHashMap){
       SQLiteDatabase db =dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD) ;

        db.beginTransaction();  //手动设置开始事务
        try{
            for(String key :rConactHashMap.keySet()){
                RConact rConact =rConactHashMap.get(key);
                //生成要修改或者插入的键值
                ContentValues cv = new ContentValues();
                cv.put("username",rConact.getUsername());
                cv.put("alias",rConact.getAlias());
                if(!TextUtils.isEmpty(rConact.getNickname()))
                cv.put("nikcname",rConact.getNickname());
                cv.put("type",rConact.getType());
                if(!TextUtils.isEmpty(rConact.getContactLabelIds()))
                cv.put("contactLabelIds",rConact.getContactLabelIds());
                if(!TextUtils.isEmpty(rConact.getConRemark())){
                    cv.put("conRemark",rConact.getConRemark());
                }else{
                    cv.put("conRemark","");
                }


                // insert 操作
                db.insert(DBCipherHelper.TABLE_NAME_RConact, null, cv);
                Log.e(TAG, "insertDatasByTransaction");
            }
            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
        }catch(Exception e){

        }finally{
            db.endTransaction(); //处理完成
            //关闭数据库
            db.close();
        }
    }

    /**
     * 更新数据库中的数据 ；
     * @param uin
     * @param msgId
     */
    public void updateMsgId(String uin ,String msgId){
        SQLiteDatabase db =dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD) ;
        db.beginTransaction();
       try{
           ContentValues cv = new ContentValues();
           cv.put("uin",uin);
           cv.put("msgId",msgId);
           db.insertWithOnConflict(DBCipherHelper.TABLE_NAME_MSG_ID, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
       }catch (Exception e){
        Log.i("szhua",e.toString()) ;
       }finally {
           db.setTransactionSuccessful();
           db.endTransaction();
       }
    }

    public  void updateMsgIdPreSend(String uin ,String msgId){
        SQLiteDatabase db =dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD) ;
        db.beginTransaction();
        try{
            ContentValues cv = new ContentValues();
            cv.put("uin",uin);
            cv.put("msgId",msgId);
            db.insertWithOnConflict(DBCipherHelper.TABLE_NAME_MSG_ID_PRE_SEND, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }catch (Exception e){
            Log.i("szhua",e.toString()) ;
        }finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

  public String getMsgIdPreSend(String uin){
      SQLiteDatabase db =dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD) ;
      Cursor c =  db.rawQuery("SELECT * FROM " + DBCipherHelper.TABLE_NAME_MSG_ID_PRE_SEND +" where uin = "+"'"+uin+"'",
              null);
      String msgId  ="" ;
      while (c.moveToNext())
      {
          msgId= c.getString(c.getColumnIndex("msgId"));
      }
      c.close();
      db.close();
      return  msgId  ;
  }

    public String getMsgId (String uin){
        SQLiteDatabase db =dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD) ;
        Cursor c =  db.rawQuery("SELECT * FROM " + DBCipherHelper.TABLE_NAME_MSG_ID +" where uin = "+"'"+uin+"'",
                null);
        String msgId  ="" ;
        while (c.moveToNext())
        {
            msgId= c.getString(c.getColumnIndex("msgId"));
        }
        c.close();
        db.close();
        return  msgId  ;
    }



    /**
     * 更新数据
     */
    public void updateContacts(HashMap<String,RConact> rConactHashMap) {
        //生成要修改或者插入的键值
        SQLiteDatabase db =dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD) ;
        db.beginTransaction();
        for (String key :rConactHashMap.keySet()){
          RConact rConact =rConactHashMap.get(key) ;
            ContentValues cv = new ContentValues();
            cv.put("username",rConact.getUsername());
            cv.put("alias",rConact.getAlias());
            if(!TextUtils.isEmpty(rConact.getNickname()))
                cv.put("nikcname",rConact.getNickname());
            cv.put("type",rConact.getType());
            if(!TextUtils.isEmpty(rConact.getContactLabelIds()))
                cv.put("contactLabelIds",rConact.getContactLabelIds());
            if(!TextUtils.isEmpty(rConact.getConRemark())){
                cv.put("conRemark",rConact.getConRemark());
            }else{
                cv.put("conRemark","");
            }
            // 生成的sql是 INSERT INTRO OR REPLACE INTO 这样的 (如果存在就替换存在的字段值. 存在的判断标准是主键冲突, 这里的主键是username). 下面会介绍这个地方的方法
            db.insertWithOnConflict(DBCipherHelper.TABLE_NAME_RConact, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    /**
     * 本地数据库获得数据 ;
     * @return
     */
    public HashMap<String ,RConact> quryForRconacts(){

        HashMap<String,RConact> rConacts =new HashMap<String,RConact>();
        //获取写数据库
        SQLiteDatabase db =dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD) ;
        Cursor c =  db.rawQuery("SELECT * FROM " + DBCipherHelper.TABLE_NAME_RConact,
                null);
        while (c.moveToNext())
        {
            RConact rConact = new RConact();
            rConact.setUsername(c.getString(c.getColumnIndex("username")));
            rConact.setType(c.getString(c.getColumnIndex("type")));
            if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("contactLabelIds")))){
                rConact.setContactLabelIds(c.getString(c.getColumnIndex("contactLabelIds")));
            }else{
                rConact.setContactLabelIds("");
            }
            if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("alias")))){
                rConact.setAlias(c.getString(c.getColumnIndex("alias")));}
            else{
                rConact.setAlias(c.getString(c.getColumnIndex("username")));
            }

            if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("conRemark"))))
            rConact.setConRemark(c.getString(c.getColumnIndex("conRemark")));

            rConact.setNickname(c.getString(c.getColumnIndex("nikcname")));
            rConacts.put(rConact.getUsername(),rConact) ;
        }
        c.close();
        db.close();
        return rConacts;
    }


}
