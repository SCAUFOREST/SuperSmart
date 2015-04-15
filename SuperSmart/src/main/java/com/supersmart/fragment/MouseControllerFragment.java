package com.supersmart.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.supersmart.R;
import com.supersmart.interfaces.Bindable;
import com.supersmart.util.NativeParams;
import com.supersmart.util.ViewUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class MouseControllerFragment extends Fragment implements View.OnClickListener ,Bindable{

    private static MouseControllerFragment instance;
    private static DataInputStream mDataInputStream;
    private static DataOutputStream mDataOutputStream;
    private static Socket mSocket ;
    private Button mBtnLeft;
    private Button mBtnRight;
    private Button mBtnWheel;
    private View mVTouchPanel;
    private TextView mTextView;
    private GestureDetector mGestureDetector;

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
        View rootView = inflater.inflate(R.layout.fragment_mouse_controller, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        mBtnLeft = (Button) rootView.findViewById(R.id.btnLeft);
        mBtnLeft.setOnClickListener(this);
        mBtnRight= (Button) rootView.findViewById(R.id.btnRight);
        mBtnRight.setOnClickListener(this);
        mBtnWheel= (Button) rootView.findViewById(R.id.btnWheel);
        mBtnWheel.setOnClickListener(this);
        mVTouchPanel =  rootView.findViewById(R.id.vTouchPanel);
        mVTouchPanel.setLongClickable(true);
        mGestureDetector = new GestureDetector(getActivity() , new MouseGestureListener());
        mVTouchPanel.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        mTextView = (TextView) rootView.findViewById(R.id.textView);
    }

    @Override
    public void onDestroy(){
        try {
            mGestureDetector = null;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLeft:
                sendClickAction(NativeParams.MOUSE_LEFT);
                break;
            case R.id.btnRight:
                sendClickAction(NativeParams.MOUSE_RIGHT);
                break;
            case R.id.btnWheel:
                sendWheelAction(NativeParams.MOUSE_WHEEL);
                break;
            default:
                break;
        }
    }

    private void sendWheelAction(String action) {
        if(mDataOutputStream != null){
            try {
                mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + action
                + NativeParams.COMMAND_SUB_DIVIDER + 2);
            } catch (IOException e) {
                e.printStackTrace();
                ViewUtil.toast(getString(R.string.operate_failed));
            }
        }else{
            ViewUtil.toast(getString(R.string.output_stream_failed));
        }
        mTextView.append(action + "\n");
        System.out.println(action);
    }

    public void sendClickAction(String action){
        if(mDataOutputStream != null){
            try {
                mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + action);
            } catch (IOException e) {
                e.printStackTrace();
                ViewUtil.toast(getString(R.string.operate_failed));
            }
        }else{
            ViewUtil.toast(getString(R.string.output_stream_failed));
        }
        mTextView.append(action + "\n");
        System.out.println(action);
    }

    private void sendMoveAction(String action , int distanceX, int distanceY) {
        if(mDataOutputStream != null){
            try {
                mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER
                        + action + NativeParams.COMMAND_SUB_DIVIDER + distanceX
                        + NativeParams.COMMAND_SUB_DIVIDER + distanceY);
            } catch (IOException e) {
                e.printStackTrace();
                ViewUtil.toast(getString(R.string.operate_failed));
            }
        }else{
            ViewUtil.toast(getString(R.string.output_stream_failed));
        }
        mTextView.append(action + "\n");
        System.out.println(action);
    }

    class MouseGestureListener extends GestureDetector.SimpleOnGestureListener{
        public MouseGestureListener() {
            super();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //sendClickAction("tag up");
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //sendClickAction("fling velocityX = " + velocityX + " velocityY = " + velocityY);
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            sendClickAction(NativeParams.MOUSE_LEFT);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            sendClickAction(NativeParams.MOUSE_DOUBLE_CLICK);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(distanceX > 0){
                distanceX = distanceX >= 1 ? -distanceX : -1 ;
            }else{
                distanceX = distanceX >= -1 ? 1 : -distanceX;
            }
            if(distanceY > 0){
                distanceY = distanceY >= 1 ? -distanceY : -1 ;
            }else{
                distanceY = distanceY >= -1 ? 1 : -distanceY;
            }
            sendMoveAction(NativeParams.MOUSE_MOVE, (int)distanceX , (int)distanceY);
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            //sendClickAction("press");
            super.onShowPress(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //sendClickAction("onLongPress");
            super.onLongPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            //sendClickAction("onDown");
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            //sendClickAction("onDoubleTapEvent");
            return false;
        }
    }


}
