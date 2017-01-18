package com.pcjh.assistant.util;

import android.content.Context;
import android.util.Log;

import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by szhua on 2016/11/26.
 * 从微信中获取用户信息的工具类 ；
 */
public class InitializeWx {
    /**
     * 单例模式；
     */
     private InitializeWx (){} ;
     private static InitializeWx myInstance =new InitializeWx() ;
     public static InitializeWx getInstance(){
         return  myInstance ;
     }

    //已知密码和文件路径的情况下；

    /**
     *
     * @param users    数据库中获得的用户
     * @param context
     * @param uin
     * @param dbManager
     * @return
     */
    public UserInfo readDatabaseFromOldInfo(Users users, Context context ,String uin ,DbManager dbManager) {
        Log.i("szhua","ioPas"+users.getPassword());
        SQLiteDatabase.loadLibs(context);
        File testFile = new File("/data/data/com.tencent.mm/MicroMsg/");
        UserInfo userinfo = null;
        if (!testFile.canRead()){
            ChmodUtil.setFileCanRead(testFile);
        }
        if (users != null) {
            File dbFile = new File(users.getDbPath());
            if (!dbFile.canRead()) {
                ChmodUtil.setFileCanRead(dbFile);
            }
            userinfo = getDataWithSqlcipher(dbFile, users.getPassword(), true ,uin ,dbManager);
        }
        return userinfo;
    }


    /**
     * 使用Sqlcipher获得解密数据库并且获得数据;
     * @param dbFile
     * @param pass
     * @param isFromOld
     * @param uin
     * @param dbManager
     * @return
     */
    public  UserInfo getDataWithSqlcipher(File dbFile, String pass, boolean isFromOld , String uin , DbManager dbManager) {
        //获得最终的db文件并进行读取其中的数据；
        UserInfo userInfo = null;

            SQLiteDatabaseHook hook = new SQLiteDatabaseHook()  {
                public void preKey(SQLiteDatabase database) {
                }
                public void postKey(SQLiteDatabase database) {
                    //执行这样的sql语句进行对数据库的解密；
                    database.rawExecSQL("PRAGMA cipher_migrate;");
                }
            };


            try {

                /**
                 * 从数据库中获得个人信息；
                 */
                UserInfo userinfo =readUserInfoFromWx(dbFile,pass,hook);
                /**
                 * 若是数据中没有数据的话;
                 */
                if (!isFromOld) {
                      setNewUsersInDb(dbFile.getAbsolutePath(),pass,uin,dbManager);
                }

                return userinfo;
            } catch (Exception e) {
                Log.e("szhua", "there is what erro is happened in sqlCipher : "+e.toString());
                return null;
            }

    }


    /**
     * 从微信数据库中获得个人信息;
     * @param dbFile
     * @param pass
     * @param hook
     * @return
     */
     private  UserInfo  readUserInfoFromWx(File dbFile ,String pass ,SQLiteDatabaseHook hook){
         //以这样的方式去读取数据库中的文件，确保文件的完整性：
         //@WeChat ====》微信客户端户对本地的数据库进行判断，发现文件被破坏的话就会执行重新登录操作，并且会对文件中的数据进行清除：
         //这样的体验对用户来说肯定是不行的。 故放弃官方的打开方式 使用下面的方法。
         SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), pass, null, SQLiteDatabase.OPEN_READWRITE, hook);
         Cursor c = db.query("userinfo", null, null, null, null, null, null);
         UserInfo userinfo = new UserInfo();
         while (c.moveToNext()) {
             String id = c.getString(c.getColumnIndex("id"));
             String value = c.getString(c.getColumnIndex("value"));
             if (id.equals("12325")) {
                 userinfo.setProvince(value);
             } else if (id.equals("12326")) {
                 userinfo.setCity(value);
             } else if (id.equals("4")) {
                 userinfo.setNickName(value);
             } else if (id.equals("12293")) {
                 userinfo.setProvinceCn(value);
             } else if (id.equals("12292")) {
                 userinfo.setCityCn(value);
             } else if (id.equals("2")) {
                 userinfo.setWxId(value);
             } else if (id.equals("6")) {
                 userinfo.setPhone(value);
             } else if (id.equals("42")) {
                 userinfo.setWxNumber(value);
             }
         }
         c.close();
         db.close();
         return  userinfo ;
     }


    /**
     * 向数据库中添加微信用户信息  ;and AppHolder ；
     * @param dbPaht 用户微信数据库的路径 ；
     * @param password 用户微信数据库的password ;
     * @param uin      用户的微信uin ；
     * @param  dbManager 用于存储的数据库对象 ；
     */
    public void setNewUsersInDb(String dbPaht ,String password ,String uin ,DbManager dbManager){
        Users users = new Users();
        users.setDbPath(dbPaht);
        users.setPassword(password);
        users.setUin(uin);

        dbManager.addWxUserInfoToDb(users);


        AppHolder.getInstance().setUsers(users);

    }


    /**
     * 将MicroMsg 文件夹下的子目录过滤出‘cf792f218920a5d21aa5c121bcbe7f65’形式的文件 ;
     */
    class  WxFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().length()== "cf792f218920a5d21aa5c121bcbe7f65".length();
        }
    }
    class  DbFileFilter implements  FileFilter{
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().equals("EnMicroMsg.db");
        }
    }


    //从微信文件中读取用户的信息 ；(FirstToGetDataFromWx for current wxNumber) ；
    /**
     *
     * @param password
     * @param context
     * @param uin
     * @param dbManager
     * @return
     */
    public UserInfo readWeChatDatabase(String password ,Context context ,String uin , DbManager  dbManager) {
        //此处用于读取 当前微信号的uin
        SQLiteDatabase.loadLibs(context);
        File microMsgFile = new File("/data/data/com.tencent.mm/MicroMsg/");


        /**
         * 若是不能够读取的话，设置他为可读取 ;
         */
        if (!microMsgFile.canRead()) {
            ChmodUtil.setFileCanRead(microMsgFile);
        }


        /**
         *符合微信EnMicrloMsg.db的文件格式的文件；
         */
        ArrayList<File> dbDatas = new ArrayList<File>();


        //得到‘cf792f218920a5d21aa5c121bcbe7f65’形式的文件夹 ；
        for (File md5File :microMsgFile.listFiles(new WxFileFilter())) {
            //获得指定的文件夹下的目录；
            if(!md5File.canRead()){
                ChmodUtil.setFileCanRead(md5File);
            }
            for (File enMicroMsgDb :md5File.listFiles(new DbFileFilter())) {

                //获得下一层的数据 ；
                if(!enMicroMsgDb.canRead()) {ChmodUtil.setFileCanRead(enMicroMsgDb);}

                //添加到集合中 ；
                dbDatas.add(enMicroMsgDb);
            }
        }
        UserInfo userInfo = null;



        /**
         * 解密数据库文件得到信息 ；
         */
        if (dbDatas.isEmpty()) {return null;} else {

            /**
             * 若不为空的情况下跳出整个循环 ；
             */
            for (File enMicroMsgDb :dbDatas ) {
                 userInfo = getDataWithSqlcipher(enMicroMsgDb, password, false,uin,dbManager);
                if (userInfo != null) {
                    break;
                }
            }
        }
        return userInfo;
    }


    /**
     * 检验当前微信账号的数据是否存在；
     *
     * @param data
     * @param  uin
     * @return users
     */
    public Users checkUserExisted(ArrayList<Users> data ,String uin) {
            for (Users users : data) {
                if(users.getUin().equals(uin)){
                 return  users ;
                }
            }
        return  null ;

    }










}
