package com.pcjh.assistant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

//�����������㲥����
public class AutoStartService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void onCreate(){
       super.onCreate();
        Timer timer =new Timer() ;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
          Log.i("leilei","ok");
            }
        },0,5000);
 }
}
