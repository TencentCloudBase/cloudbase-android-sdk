package com.tencent.tcb.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.auth.LoginListener;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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
        String sign = getCertificateSHA1Fingerprint(context);
        Log.d("签名", sign);
    }

    private String getCertificateSHA1Fingerprint(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
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
