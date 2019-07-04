package com.tencent.tcb.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tencent.tcb.utils.Config;
import com.tencent.tcb.utils.Request;
import com.tencent.tcb.utils.TcbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Config config = new Config();
                Request request = new Request(config);
                HashMap<String, Object> params = new HashMap<>();
                params.put("path", "jimmyjzhang/wxf5132aa5236cbef7.o6zAJs6Ww9c8IdpuAjeD9JTHyuqg.7RWbrbsJ8fNab37154c5773759d9dd0ca4c0288ab100.jpeg");
                try {
                    request.send("storage.getUploadMetadata", params, "POST");
                } catch (TcbException e) {
                    System.out.println(e);
                }
            }
        }).start();
    }
}
