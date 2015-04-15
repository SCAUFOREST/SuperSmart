package com.supersmart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.supersmart.util.InjectView;
import com.supersmart.util.Injector;


public class CommonActivity extends FragmentActivity implements View.OnClickListener{
    private final String PAY = "充值";
    private final String GIFT = "礼包";
    private final String FLOOR = "勇闯重楼";
    private final String TRAINING = "修炼馆";
    private static final String FRAGMENT_TAG = "fragment";
    @InjectView(R.id.ivBack)
    private ImageView mIvBack;
    @InjectView(R.id.tvTitle)
    private TextView mTvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common);
        Injector.get(this).inject();
        initTitleBar();
        initContent();
    }

    private void initTitleBar() {
        String title = getIntent().getStringExtra(FRAGMENT_TAG);
        mTvTitle.setText(title);
        mIvBack.setOnClickListener(this);
    }

    private void initContent() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager == null)
            return;
        fragmentManager.beginTransaction().replace(R.id.container, getFragment())
                .commit();
    }

    private Fragment getFragment() {

        return null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                finish();
                break;
            default:
                break;
        }
    }
}
