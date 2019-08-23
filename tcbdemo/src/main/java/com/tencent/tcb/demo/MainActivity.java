package com.tencent.tcb.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.tcb.auth.CustomAuth;
import com.tencent.tcb.auth.WeixinAuth;
import com.tencent.tcb.auth.LoginListener;
import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.TcbException;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.Signature;
import java.util.Date;

import android.util.Base64;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Handler uiHandler = null;
    WeixinAuth weixinAuth = null;
    CustomAuth customAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHandler = new Handler();

        // 点击按钮，调用微信登录
        Button button = (Button) findViewById(R.id.weixin_login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weixinLogin();
            }
        });

        Button button4 = (Button) findViewById(R.id.custom_login_button);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLogin();
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

        Button button5 = (Button) findViewById(R.id.logout_button);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        final Context context = this;
        final Config config = Constants.config();
        weixinAuth = WeixinAuth.getInstance(this, config);
//        String sign = getCertificateSHA1Fingerprint(context);
//        Log.d("签名", sign);

        customAuth = new CustomAuth(this, config.envName);
    }

//    private String getCertificateSHA1Fingerprint(Context mContext) {
//        PackageManager pm = mContext.getPackageManager();
//        String packageName = mContext.getPackageName();
//        int flags = PackageManager.GET_SIGNATURES;
//        PackageInfo packageInfo = null;
//        try {
//            packageInfo = pm.getPackageInfo(packageName, flags);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        Signature[] signatures = packageInfo.signatures;
//        byte[] cert = signatures[0].toByteArray();
//        InputStream input = new ByteArrayInputStream(cert);
//        CertificateFactory cf = null;
//        try {
//            cf = CertificateFactory.getInstance("X509");
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        X509Certificate c = null;
//        try {
//            c = (X509Certificate) cf.generateCertificate(input);
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        String hexString = null;
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] publicKey = md.digest(c.getEncoded());
//            hexString = byte2HexFormatted(publicKey);
//        } catch (NoSuchAlgorithmException e1) {
//            e1.printStackTrace();
//        } catch (CertificateEncodingException e) {
//            e.printStackTrace();
//        }
//        return hexString;
//    }

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
                        Log.d("测试", "登录成功");
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailed(final TcbException e) {
                        Log.e("测试", e.toString());

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "登录失败" + e.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private String jsonWebToken() {
        String pKey = "MIICXgIBAAxxxxxxxxxf8zEVJn7OlcFayjmgTJ+1Cb7JSarjL1amLxv\n" +
                "vJ8O0p5So7+/P+LHEPxmtksr4Q2UjkV/zAqE6+kBN8qfwWg3VEu/SCf6ieuIgvbc\n" +
                "6mePFZmylrTfc0NoN8zTnnR7q/HV2FpOKIlt8xvlfnO1tFFN2eUDIxGBVwIDAQAB\n" +
                "AoGAXi6Jn1pZa86MkLJFqs6h8vjTFe+R4O197heafzp3nMJigsoyLyphVjV6ERx6\n" +
                "ID2eGgF/UYKjnJHl1KgzqjtUDZXEJPpsIUzQBpuG1FQueopO+qDFmnwpltpn/SXM\n" +
                "jdfvQQYm3rZAsfL4RL5dHPiZ823LiGUWZ0LFliVl65TThOkCQQD1NJ2ejPjGfzXe\n" +
                "rPC5eAEbm3BtXY3wVPnrCdNKcjtI7l4d/i5w+6rKc7RfnoqpvJbpe4uLVuxyUk1v\n" +
                "NItTthzFAkEAwL9BpnqolqskBu0GeiUVkJMOUs7YUvqKDFU80NR/HdXFYgHB5/kS\n" +
                "Jw5LE1LnmcrDwksG24+MViaY3q28DVM/awJBAMFRkzcOY5BzeLAvXraK0yzF1tSS\n" +
                "nrYs+MCChY+7EdyE+bTh0hGHiPaGVF3Sq/X4Vm6L1c+sX0wecShMn9AG0xUCQQCQ\n" +
                "X3HMQjn/SUeeDHJ6kUZ62Tu0WQz98n3uyPXZsiFY9qN3Sru0hwLK0FD5s3KY5qEE\n" +
                "6m/Di91hNl3xBY9DJ+TrAkEAryjws8ncAVVEIayL2WjfFOhirwL9GcVWRqy8WuUJ\n" +
                "WvgSx9ZGPsecDAzSXnuWUteAygJ+kl8Iltp2kEWuzerv2g==";
        try {
            long now = new Date().getTime();
            JSONObject jsonHeader = new JSONObject();
            jsonHeader.put("alg", "RS256");
            jsonHeader.put("typ", "JWT");
            String header = jsonHeader.toString();
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("alg", "RS256");
            jsonPayload.put("env", "dev-97eb6c");
            jsonPayload.put("iat", now);
            jsonPayload.put("exp", now + 10 * 60 * 1000);
            jsonPayload.put("uid", "1024");
            jsonPayload.put("refresh", 3600 * 1000);
            jsonPayload.put("expire", now + 7 * 24 * 60 * 60 * 1000);
            String payload = jsonPayload.toString();
            String baseHeader = Base64.encodeToString(header.getBytes(),
                    Base64.URL_SAFE + Base64.NO_PADDING + Base64.NO_WRAP);
            String basePayload = Base64.encodeToString(payload.getBytes(),
                    Base64.URL_SAFE + Base64.NO_PADDING + Base64.NO_WRAP);
            Log.d("测试", baseHeader);
            Log.d("测试", basePayload);
            String realPK = pKey.replaceAll("\n", "");

            String input = baseHeader + "." + basePayload;
            byte[] b1 = Base64.decode(realPK, Base64.DEFAULT);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(kf.generatePrivate(spec));
            privateSignature.update(input.getBytes());
            byte[] s = privateSignature.sign();
            String sign = Base64.encodeToString(s,
                    Base64.NO_PADDING + Base64.URL_SAFE + Base64.NO_WRAP);
            String token = baseHeader + "." + basePayload + "." + sign;
            Log.d("测试", token);
            return token;
        } catch (Exception e) {
            Log.d("测试", e.toString());
            return "";
        }
    }


    private void customLogin() {
        String token = jsonWebToken();
        // 此处替换成您生成的自定义登录 Ticket
        final String ticket = "661f0d53-dd8f-483b-b032-e9945478ec5b/@@/" + token;
        Log.d("测试", ticket);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    customAuth.loginInWithTicket(ticket);
                    Log.d("测试", "登录成功");
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final TcbException e) {
                    Log.e("登录失败", e.toString());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "登录失败" + e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void logout() {
        customAuth.logout();
    }
}
