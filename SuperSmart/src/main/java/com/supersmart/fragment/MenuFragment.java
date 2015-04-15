package com.supersmart.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.supersmart.R;


public class MenuFragment extends Fragment {

    private OnMenuItemSelectListener mOnMenuItemSelectListener;

    public MenuFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.lvMenu);
        listView.setAdapter(new MenuAdapter(getActivity()));
        return rootView;
    }

    public void setOnMenuItemSelectListener(OnMenuItemSelectListener onMenuItemSelectListener) {
        mOnMenuItemSelectListener = onMenuItemSelectListener;
    }

    public interface OnMenuItemSelectListener {
        void onMenuItemSelect(int position);
    }

    class MenuAdapter extends BaseAdapter {

        private Context mContext;

        public MenuAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ImageView itemView = new ImageView(mContext);
            itemView.setBackgroundDrawable(getResources().getDrawable(R.drawable.lv_item_selector));
            itemView.setScaleType(ImageView.ScaleType.CENTER);
            itemView.setPadding(0, 20, 0, 20);
            if (position == 0) {
                itemView.setImageResource(R.drawable.ppt);
            } else if (position == 1) {
                itemView.setImageResource(R.drawable.mouse);
            } else if (position == 2) {
                itemView.setImageResource(R.drawable.power);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMenuItemSelectListener == null){
                        throw new IllegalArgumentException("You have to set OnMenuSelectedListener");
                    }else{
                        mOnMenuItemSelectListener.onMenuItemSelect(position);
                    }
                }
            });
            return itemView;
        }

        public void showAppInfo(){

            // show App Information Dialog
            final Dialog appInfoDialog = new Dialog(getActivity() ,R.style.DialogStyle );
            appInfoDialog.setContentView(R.layout.dialog_app_info);
            appInfoDialog.setCanceledOnTouchOutside(true);
            TextView confirm = (TextView) appInfoDialog.findViewById(R.id.tvConfirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(appInfoDialog != null){
                        appInfoDialog.dismiss();
                    }
                }
            });
            appInfoDialog.show();
        }
    }

}
