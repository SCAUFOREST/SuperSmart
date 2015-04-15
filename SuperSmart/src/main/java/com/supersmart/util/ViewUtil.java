package com.supersmart.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.Toast;

import com.supersmart.R;

/**
 * Created by forest on 2015/3/12.
 */
public class ViewUtil {
    private static Context mContext;

    /**
     * to init the Application Context when launcher
     *
     * @param context application context suggest !
     */
    public static void init(Context context) {
        mContext = context;
    }

    /**
     * show toast
     *
     * @param msg message to show
     */
    public static void toast(String msg) {
        if (msg == null)
            return;
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     *
     * @param text text to add color
     * @param colorId  color id from resource
     * @param start start index  of the colored character
     * @param end   end index of the colored character
     * @return
     */
    public static SpannableString getSpannableString(String text, int colorId, int start, int end)
            throws IndexOutOfBoundsException ,Resources.NotFoundException {
        if(text == null)
            throw new IllegalArgumentException("the colored text can not be null !");
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(colorId));
        spannableString.setSpan(colorSpan , start , end , Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

}
