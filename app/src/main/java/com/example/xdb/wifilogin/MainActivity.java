package com.example.xdb.wifilogin;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private boolean isLoginSuccess = false;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //editText.setText(editText.getText() + "\n" + msg.obj.toString());
            editText.getText().append("\n" + msg.obj.toString());
            editText.setSelection(editText.getText().length(), editText.getText().length());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        editText.setMovementMethod(ScrollingMovementMethod.getInstance());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isLoginSuccess) {
                    doLogin();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void doLogin() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build();
        FormBody formBody = new FormBody.Builder()
                .add("opr", "pwdLogin")
                .add("userName", "xxx")
                .add("pwd", "xxx")
                .add("rememberPwd", "1")
                .build();
        final Request request = new Request.Builder()
                .url("http://1.1.1.3/ac_portal/login.php")//请求的url
                .post(formBody)//设置请求方式，get()/post()  查看Builder()方法知，在构建时默认设置请求方式为GET
                .build(); //构建一个请求Request对象

        //创建/Call
        Call call = okHttpClient.newCall(request);
        Message message = new Message();
        //message.obj = "request:" + request.toString();
        //handler.sendMessage(message);
        //message = new Message();
        //message.obj = "body:" + request.body().toString();
        //handler.sendMessage(message);
        message = new Message();
        message.obj = "加入队列 异步操作";
        handler.sendMessage(message);
  /*
        try {
            Response response = call.execute();
            String resp = response.body().string();
            Log.i("response", resp);
            if (resp.contains("logon success") || resp.contains("用户已在线")) {
                message = new Message();
                message.obj = resp;
                handler.sendMessage(message);
                isLoginSuccess = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       */
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("error", "连接失败");
                Message message = new Message();
                message.obj = "连接失败!";
                handler.sendMessage(message);
            }

            //异步请求(非主线程)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                //Log.i("response", resp);
                if (resp.contains("logon success") || resp.contains("用户已在线")) {
                    Message message = new Message();
                    message.obj = resp;
                    handler.sendMessage(message);
                    isLoginSuccess = true;
                }
            }
        });


    }

    class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            Log.i("HttpLogInfo", message);
        }
    }


}
