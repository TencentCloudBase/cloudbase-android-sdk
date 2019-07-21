package com.tencent.tcb.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.database.Command;
import com.tencent.tcb.database.Db;
import com.tencent.tcb.storage.StorageService;
import com.tencent.tcb.auth.LoginListener;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    WeixinAuth weixinAuth = null;

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

        Button button1 = (Button) findViewById(R.id.cloud_function_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CloudFunctionActivity.class));
            }
        });

        Button button2 = (Button) findViewById(R.id.cloud_storage_button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CloudStorageActivity.class));
            }
        });

        Button button3 = (Button) findViewById(R.id.cloud_database_button);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CloudDatabaseActivity.class));
            }
        });

        final Context context = this;
        final Config config = Constants.config();
        weixinAuth = WeixinAuth.getInstance(this, config);
    }

    // 拉起微信登录
    private void weixinLogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String LogTag = "WeixinLogin";

                /**
                 * 如果应用已经实现了微信登录
                 * 这里建议使用weixinAuth.loginWithCode 接口
                 */
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

}
