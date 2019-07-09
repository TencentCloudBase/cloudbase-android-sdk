package com.tencent.tcb.demo.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.auth.WeixinAuth;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private WeixinAuth weixinAuth = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config config = new Config();
        weixinAuth = new WeixinAuth(this, config);
        IWXAPI wxAPI = weixinAuth.getInstance().getWxAPI();

        //如果没回调onResp，八成是这句没有写
        wxAPI.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    // app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Log.e("AUTH FAILED", "");
                break;
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) resp).code;
                if (code != null) {
                    // 拿到了微信返回的code,立马再去请求access_token
                    Log.d("OK", "get openid");
                    weixinAuth.callback(code);
                }
            default:
                Log.e("Auth failed", "");
        }
    }
}
