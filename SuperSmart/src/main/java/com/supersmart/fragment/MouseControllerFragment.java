package com.supersmart.fragment;

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


public class MouseControllerFragment extends Fragment implements Bindable{

    private static MouseControllerFragment instance;
    private static DataInputStream mDataInputStream;
    private static DataOutputStream mDataOutputStream;
    private static Socket mSocket ;

    public static MouseControllerFragment newInstance(String address , int  port) throws IOException {
        if(instance == null){
            synchronized (MouseControllerFragment.class){
                instance = new MouseControllerFragment(address , port);
            }
        }
        return instance;
    }

    private MouseControllerFragment(String address, int port) throws IOException {
        bindSocket(address , port);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mouse_controller, container, false);
    }

    @Override
    public void onDestroy(){
        try {
            if(mSocket!=null){
                mSocket.close();
                mSocket = null ;
            }
            if(mDataInputStream != null){
                mDataInputStream.close();
                mDataInputStream = null;
            }
            if(mDataOutputStream != null){
                mDataOutputStream.close();
                mDataOutputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void bindSocket(final String address , final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(address , port);
                    mDataInputStream = new DataInputStream(mSocket.getInputStream());
                    mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());
                    mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.MANAGER_MOUSE);  // to tell server which type of connection to handle

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
}
