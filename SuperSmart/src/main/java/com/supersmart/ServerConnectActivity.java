package com.supersmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supersmart.util.DataUtil;
import com.supersmart.util.InjectView;
import com.supersmart.util.Injector;
import com.supersmart.util.NativeParams;
import com.supersmart.util.ViewUtil;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;


public class ServerConnectActivity extends Activity implements View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {
    @InjectView(R.id.btnLogin)
    private Button mBtnLogin;
    @InjectView(R.id.tvIpAddress)
    private AutoCompleteTextView mTvAccount;
    @InjectView(R.id.etPort)
    private EditText mEtPassword;
    @InjectView(R.id.cbAccount)
    private CheckBox mCbAccount;
    @InjectView(R.id.loginLayout)
    private RelativeLayout mRootView;

    final ArrayList<String> mOriginAccounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        Injector.get(this).inject();
        mRootView.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mCbAccount.setOnClickListener(this);
        Set<String> names = DataUtil.getSharedPreferencesData(getString(R.string.addressdata)).keySet();
        for (String key : names) {
            mOriginAccounts.add(key);
        }
        AccountAdapter adapter = new AccountAdapter(this, mOriginAccounts);
        mTvAccount.setDropDownBackgroundResource(R.drawable.dropdown_list_bg);
        mTvAccount.setThreshold(1);
        mTvAccount.setAdapter(adapter);
        mTvAccount.setDropDownVerticalOffset(10);
        mTvAccount.setOnItemClickListener(this);
        mTvAccount.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.cbAccount)
            mCbAccount.setChecked(false);
        switch (v.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.cbAccount:
                if (mCbAccount.isChecked()) {
                    mTvAccount.showDropDown();
                } else {
                    mTvAccount.dismissDropDown();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String pwd = DataUtil.getSharedPreferencesData(getString(R.string.addressdata))
                .get(mOriginAccounts.get(position));
        mEtPassword.setText(pwd);
        mCbAccount.setChecked(false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!mTvAccount.isPopupShowing()) {
            mCbAccount.setChecked(false);
        } else {
            mCbAccount.setChecked(true);
        }
        String userName = mTvAccount.getText().toString();
        if (!mOriginAccounts.contains(userName)) {    // no account history matched
            mEtPassword.setText("");
        } else {   // account history matched
            String pwd = DataUtil.getSharedPreferencesData(getString(R.string.addressdata))
                    .get(userName);
            mEtPassword.setText(pwd);
            mCbAccount.setChecked(false);
            mTvAccount.dismissDropDown();
        }
    }

    /**
     * user login
     */
    public void login() {
        String address = mTvAccount.getText().toString();
        String port = mEtPassword.getText().toString();
        if (address == null || address.length() == 0) {     // address not input
            alert(getString(R.string.login), getString(R.string.ip_invalid), getString(R.string.sure));
            return;
        }
        if (port == null || port.length() == 0) {   // port not input
            alert(getString(R.string.login), getString(R.string.port_invalid), getString(R.string.sure));
            return;
        }

        if(!isValidAddress(address, port)){
            ViewUtil.toast(getString(R.string.invalid_address));
            return;
        }
        DataUtil.recordSharedPreferencesData(getString(R.string.addressdata), address, port);
        Intent intent = new Intent(ServerConnectActivity.this, MainActivity.class);
        intent.putExtra(NativeParams.LOGIN_ADDRESS, address);
        intent.putExtra(NativeParams.LOGIN_PORT,Integer.valueOf(port));
        startActivity(intent);
        finish();

    }

    /**
     * validate ip address and port
     * @param address ip address
     * @param port port to connect
     * @return whether the  address and port is valid
     */
    private boolean isValidAddress(String address, String port) {
        Pattern pattern = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
        if(!pattern.matcher(address).matches()){
            return false;
        }
        ViewUtil.toast("ip valid");
        pattern = Pattern.compile("[0-9]*");
        if(!pattern.matcher(port).matches()){
            return  false;
        }
        return true;
    }


    /**
     * tips dialog to show
     *
     * @param title        dialog title
     * @param msg          tips message
     * @param buttonString button text
     */
    public void alert(String title, String msg, String buttonString) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(buttonString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }


    static class ViewHolder {
        TextView tvAccountItem;
        ImageView ivDelete;
    }

    /**
     * AutoCompleteTextView Adapter
     */
    class AccountAdapter extends BaseAdapter implements Filterable {
        private LayoutInflater mInflater;
        private ArrayList<String> mAccounts;
        private ArrayFilter mFilter;
        private Context mContext;
        private Object mLock = new Object();

        public AccountAdapter(Context context, ArrayList<String> accounts) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mAccounts = accounts;
        }

        @Override
        public int getCount() {
            return mAccounts.size();
        }

        @Override
        public Object getItem(int position) {
            return mAccounts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_account, null);
                viewHolder = new ViewHolder();
                viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
                viewHolder.tvAccountItem = (TextView) convertView.findViewById(R.id.tvAccountItem);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvAccountItem.setText(mAccounts.get(position));
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAccountInfo(position);
                }
            });
            return convertView;
        }

        private void removeAccountInfo(final int position) {
            new AlertDialog.Builder(mContext)
                    .setTitle(getString(R.string.tips))
                    .setMessage(getString(R.string.remove_account_confirm))
                    .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DataUtil.removeUserData(getString(R.string.addressdata), mAccounts.get(position));
                            mAccounts.remove(position);
                            notifyDataSetChanged();
                        }
                    })
                    .create()
                    .show();
        }

        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new ArrayFilter();
            }
            return mFilter;
        }

        /**
         * AutoCompleteTextView Filter
         */
        private class ArrayFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();

                if (mAccounts == null || mAccounts.size() == 0)
                    mAccounts = new ArrayList<>(mOriginAccounts);

                if (prefix == null || prefix.length() == 0) {
                    ArrayList<String> list;
                    synchronized (mLock) {
                        list = new ArrayList<>(mAccounts);
                    }
                    results.values = list;
                    results.count = list.size();
                } else {
                    String prefixString = prefix.toString().toLowerCase();

                    ArrayList<String> values;
                    synchronized (mLock) {
                        values = new ArrayList<String>(mAccounts);
                    }

                    final int count = values.size();
                    final ArrayList<String> newValues = new ArrayList<>();

                    for (int i = 0; i < count; i++) {
                        final String value = values.get(i);
                        final String valueText = value.toString().toLowerCase();

                        // First match against the whole, non-splitted value
                        if (valueText.startsWith(prefixString)) {
                            newValues.add(value);
                        } else {
                            final String[] words = valueText.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].startsWith(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }
                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                mAccounts = (ArrayList<String>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }
}
