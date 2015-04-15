package com.supersmart.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.supersmart.R;
import com.supersmart.interfaces.Bindable;
import com.supersmart.util.NativeParams;
import com.supersmart.util.ViewUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class PptManagerFragment extends Fragment implements View.OnClickListener , Bindable {

    private Button mBtnNext;
    private Button mBtnPrior;
    private Button mBtnCurrent;
    private Button mBtnDirectTo;
    private Button mBtnFirstPage;
    private Button mBtnExit;

    private static PptManagerFragment instance;
    private static DataInputStream mDataInputStream;
    private static DataOutputStream mDataOutputStream;
    private static Socket mSocket ;

    public static PptManagerFragment newInstance(String address , int  port) throws IOException {
        if(instance == null){
            synchronized (PptManagerFragment.class){
                instance = new PptManagerFragment(address , port);
            }
        }
        return instance;
    }

    private PptManagerFragment(String address, int port) throws IOException {
        bindSocket(address , port);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ppt_manager, container, false);
        mBtnCurrent = (Button) rootView.findViewById(R.id.btnCurrentPage);
        mBtnCurrent.setOnClickListener(this);
        mBtnDirectTo = (Button) rootView.findViewById(R.id.btnDirectTo);
        mBtnDirectTo.setOnClickListener(this);
        mBtnFirstPage = (Button) rootView.findViewById(R.id.btnFirstPage);
        mBtnFirstPage.setOnClickListener(this);
        mBtnNext = (Button) rootView.findViewById(R.id.btnNext);
        mBtnNext.setOnClickListener(this);
        mBtnPrior = (Button) rootView.findViewById(R.id.btnPrior);
        mBtnPrior.setOnClickListener(this);
        mBtnExit = (Button) rootView.findViewById(R.id.btnExit);
        mBtnExit.setOnClickListener(this);
        return rootView;
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
    public void onClick(View v) {
        ViewUtil.toast("click");
        try {
            if(mDataOutputStream != null) {
                ViewUtil.toast("stream not null");


                switch (v.getId()) {

                    case R.id.btnCurrentPage:
                        mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.PPT_CURRENT);
                        break;
                    case R.id.btnPrior:
                        mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.PPT_PRIOR);
                        break;
                    case R.id.btnNext:
                        mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.PPT_NEXT);
                        break;
                    case R.id.btnDirectTo:
                        showDirectDialog();
                        break;
                    case R.id.btnFirstPage:
                        mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.PPT_FIRST_PAGE);
                        break;
                    case R.id.btnExit:
                        mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.PPT_EXIT );
                        break;
                    default:
                        break;
                }
            }else {
                ViewUtil.toast(getString(R.string.output_stream_failed));
            }
        } catch (IOException e) {
            e.printStackTrace();
            ViewUtil.toast(getString(R.string.operate_failed));
        }
    }

    private void showDirectDialog() {
        final Dialog dialog = new Dialog(getActivity() , R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_direct_to);
        final EditText etNum = (EditText) dialog.findViewById(R.id.etPageNum);
        Button btnSure = (Button) dialog.findViewById(R.id.btnSure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = etNum.getText().toString();
                try {
                    int pageNum = Integer.valueOf(num);
                    mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.PPT_DIRECT_TO +
                                                NativeParams.COMMAND_SUB_DIVIDER + pageNum);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    ViewUtil.toast(getString(R.string.invalid_number));
                } catch (IOException e) {
                    e.printStackTrace();
                    ViewUtil.toast(getString(R.string.operate_failed));
                }
                dialog.dismiss();
            }
        });
        dialog.show();
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
                    mDataOutputStream.writeUTF(NativeParams.COMMAND_PREFIX + NativeParams.COMMAND_DIVIDER + NativeParams.MANAGER_PPT);  // to tell server which type of connection to handle

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
