package com.tencent.tcb.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.function.FunctionService;
import com.tencent.tcb.storage.StorageService;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.auth.LoginListener;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Config config = null;
    WeixinAuth weixinAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;
        config = new Config();
        weixinAuth = new WeixinAuth(this, config);
        // this.invokeFunctionTest(this);
        // this.mockLogin();
        // this.fileTest(this);
        // this.downLoadFileTest(this);
        // this.deleteFileTest(this);
        this.uploadFileTest(this);
    }

    // 模拟登录
    private void mockLogin() {
        final String LogTag = "MockLogin";

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

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                Log.d(LogTag, "返回 Code");
                weixinAuth.callback("011mI4mD0sOXOk2scunD0Ej8mD0mI4mx");
            }
        }, 2000);
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
                } catch (JSONException e) {
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
                } catch (JSONException e) {
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

                storage.uploadFile("books/cn.pdf", root + "/files" + "/ddcn.pdf", new StorageService.FileTransportListener() {
                    @Override
                    public void onSuccess() {

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

                storage.downloadFile(
                        "cloud://dev-97eb6c.6465-dev-97eb6c/500.svg", root + "/files" + "/500.svg",
                        new StorageService.FileTransportListener() {
                            @Override
                            public void onSuccess() {
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
