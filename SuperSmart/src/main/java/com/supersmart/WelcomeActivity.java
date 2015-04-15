package com.supersmart;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.supersmart.util.DataUtil;
import com.supersmart.util.ViewUtil;


public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        DataUtil.init(getApplicationContext());  // init data utils
        ViewUtil.init(getApplicationContext()); //  init view utils
        start();
    }

    /**
     * try to sleep 1500ms and then turn to login page
     */
    private void start() {
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent  = new Intent(WelcomeActivity.this , ServerConnectActivity.class) ;
                startActivity(intent);
                finish();
            }
        };
        asyncTask.execute();
    }

}
