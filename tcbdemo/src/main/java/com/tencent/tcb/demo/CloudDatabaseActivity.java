package com.tencent.tcb.demo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tcb.database.Db;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

public class CloudDatabaseActivity extends AppCompatActivity {
    private final String LogTag = "CloudDatabase";
    private TextView resultText;
    private Handler uiHandler;
    private Db db;
    private String dataId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_database);

        resultText = (TextView) findViewById(R.id.result_text);
        uiHandler = new Handler();
        db = new Db(Constants.envName, this);

        Button addButton = (Button) findViewById(R.id.add_button);
        Button queryButton = (Button) findViewById(R.id.query_button);
        Button updateButton = (Button) findViewById(R.id.update_button);
        Button deleteButton = (Button) findViewById(R.id.delete_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbAdd();
            }
        });
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbQuery();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbUpdate();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRemove();
            }
        });


    }

    private void dbAdd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject result;
                try {
                    JSONObject data = new JSONObject();
                    data.put("name", "jimmytest");
                    data.put("age", 18);
                    result = db.collection("user").add(data);
                    dataId = result.optString("id");
                    // 显示结果
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resultText.setText(result.toString());
                        }
                    });
                } catch (TcbException e) {
                    Log.e(LogTag, e.toString());
                } catch (JSONException e) {
                    Log.e(LogTag, e.toString());
                }
            }
        }).start();
    }



    private void dbQuery() {
        if (dataId == null || dataId.isEmpty()) {
            resultText.setText("无记录可查，请先创建一个记录");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject result;
                try {
                    JSONObject query = new JSONObject();
                    query.put("_id", dataId);
                    result = db.collection("user").where(query).get();
                    // 显示结果
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resultText.setText(result.toString());
                        }
                    });
                } catch (TcbException e) {
                    Log.e(LogTag, e.toString());
                } catch (JSONException e) {
                    Log.e(LogTag, e.toString());
                }
            }
        }).start();
    }

    private void dbUpdate() {
        if (dataId == null || dataId.isEmpty()) {
            resultText.setText("无记录可更新，请先创建一个记录");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject result;
                try {
                    JSONObject data = new JSONObject();
                    data.put("age", 30);
                    result = db.collection("user").doc(dataId).update(data);
                    // 显示结果
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resultText.setText(result.toString());
                        }
                    });
                } catch (TcbException e) {
                    Log.e(LogTag, e.toString());
                } catch (JSONException e) {
                    Log.e(LogTag, e.toString());
                }
            }
        }).start();
    }

    private void dbRemove() {
        if (dataId == null || dataId.isEmpty()) {
            resultText.setText("无记录可删，请先创建一个记录");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject result;
                try {
                    result = db.collection("user").doc(dataId).remove();
                    dataId = null;
                    // 显示结果
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resultText.setText(result.toString());
                        }
                    });
                } catch (TcbException e) {
                    Log.e(LogTag, e.toString());
                }
            }
        }).start();
    }
}
