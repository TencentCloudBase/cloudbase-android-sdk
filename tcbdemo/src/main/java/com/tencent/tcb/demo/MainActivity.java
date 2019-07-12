package com.tencent.tcb.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.function.FunctionService;
import com.tencent.tcb.storage.StorageService;
import com.tencent.tcb.auth.LoginListener;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Config config = null;
    WeixinAuth weixinAuth = null;
    public String envName = "dev-97eb6c";
    // 请使用微信开放平台移动应用 appId
    // 并在云开发 Web 控制台：用户管理/登陆设置中绑定你的 AppID 和 AppSecret
    public String appId = "wx9c4c30a432a38ebc";
    public String domain = "http://jimmytest-088bef.tcb.qcloud.la";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 点击按钮，调用微信登录
        Button button = (Button) findViewById(R.id.weixin_login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weixinLogin();
            }
        });

        final Context context = this;
        config = new Config(envName, appId, domain);
        weixinAuth = WeixinAuth.getInstance(this, config);
        // this.invokeFunctionTest(this);
        // this.mockLogin();
        // this.fileTest(this);
        this.downLoadFileTest(this);
        // this.deleteFileTest(this);
        // this.uploadFileTest(this);
    }

    // 拉起微信登录
    private void weixinLogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String LogTag = "WeixinLogin";
                Log.d("deng", "de");
                weixinAuth.login(new LoginListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(LogTag, "success");
                    }

                    @Override
                    public void onFailed(TcbException e) {
                        Log.e(LogTag, e.toString());
                    }
                });
            }
        }).start();
    }

    // 调用函数测试
    private void invokeFunctionTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FunctionService functionService = new FunctionService(config, context);
                String LogTag = "InvokeFunctionTest";

                try {
                    JSONObject res = functionService.callFunction("test-scf");
                    Log.d(LogTag, res.toString());
                } catch (TcbException e) {
                    Log.e(LogTag, e.toString());
                }
            }
        }).start();
    }

    private void fileTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StorageService storageService = new StorageService(config, context);
                String LogTag = "FileTest";

                try {
                    String[] fileList = {"cloud://dev-97eb6c.6465-dev-97eb6c/500.svg"};
                    JSONObject tempUrl = storageService.getTempFileURL(fileList);
                    Log.d(LogTag, tempUrl.toString());
                } catch (TcbException e) {
                    Log.e(LogTag, e.toString());
                }
            }
        }).start();
    }

    private void deleteFileTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StorageService storageService = new StorageService(config, context);
                String LogTag = "FileTest";

                try {
                    String[] fileList = {"cloud://dev-97eb6c.6465-dev-97eb6c/ddia-cn.pdf"};
                    JSONObject tempUrl = storageService.deleteFile(fileList);
                    Log.d(LogTag, tempUrl.toString());
                } catch (TcbException e) {
                    Log.e(LogTag, e.toString());
                }
            }
        }).start();
    }

    private void uploadFileTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StorageService storage = new StorageService(config, context);
                String root = getApplicationInfo().dataDir;
                final String LogTag = "UploadFileTest";

                storage.uploadFile(
                        "books/cn.pdf",
                        root + "/files" + "/ddcn.pdf",
                        new StorageService.FileTransportListener() {
                            @Override
                            public void onSuccess(JSONObject result) {

                            }

                            @Override
                            public void onProgress(int progress) {
                                Log.d(LogTag, String.valueOf(progress));
                            }

                            @Override
                            public void onFailed(TcbException e) {
                                Log.e(LogTag, e.toString());
                            }
                        });

            }
        }).start();
    }

    private void downLoadFileTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StorageService storage = new StorageService(config, context);
                String root = getApplicationInfo().dataDir;
                String LogTag = "DownFileTest";

                Log.d("run", "run");

                storage.downloadFile(
                        "cloud://dev-97eb6c.6465-dev-97eb6c/500.svg",
                        "/data/data/com.tencent.tcb.demo/files/500.svg",
                        new StorageService.FileTransportListener() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                // result 为 null
                                Log.d("Ok", "Download success");
                            }

                            @Override
                            public void onProgress(int progress) {
                                Log.d("Download", String.valueOf(progress));
                            }

                            @Override
                            public void onFailed(TcbException e) {
                                Log.e("failed", e.toString());
                            }
                        });
            }
        }).start();
    }
}
