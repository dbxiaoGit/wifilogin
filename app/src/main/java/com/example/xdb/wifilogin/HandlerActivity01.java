package com.example.xdb.wifilogin;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class HandlerActivity01 extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private class LongTimeTask extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }


}