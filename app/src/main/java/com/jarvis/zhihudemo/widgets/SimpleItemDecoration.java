package com.jarvis.zhihudemo.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.jarvis.zhihudemo.R;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/4/2 下午5:22
 * @changeRecord [修改记录] <br/>
 */

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {
    private Paint.FontMetrics fontMetrics;
    private int wight;
    public static int itemDecorationHeight = ExplosionUtils.dp2Px(30);
    private Paint paint;
    private ObtainTextCallback callback;
    private float itemDecorationPadding;
    private TextPaint textPaint;
    private Rect text_rect=new Rect();
    public SimpleItemDecoration(Context context, ObtainTextCallback callback) {

        wight=context.getResources().getDisplayMetrics().widthPixels;
        paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setColor(context.getResources().getColor(R.color.BL03));
        itemDecorationPadding = ExplosionUtils.dp2Px(10);
        this.callback = callback;

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(ExplosionUtils.dp2Px(25));
        fontMetrics = new Paint.FontMetrics();
        textPaint.getFontMetrics(fontMetrics);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            int bottom = view.getTop();
            int top = bottom - itemDecorationHeight;

            int position = parent.getChildAdapterPosition(view);
            String content = callback.getText(position);
            textPaint.getTextBounds(content,0, content.length(),text_rect);

            if(isFirstInGroup(position)) {
                c.drawRect(0, top, wight, bottom, paint);
                c.drawText(content, itemDecorationPadding + 0, bottom - fontMetrics.descent, textPaint);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        View child0 = parent.getChildAt(0);
        int position = parent.getChildAdapterPosition(child0);
        String content = callback.getText(position);
        if(child0.getBottom() <= itemDecorationHeight && isFirstInGroup(position+1)){
            c.drawRect(0, 0, wight, child0.getBottom(), paint);
            c.drawText(content, itemDecorationPadding+0, child0.getBottom() - fontMetrics.descent, textPaint);
        }
        else {
            c.drawRect(0, 0, wight, itemDecorationHeight, paint);
            c.drawText(content, itemDecorationPadding + 0, itemDecorationHeight-fontMetrics.descent, textPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if(isFirstInGroup(position)){
            outRect.top = itemDecorationHeight;
        }
    }

    public interface ObtainTextCallback {
        String getText(int position);
    }

    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String prevGroupId = callback.getText(pos - 1);
            String groupId = callback.getText(pos);
            if (prevGroupId.equals(groupId)) {
                return false;
            } else {
                return true;
            }
        }
    }
}
