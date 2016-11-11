package com.pcjh.assistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

//�����������㲥����
public class AutoStartBroadcastReceiver extends BroadcastReceiver {
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	private SharedPreferences mPreferences = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		mPreferences = context.getSharedPreferences("AutoStart",
				ContextWrapper.MODE_PRIVATE);

		Log.i("Leilei","okok");

		if (intent.getAction().equals(ACTION)) {
			
			if (mPreferences.getBoolean("AddToAuto", false)) {
				
			    //��ߵ�XXX.class����Ҫ�����ķ���  
		        Intent service = new Intent(context,AutoStartService.class);  
		        context.startService(service);  
				// ����Ӧ�ã�����Ϊ��Ҫ�Զ�������Ӧ�õİ�����ֻ������app��activity�İ���
				Intent newIntent =new Intent(context,Service1.class) ;
				context.startService(newIntent);
			}
		}
	}

}
