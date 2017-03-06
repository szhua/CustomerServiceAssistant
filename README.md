#微信数据的获取

从微信中获取聊天记录，是一个不错的想法。然后将微信聊天记录不间断的上传，需要一个合理的逻辑去处理这件事情。经过一个多月的摸索，现在已经能够达到基本的需求，失误率很低。不合理的地方还有很多，以后再进行修改。自从这个项目开始到现在gitHub上面已经更新了好多的版本，不断的进行修改。

![基本框架结构](https://github.com/szhua/CustomerServiceAssistant/blob/master/解密微信.png)

这个是一个简单的解密微信数据库的流程，网上的资料很多，但是demo却很少。我是使用的sqlChiper进行的解密。
https://github.com/sqlcipher/android-database-sqlcipher;

流程中具体的步骤：

1.  获得手机的IMEI只需获得手机权限就能够轻松的获得。
2.  这个步骤网上的资料很少，通过观察微信的文件结构获得的。文件路径为："/data/data/com.tencent.mm/shared_prefs/system_config_prefs.xml" 是一个xml文件，当前登录的微信uin的key为default_uin ,通过android中的XmlParser类就能够进行解析，若是当前没有登录微信账号的话，default_uin对应的值为0 。
3.  IME+uin的意思是将两个字符串进行拼接，然后进行MD5加密，获得加密后字符串的前7位，得到当前登录微信号的enMicroMsg.db的密码(当前登录微信enMicroMsg.db密码)。
4.  这个步骤不是很容易，网上也没有资料：下面是微信的聊天记录文件目录。
![文件目录](https://github.com/szhua/CustomerServiceAssistant/blob/master/20141202102707406.png)

这个图是从网上copy下来的，实际中的文件目录和这个大致相同，不同的是会有好几个和划红部分相同的文件夹，这个文件夹里面不知道哪个有我们需要的enMicroMsg.db文件，即使有enMicroMsg.db文件
也不一定是我们需要的那个数据库文件。考虑到一个机器不会有很多的微信号，本人愚笨使用了下面的方式进行解析。
下面是解密步骤：
![](https://github.com/szhua/CustomerServiceAssistant/blob/master/解密.png)
MIcroMsg文件夹的路径是固定的，直接进行拼接就可以了。然后我们通过遍历得到符合MD5的文件夹，这样的文件夹下可能会有仅存在一个enMicriMsg,db文件，遍历以后一般的手机不会大于5个微信号，
这也使我们的工作变得好做了很多，上面的方法的效率也就不会很低。解密后，我们将数据库的路径，密码，和uin信息一一对应的存储在数据库中，下次再进行解密的时候就直接找到此文件就行了。
<br/>
至此，我们就将数据库——enMicroMsg.db文件拿出来，（记住这个enMicroMsg是当前登录用户的数据库文件），并且进行了解密；

<strong>下面是解密的整体步骤：<strong/>
![整体步骤](https://github.com/szhua/CustomerServiceAssistant/blob/master/使用sqlChiper解密enMicrMsg.db.png)

贴出使用sqlChiper解密enMicroMsg的步骤代码：

```java

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


```

<strong> 以上简单的介绍一下怎样的解密微信的数据库，在手机root的情况下，使用怎样的方式去获得。需要提及的是，android的微信数据库加密使用的就是sqlChiper的开源库所以我们才得以顺利的破解人家数据库。若是微信版本变更，使用别的加密方式我们就难以下手了~
<strong/>
<hr/>
<hr/>
<strong  >关于本例中的进程保活手段
 微信数据的上传需要一个常驻的后台任务，这个任务的优先程度，存活率保证了数据上传的稳定性，下面是为了
保活实行的一些手段。<strong/>


> IPC机制：AIDL让进程之间保持长连接

 ```java
   /*关于AIDL机制，请参考一些文档
   注：主进程与守护进程都要进行返回Binder
   */
   @Override
    public IBinder onBind(Intent intent) {
        return customBinder;
    }
  private class CustomBinder extends  CustomAidlInterface.Stub{
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
        double aDouble, String aString) throws RemoteException {
        }
    }
 ```
>使用bindService监听服务之间的链接。

```java
 /*服务之间的绑定：bindService*/
 WorkService.this.bindService(new Intent(WorkService.this,WatchDogService.class),customConnection, Context.BIND_IMPORTANT);

 /*监听服务之间的链接情况*/
 private class CustomConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            WorkService.this.startService(new Intent(WorkService.this,WatchDogService.class));
            WorkService.this.bindService(new Intent(WorkService.this,WatchDogService.class),customConnection,Context.BIND_IMPORTANT) ;
        }
    }
```

>启动前台服务，提高进程的优先级。

```java
   /*这样会在启动服务的时候多一个NOtification,但是无伤大雅，能够提高进程的优先级*/
   
   Notification.Builder builder =new Notification.Builder(this);
        PendingIntent pendingIntent =PendingIntent.getService(this,0,intent,0);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker("启动服务中")
                .setAutoCancel(false)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("客服助手");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            builder.setContentInfo("客服助手");
        }
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){
            startForeground(startId,builder.build());
        }else{
            startForeground(startId,builder.getNotification());
        }
```
> onStartCommand中返回数值，保证服务的存活率。

 ```java
   return START_STICKY;
```

> android5.0以上的使用JobScheduler,5.0以下的使用AlarmManager，定时任务定时唤醒

 ```java
 /**
 * Android 5.0+ 使用的 JobScheduler.
 * 运行在 :watch 子进程中.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
...
}
/*使用*/
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobInfo.Builder builder = new JobInfo.Builder(sHashCode, new ComponentName(getApplication(), JobSchedulerService.class));
            builder.setPeriodic(INTERVAL_WAKE_UP);
            //Android 7.0+ 增加了一项针对 JobScheduler 的新限制，最小间隔只能是下面设定的数字
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) builder.setPeriodic(JobInfo.getMinPeriodMillis(), JobInfo.getMinFlexMillis());
            builder.setPersisted(true);
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(builder.build());
        } 
        
        
          //Android 4.4- 使用 AlarmManager
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent i = new Intent(getApplication(), WorkService.class);
            PendingIntent pi = PendingIntent.getService(getApplication(), sHashCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_WAKE_UP, INTERVAL_WAKE_UP, pi);
```
> 使用RxJava中的订阅机制，防止任务的重复启动，监测服务的存活。

 ```java
   if (sSubscription != null && !sSubscription.isUnsubscribed()&&sSubscription2!=null&&!sSubscription2.isUnsubscribed()) return START_STICKY;

        sSubscription = Observable
                .interval(5, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted(){
                    }
                    @Override
                    public void onError(Throwable e){
                    }
                    @Override
                    public void onNext(Long count) {
                        Log.i("testIsRunning","isRunning!"+Thread.currentThread().getName());
                    }
                });



        sSubscription2 =  Observable
                .interval(60, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted(){
                    }
                    @Override
                    public void onError(Throwable e){
                    }
                    @Override
                    public void onNext(Long count) {
                        Log.i("testIsRunning","isRunning2!"+Thread.currentThread().getName());
                        /*之所以使用handler来执行任务，这里是保证此Subscription不被销毁，持续的进行周期性的上传*/
                        workHandler.sendEmptyMessage(0) ;
                    }
                });

```
>简单守护开机广播

   ```java
     //若需要防止 CPU 休眠，这里给出了 WakeLock 的参考实现
         PowerManager pom = (PowerManager) getSystemService(POWER_SERVICE);
        sWakeLock = pom.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WatchDogService.class.getSimpleName());
        sWakeLock.setReferenceCounted(false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        try {
            getApplication().registerReceiver(WakeLockReceiver.getInstance(), intentFilter);
        } catch (Exception ignored) {
        }
```








