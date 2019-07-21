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
import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private WeixinAuth weixinAuth = null;
    public String envName = "test-a8e99b";
    // 请使用微信开放平台移动应用 appId
    // 并在云开发 Web 控制台：用户管理/登陆设置中绑定你的 AppID 和 AppSecret
    public String appId = "wx9c4c30a432a38ebc";
    public String domain = "http://jimmytest-088bef.tcb.qcloud.la";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config config = new Config(envName, appId, domain);
        weixinAuth = WeixinAuth.getInstance(this, config);
        IWXAPI wxAPI = weixinAuth.getWxAPI();

        // 处理微信回调数据
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
                weixinAuth.loginListener.onFailed(
                        new TcbException("ERR_AUTH_DENIED", "user auth denied")
                );
                break;
            case BaseResp.ErrCode.ERR_OK:
                final String code = ((SendAuth.Resp) resp).code;
                if (code != null) {
                    // 拿到了微信返回的code,立马再去请求access_token
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            weixinAuth.callback(code);
                        }
                    }).start();
                }
            default:
                weixinAuth.loginListener.onFailed(
                        new TcbException("ERR_AUTH_DENIED", "user auth denied")
                );
                Log.e("Auth failed", "");
        }

        finish();
    }
}
