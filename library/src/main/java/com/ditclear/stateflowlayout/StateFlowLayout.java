package com.ditclear.stateflowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.InflateException;
import android.view.View;
import android.widget.ImageView;

/**
 * 页面描述：{@link Node}
 *
 * Created by ditclear on 2017/7/10.
 */

public class StateFlowLayout extends LinearLayoutCompat {

    public static final String TAG = "StateFlowLayout";
    private final Rect rect;
    private Paint mStatePaint, mContentPaint;

    private int stateColor, contentColor = Color.BLACK;

    private float stateSize;
    private float contentSize;
    private int mDividerWidth = 0;
    private boolean showDividerMiddle;

    public StateFlowLayout(Context context) {
        this(context, null);
    }

    public StateFlowLayout(Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateFlowLayout(Context context,
            @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateFlowLayout, 0,
                    0);
            stateColor = a.getColor(R.styleable.StateFlowLayout_state_color,
                    ContextCompat.getColor(context, android.R.color.black));
            contentColor = a.getColor(R.styleable.StateFlowLayout_content_color, stateColor);
            stateSize = a.getDimensionPixelSize(R.styleable.StateFlowLayout_state_size, 18);
            contentSize = a.getDimensionPixelSize(R.styleable.StateFlowLayout_content_size, 14);
            a.recycle();
        }

        setOrientation(HORIZONTAL);
        setShowDividers(SHOW_DIVIDER_MIDDLE);

        mStatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStatePaint.setColor(stateColor);
        mStatePaint.setTextSize(stateSize);
        mStatePaint.setFakeBoldText(true);
        mStatePaint.setStrokeWidth(stateSize);

        mContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mContentPaint.setColor(contentColor);
        mContentPaint.setTextSize(contentSize);
        mContentPaint.setFakeBoldText(true);
        mContentPaint.setStrokeWidth(contentSize);

        rect = new Rect();

        mContentPaint.getTextBounds(TAG, 0, TAG.length(), rect);

    }

    @Override
    protected void onFinishInflate() {
        final int layoutDirection = ViewCompat.getLayoutDirection(this);
        if (layoutDirection != LAYOUT_DIRECTION_LTR) {
            throw new InflateException("please use the default direction");
        }
        super.onFinishInflate();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof Node)) {
                throw new InflateException(child.getClass().getSimpleName()
                        + "not implement interface Node");
            }
        }

        setGravity(Gravity.CENTER);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + rect.height() * 3);
        }
        showDividerMiddle = getShowDividers() == SHOW_DIVIDER_MIDDLE;
        if (getDividerDrawable() != null && showDividerMiddle) {
            mDividerWidth = getDividerDrawable().getIntrinsicWidth();

        }

        if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!showDividerMiddle) {
            return;
        }
        int childNodeCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Node node = (Node) child;
            int childLeft = child.getLeft();
            int childRight = child.getRight();
            if (hasDividerBeforeChildAt(i)) {
                if (node.isChild() && i != 0) {
                    childNodeCount++;
                }
                childLeft -= node.isChild() ? mDividerWidth / 2 * childNodeCount
                        : mDividerWidth / 2 * (childNodeCount + 1);
                childRight -= node.isChild() ? mDividerWidth / 2 * childNodeCount
                        : mDividerWidth / 2 * (childNodeCount + 1);
                child.layout(childLeft, child.getTop(), childRight, child.getBottom());
            }
        }
    }

    private int getNodeOffset(Node node, int childCount) {
        if (node.isNextChild()) {
            return mDividerWidth / 2 * childCount;
        } else {
            return mDividerWidth / 2;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Node node = (Node) child;
            if (child instanceof ImageView) {
                String state = (String) child.getTag();
                String content = (String) child.getContentDescription();
                if (state != null) {
                    mStatePaint.getTextBounds(state, 0, state.length(), rect);
                    canvas.drawText(state,
                            child.getLeft() - (rect.width() - child.getMeasuredWidth()) / 2,
                            child.getBottom() * 1.3f, mStatePaint);
                }

                if (content != null) {
                    mContentPaint.getTextBounds(content, 0, content.length(), rect);
                    canvas.drawText(content,
                            child.getLeft() - (rect.width() - child.getMeasuredWidth()) / 2,
                            child.getTop() * 0.8f, mContentPaint);
                }
                if (getDividerDrawable() == null || !showDividerMiddle) {
                    continue;
                }
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int layoutDirection = ViewCompat.getLayoutDirection(this);
                int position = 0;
                if (layoutDirection == LAYOUT_DIRECTION_RTL) {
                    position = child.getLeft() - lp.leftMargin - getDividerPadding();
                } else {
                    position = child.getRight() + lp.rightMargin;
                }
                if (i != getChildCount() - 1) {
                    drawVerticalDivider(canvas, position, node.isChild(), node.isNextChild());
                }

            }

        }


    }

    void drawVerticalDivider(Canvas canvas, int left, boolean divider, boolean nextDivider) {
        getDividerDrawable().setBounds(left, getPaddingTop() + getDividerPadding(),
                left + (divider || nextDivider ? mDividerWidth / 2 : mDividerWidth),
                getHeight() - getPaddingBottom() - getDividerPadding());
        getDividerDrawable().draw(canvas);
    }


}
