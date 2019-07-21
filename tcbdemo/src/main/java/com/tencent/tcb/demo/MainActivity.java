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
    private Config config = null;
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
        config = Constants.config();
        weixinAuth = WeixinAuth.getInstance(this, config);
        // this.invokeFunctionTest(this);
        // this.mockLogin();
        // this.fileTest(this);
        // this.downLoadFileTest(this);
        // this.deleteFileTest(this);
        // this.uploadFileTest(this);
        // this.dbAddTest(this);
        // this.dbCountTest(this);
        // this.dbQueryTest(this);
        // this.dbQueryCommandTest(this);
        // this.dbOrderTest(this);
        // this.dbDocTest(this);
        // this.dbServerDateTest(this);
        // this.dbGeoTest(this);
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

    private void dbAddTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                JSONObject result;
                try {
                    JSONObject data = new JSONObject();
                    data.put("name", "jimmytest");
                    data.put("age", 25);
                    result = db.collection("user").add(data);
                    Log.d("DbAdd", "Db add document success");
                } catch (TcbException e) {
                    Log.e("DbAdd", e.toString());
                } catch (JSONException e) {
                    Log.e("DbAdd", e.toString());
                }
            }
        }).start();
    }

    private void dbCountTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                JSONObject result;
                try {
                    result = db.collection("user").count();
                    Log.d("DbCount", "Db count success");
                } catch (TcbException e) {
                    Log.e("DbCount", e.toString());
                }
            }
        }).start();
    }

    private void dbQueryTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                JSONObject result;
                try {
                    JSONObject query = new JSONObject();
                    query.put("name", "jimmytest");
                    result = db.collection("user").where(query).get();
                    Log.d("DbQuery", "Db query success");
                } catch (TcbException e) {
                    Log.e("DbQuery", e.toString());
                } catch (JSONException e) {
                    Log.e("DbQuery", e.toString());
                }
            }
        }).start();
    }

    private void dbQueryCommandTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                Command cmd = db.command;
                JSONObject result;
                try {
                    JSONObject query = new JSONObject();
                    // age大于18
                    query.put("age", cmd.gt(18));
                    // age大于18并且小于30
                    // query.put("age", cmd.and(cmd.gt(18), cmd.lt(30)));

                    result = db.collection("user").where(query).get();
                    Log.d("DbQueryCmd", "Db query cmd success:" + result.toString());
                } catch (TcbException e) {
                    Log.e("DbQueryCmd", e.toString());
                } catch (JSONException e) {
                    Log.e("DbQueryCmd", e.toString());
                }
            }
        }).start();
    }

    private void dbOrderTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                JSONObject result;
                try {
                    result = db.collection("user").orderBy("age", "asc").get();
                    Log.d("DbQuery", "Db query success:" + result.toString());
                } catch (TcbException e) {
                    Log.e("DbQuery", e.toString());
                }
            }
        }).start();
    }

    private void dbFieldTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                JSONObject result;
                try {
                    HashMap<String, Boolean> fieldMap = new HashMap<>();
                    fieldMap.put("age", true);

                    result = db.collection("user").field(fieldMap).get();
                    Log.d("DbFieldCmd", "Db field success:" + result.toString());
                } catch (TcbException e) {
                    Log.e("DbFieldCmd", e.toString());
                }
            }
        }).start();
    }

    /**
     * 文档操作
     *
     * @param context
     */
    private void dbDocTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                JSONObject result;
                try {
                    // 创建文档
                    JSONObject data = new JSONObject();
                    data.put("name", "jimmytest2");
                    result = db.collection("user").add(data);
                    Log.d("DbDoc", "Db doc create success: " + result.toString());

                    // 解析docId
                    String docId = result.getString("id");

                    // 替换文档数据
                    data.put("age", 28);
                    result = db.collection("user").doc(docId).set(data);
                    Log.d("DbDoc", "Db doc set success: " + result.toString());

                    // 更新文档数据
                    JSONObject data2 = new JSONObject();
                    data2.put("age", 23);
                    result = db.collection("user").doc(docId).update(data2);
                    Log.d("DbDoc", "Db doc update success: " + result.toString());

                    // 删除文档
                    result = db.collection("user").doc(docId).remove();
                    Log.d("DbDoc", "Db doc remove success: " + result.toString());
                } catch (TcbException e) {
                    Log.e("DbDoc", e.toString());
                } catch (JSONException e) {
                    Log.e("DbDoc", e.toString());
                }
            }
        }).start();
    }

    /**
     * 构造服务器时间
     *
     * @param context
     */
    private void dbServerDateTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                JSONObject result;
                try {
                    JSONObject data = new JSONObject();
                    data.put("description", "eat an apple");
                    data.put("createTime", db.serverDate());

                    result = db.collection("user").add(data);
                    Log.d("DbServerDateTest", "Db server data success:" + result.toString());
                } catch (TcbException e) {
                    Log.e("DbServerDateTest", e.toString());
                } catch (JSONException e) {
                    Log.e("DbServerDateTest", e.toString());
                }
            }
        }).start();
    }

    private void dbGeoTest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Db db = new Db(config, context);
                JSONObject result;
                try {
                    // 创建带有地理位置的数据
                    JSONObject data = new JSONObject();
                    data.put("description", "eat an apple");
                    data.put("location", db.geo.point(113.323809, 23.097732));
                    result = db.collection("tcb_android").add(data);
                    Log.d("DbServerDateTest", "Db server data success:" + result.toString());

                    // 按地理位置寻找
                    JSONObject query = new JSONObject();
                    query.put("location", db.command.geoNear(
                            db.geo.point(113, 23),
                            1000,
                            5000));
                    result = db.collection("tcb_android").where(query).get();
                    Log.d("DbServerDateTest", "Db server data success:" + result.toString());

                } catch (TcbException e) {
                    Log.e("DbServerDateTest", e.toString());
                } catch (JSONException e) {
                    Log.e("DbServerDateTest", e.toString());
                }
            }
        }).start();
    }

}
