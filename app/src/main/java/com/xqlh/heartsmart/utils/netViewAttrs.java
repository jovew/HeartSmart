package com.xqlh.heartsmart.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.xqlh.heartsmart.R;


/**
 * @author xushangfei
 * @time Created at 2016/3/12.
 * @email xsf_uestc_ncl@163.com
 */
public class netViewAttrs {
    private int netColor;
    private int overlayColor;
    private int textColor;
    private int overlayAlpha;
    private int tagsize;

    public netViewAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.netView, defStyleAttr, 0);
        netColor = ta.getColor(R.styleable.netView_netColor, context.getResources().getColor(R.color.slategray));
        overlayAlpha = ta.getInteger(R.styleable.netView_overlayAlpha, 130);
        tagsize = ta.getInteger(R.styleable.netView_tagsize, 20);
        overlayColor = ta.getColor(R.styleable.netView_overlayColor, context.getResources().getColor(R.color.colorPrimaryDark));
        textColor = ta.getColor(R.styleable.netView_textColor, context.getResources().getColor(R.color.skyblue));
        ta.recycle();
    }

    public int getNetColor() {
        return netColor;
    }

    public int getOverlayAlpha() {
        return overlayAlpha;
    }

    public int getTextColor() {

        return textColor;
    }

    public int getTagsize() {
        return tagsize;
    }

    public int getOverlayColor() {
        return overlayColor;
    }
}
