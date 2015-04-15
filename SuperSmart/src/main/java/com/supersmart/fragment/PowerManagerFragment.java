package com.supersmart.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supersmart.R;
import com.supersmart.interfaces.Bindable;
import com.supersmart.util.NativeParams;
import com.supersmart.util.ViewUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PowerManagerFragment extends Fragment implements View.OnClickListener, Bindable {

    private static PowerManagerFragment instance;
    private static DataInputStream mDataInputStream;
    private static DataOutputStream mDataOutputStream;
    private static Socket mSocket;

    public static PowerManagerFragment newInstance(String address, int port) throws IOException {
        if (instance == null) {
            synchronized (PowerManagerFragment.class) {
                instance = new PowerManagerFragment(address, port);
            }
        }
        return instance;
    }

    private PowerManagerFragment(String address, int port) throws IOException {
        bindSocket(address, port);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_power_manager, container, false);
        rootView.findViewById(R.id.btnShutOff).setOnClickListener(this);
        rootView.findViewById(R.id.btnReboot).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onDestroy() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            if (mDataInputStream != null) {
                mDataInputStream.close();
                mDataInputStream = null;
            }
            if (mDataOutputStream != null) {
                mDataOutputStream.close();
                mDataOutputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void bindSocket(final String address, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(address, port);
                    mDataInputStream = new DataInputStream(mSocket.getInputStream());
                    mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
                    mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.MANAGER_POWER);  // to tell server which type of connection to handle

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
                    ViewUtil.toast(getString(R.string.fragment_error));
                    getActivity().finish();
                    Looper.loop();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (mDataOutputStream != null) {
            switch (v.getId()) {
                case R.id.btnShutOff:
                    powerAction(getString(R.string.sure_power_off) , NativeParams.CMD_SHUTDOWN);
                    break;
                case R.id.btnReboot:
                    powerAction(getString(R.string.sure_power_reboot) , NativeParams.CMD_REBOOT);
                    break;
                default:
                    break;
            }
        } else {
            ViewUtil.toast(getString(R.string.output_stream_failed));
        }
    }


    private void powerAction(String msg , final String cmd) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.tips))
                .setMessage(msg)
                .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDataOutputStream != null) {
                            try {
                                mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + cmd);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            ViewUtil.toast("stream == null");
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

}
