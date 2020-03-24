package com.htphtp.tools.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView横向 间距
 * Created by HeTianpeng on 16/4/18.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;
    private boolean containLastPosition;
    private Paint mPaint;

    private int marginLeft;
    private int marginRight;


    /**
     */
    public SpaceItemDecoration(int dividerWidth, boolean containLastPosition) {
        this.mSpace = dividerWidth;
        this.containLastPosition = containLastPosition;
    }

    public SpaceItemDecoration(int dividerWidth) {
        this(dividerWidth, false);
    }

    public SpaceItemDecoration(int dividerWidth, boolean containLastPosition, int color) {
        this(dividerWidth, containLastPosition);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemCount = parent.getAdapter().getItemCount();
        int pos = parent.getChildAdapterPosition(view);
//        Log.d(TAG, "itemCount>>" +itemCount + ";Position>>" + pos);

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                drawLinearLayoutManagerHorizontal(outRect, itemCount, pos);

            } else {
                drawLinearLayoutManagerVertical(outRect, itemCount, pos);
            }
        }

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        if (mPaint == null) {
            return;
        }

        int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

            if (!containLastPosition && i == childSize - 1) {
                break;
            }

            //画水平分隔线
            int left = child.getLeft() + marginLeft;
            int right = child.getRight() - marginRight;
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mSpace;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        if (mPaint == null) {
            return;
        }

        int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

            if (!containLastPosition && i == childSize - 1) {
                break;
            }

            //画垂直分隔线
            int top = child.getTop();
            int bottom = child.getBottom() + mSpace;
            int left = child.getRight() + layoutParams.rightMargin;
            int right = left + mSpace;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    private void drawLinearLayoutManagerHorizontal(Rect outRect, int itemCount, int pos) {
        outRect.left = 0;
        outRect.top = 0;
        outRect.bottom = 0;

        if (containLastPosition) {
            outRect.right = mSpace;
        } else {
            if (pos != (itemCount - 1)) {
                outRect.right = mSpace;
            } else {
                outRect.right = 0;
            }
        }


    }

    private void drawLinearLayoutManagerVertical(Rect outRect, int itemCount, int pos) {
        outRect.left = 0;
        outRect.top = 0;
        outRect.right = 0;

        if (containLastPosition) {
            outRect.bottom = mSpace;
        } else {
            if (pos != (itemCount - 1)) {
                outRect.bottom = mSpace;
            } else {
                outRect.bottom = 0;
            }
        }
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }
}
