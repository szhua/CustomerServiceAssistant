package com.pcjh.assistant.wxapi;

import android.app.Activity;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by szhua on 2016/10/31.
 */
public class WXEntryActivity extends Activity  implements IWXAPIEventHandler {
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(this, "发送成功", Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(this, "分享取消", Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this, "分享被拒绝", Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                Toast.makeText(this, "分享返回", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
