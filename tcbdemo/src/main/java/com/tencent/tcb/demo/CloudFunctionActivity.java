package com.tencent.tcb.demo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tcb.function.FunctionService;
import com.tencent.tcb.utils.TcbException;
import com.tencent.tcb.utils.TcbListener;

import org.json.JSONException;
import org.json.JSONObject;

public class CloudFunctionActivity extends AppCompatActivity {
    private final String LogTag = "CloudFunction";
    private TextView resultText;
    private Handler uiHandler;
    private FunctionService functionService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_function);

        resultText = (TextView) findViewById(R.id.function_result_text);
        uiHandler = new Handler();
        functionService = new FunctionService(Constants.envName, this);

        Button button = (Button) findViewById(R.id.test_function_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeFunction();
            }
        });
    }

    /**
     *
     * 在云开发控制台创建函数名为sum的云函数
     * 函数代码为:
     * exports.main = (event, context) => {
     *   console.log(event)
     *   console.log(context)
     *   return {
     *     sum: event.a + event.b
     *   }
     * }
     *
     */
    private void invokeFunction() {
        JSONObject data = new JSONObject();
        try {
            data.put("a", 1);
            data.put("b", 2);
        } catch (JSONException e) {
            Log.e(LogTag, e.toString());
        }

        functionService.callFunctionAsync("sum", data, new TcbListener() {
            @Override
            public void onSuccess(final JSONObject result) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultText.setText("函数执行结果: " + result.toString());
                    }
                });
            }

            @Override
            public void onFailed(final TcbException e) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultText.setText("执行错误：" + e.toString());
                    }
                });
            }
        });
    }
}
