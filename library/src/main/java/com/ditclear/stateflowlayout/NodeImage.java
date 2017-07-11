package com.ditclear.stateflowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 页面描述：
 *
 * Created by ditclear on 2017/7/11.
 */

public class NodeImage extends AppCompatImageView implements Node {

    private boolean child, nextChild;
    private String state = "";

    public NodeImage(Context context) {
        this(context, null);
    }

    public NodeImage(Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeImage(Context context,
            @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NodeImage, 0,
                    0);
            state = a.getString(R.styleable.NodeImage_state);
            child = a.getBoolean(R.styleable.NodeImage_child, false);
            nextChild = a.getBoolean(R.styleable.NodeImage_next_child, false);
            a.recycle();
        }
    }

    @Override
    public boolean isChild() {
        return child;
    }

    @Override
    public boolean isNextChild() {
        return nextChild;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getContent() {
        return getContentDescription() == null ? "" : getContentDescription().toString();
    }
}
