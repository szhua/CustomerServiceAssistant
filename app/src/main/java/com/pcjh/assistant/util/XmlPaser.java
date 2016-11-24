package com.pcjh.assistant.util;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by szhua on 2016/10/13.
 * 此工具类用于解析system_config_prefs.xml
 */
public class XmlPaser {
    private static String  value;
    private static  final  String TAGNAME ="default_uin";
    public static String getUin(InputStream xml) throws Exception {
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(xml, "UTF-8"); //为Pull解释器设置要解析的XML数据
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT){
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if(pullParser.getAttributeCount()>0){
                    if(TAGNAME.equals(pullParser.getAttributeValue(0))){
                        if(pullParser.getAttributeCount()>1){
                           value =pullParser.getAttributeValue(1);
                        }
                    }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    pullParser.next() ;
                    break;
            }
            event = pullParser.next();
            Log.i("xmlparser","name"+pullParser.getName()) ;
        }
        return value;
    }
    public static String   getUidFromFile(){
        //tod  shared_prefs 的权限 ；
        String path ="/data/data/com.tencent.mm/shared_prefs/system_config_prefs.xml" ;
        File xmlFile =new File(path);
        ChmodUtil.setFileCanRead(xmlFile);
        String   wx =null ;
        Log.i("wxUin","fs"+xmlFile.canRead()) ;
        try {
            if(xmlFile.exists()&&xmlFile.canRead()){
                InputStream inputStream =new FileInputStream(xmlFile) ;
                wx =XmlPaser.getUin(inputStream);
              //  ChmodUtil.setFileCanNotRead(xmlFile);
                return  wx ;
            }else{
                Log.i("wxUin","notexists") ;
            }
            //  Log.i("wxUin","uin:"+uin) ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("wxUin","FileNotFoundException");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("wxUin","Exception");
        }
        return  wx ;
    }
}
