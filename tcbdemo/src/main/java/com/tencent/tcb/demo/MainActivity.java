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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        this.invokeFunctionTest(this);
//        this.mockLogin();
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
                weixinAuth.callback("081a7rdi1Lwsev0BPHfi1zJDdi1a7rdg");
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
                } catch (JSONException e) {
                    Log.e(LogTag, e.toString());
                } catch (TcbException e) {
                    Log.e(LogTag, e.toString());
                }
            }
        }).start();
    }

    private void downLoadFileTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Config config = new Config();
                String data = "Data to write";
                FileOutputStream out = null;
                BufferedWriter writer = null;
                try {
                    StorageService storage = new StorageService(config, context);
                    String root = getApplicationInfo().dataDir;
                    out = openFileOutput("data.txt", Context.MODE_PRIVATE);
                    writer = new BufferedWriter(new OutputStreamWriter(out));
                    writer.write(data);
                    writer.close();

                    storage.downloadFile("cloud://6465-dev-97eb6c/ddia-cn.pdf", root + "/files/Redis.pdf",
                            new StorageService.OnDownloadListener() {
                                @Override
                                public void onDownloadSuccess() {
                                    Log.d("Ok", "Download success");
                                }

                                @Override
                                public void onProgress(int progress) {
                                    Log.d("Download", String.valueOf(progress));
                                }

                                @Override
                                public void onDownloadFailed(IOException e, TcbException err) {
                                    if (e != null) {
                                        Log.e("failed", e.toString());
                                    }

                                    if (err != null) {
                                        Log.e("failed", err.toString());
                                    }
                                }
                            });
                } catch (IOException e) {
                    Log.e("IOException", e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
