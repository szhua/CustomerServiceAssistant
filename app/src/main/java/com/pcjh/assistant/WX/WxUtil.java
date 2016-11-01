package com.pcjh.assistant.WX;

import android.content.Context;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by 单志华 on 2016/10/31.
 */
public class WxUtil {
    private static final String APP_ID ="wxa7de31ea0a20f8e6" ;
    private static IWXAPI iwxapi ;
    public static void regWX(Context context) {
        iwxapi = WXAPIFactory.createWXAPI(context,APP_ID,true) ;
        iwxapi.registerApp(APP_ID) ;
    }


    public static void shareToWxCircle(String text){
        WXTextObject wxTextObject =new WXTextObject() ;
        wxTextObject.text =text ;


        WXMediaMessage msg =new WXMediaMessage() ;
        msg.mediaObject =wxTextObject ;
        msg.description =text ;
        msg.title="" ;

        SendMessageToWX.Req req=new SendMessageToWX.Req() ;
        req.transaction =buildTransaction("text") ;
        req.message =msg ;

        req.scene = SendMessageToWX.Req.WXSceneTimeline ;
        // 调用api接口发送数据到微信
        iwxapi.sendReq(req);

    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }



}
