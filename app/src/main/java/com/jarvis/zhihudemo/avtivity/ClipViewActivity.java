package com.jarvis.zhihudemo.avtivity;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.SeekBar;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;

/**
 * @author yyf @ Zhihu Inc.
 * @since 10-18-2018
 */
@ContentView(R.layout.activity_clip_view)
public class ClipViewActivity extends BaseActivity {

    @ViewInject(R.id.view)
    View targetView;

    @ViewInject(R.id.seekbar)
    SeekBar seekBar;

    CustomOutlineProvider provider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        provider = new CustomOutlineProvider();
        targetView.setOutlineProvider(provider);
        targetView.setClipToOutline(true);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    Rect originRect = new Rect();
//                    targetView.getClipBounds(originRect);
//                    int centerX = (originRect.right - originRect.left) / 2;
//                    int centerY = (originRect.bottom - originRect.top) / 2;
//                    Rect tmp = new Rect(centerX - 150, centerY - 150, centerX + 150, centerY + 150);
//                    targetView.setClipBounds(tmp);
                provider.px = progress * targetView.getMeasuredHeight() / 200;
                targetView.invalidateOutline();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public class CustomOutlineProvider extends ViewOutlineProvider {

        @Px int px = 0;

        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            int leftMargin = 0;
            int topMargin = 0;
            Rect selfRect = new Rect(leftMargin, topMargin, rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
            outline.setRoundRect(selfRect, px);
        }
    }
}
