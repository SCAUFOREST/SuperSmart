package com.supersmart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.supersmart.fragment.MenuFragment;
import com.supersmart.fragment.MouseControllerFragment;
import com.supersmart.fragment.PowerManagerFragment;
import com.supersmart.fragment.PptManagerFragment;
import com.supersmart.util.InjectView;
import com.supersmart.util.Injector;
import com.supersmart.util.NativeParams;
import com.supersmart.util.ViewUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener, MenuFragment.OnMenuItemSelectListener {

    private Socket mSocket;
    private DataOutputStream mDataOutputStream;
    private DataInputStream mDataInputStream;

    @InjectView(R.id.btnDrawerToggle)
    private Button mBtnDrawerToggle;

    private String mAddress;
    private int mPort;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        setBehindContentView(R.layout.menu_frame);
        mAddress = getIntent().getStringExtra(NativeParams.LOGIN_ADDRESS);
        mPort = getIntent().getIntExtra(NativeParams.LOGIN_PORT, -1);
        initViews();
        //bindSocket();
    }


    @Override
    public void onDestroy() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mDataOutputStream != null) {
            try {
                mDataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mDataInputStream != null) {
            try {
                mDataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }


    private void initViews() {
        Injector.get(this).inject();
        mBtnDrawerToggle.setOnClickListener(this);
        getSupportActionBar().hide();

        MenuFragment menuFragment = new MenuFragment();
        menuFragment.setOnMenuItemSelectListener(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.menu, menuFragment);
        ft.commit();

        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setShadowWidth(20);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindWidth(200);
        slidingMenu.setFadeDegree(0.7f);
        slidingMenu.setTouchModeAbove(SlidingMenu.LEFT);

        try {
            FragmentTransaction containerTransaction = getSupportFragmentManager().beginTransaction();
            containerTransaction.replace(R.id.container, PptManagerFragment.newInstance(mAddress , mPort));
            containerTransaction.commit();
        } catch (IOException e) {
            e.printStackTrace();
            ViewUtil.toast(getString(R.string.fragment_error));
        }

    }


    private void bindSocket() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(mAddress, mPort);
                    mDataInputStream = new DataInputStream(mSocket.getInputStream());
                    mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
                    while ((true)) {
                        String msg = mDataInputStream.readUTF();
                        if (msg != null) {
                            Looper.prepare();
                            ViewUtil.toast(msg);
                            Looper.loop();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    ViewUtil.toast(getString(R.string.connect_failed));
                    Looper.loop();
                    Intent intent = new Intent(MainActivity.this, ServerConnectActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDrawerToggle:
                toggle();
                break;
            default:
                break;
        }
    }


    @Override
    public void onMenuItemSelect(int position) {
        toggle();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        try {
            switch (position) {
                case 0:
                    ft.replace(R.id.container, PptManagerFragment.newInstance(mAddress , mPort));
                    ft.commit();
                    break;
                case 1:
                    ft.replace(R.id.container, MouseControllerFragment.newInstance(mAddress , mPort));
                    ft.commit();
                    break;
                case 2:
                    ft.replace(R.id.container, PowerManagerFragment.newInstance(mAddress, mPort));
                    ft.commit();
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            ViewUtil.toast(getString(R.string.fragment_error));
        }

    }

}