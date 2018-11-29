package com.jarvis.zhihudemo.avtivity;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.widgets.ToolBarContainerView;
import com.jarvis.zhihudemo.widgets.animation.FlingAnimation;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-23-2018
 */
@ContentView(R.layout.activity_toolbar)
public class ToolbarActivity extends AppCompatActivity {

    @ViewInject(R.id.btn)
    Button button;

    @ViewInject(R.id.toolbar_container)
    ToolBarContainerView containerView;

    @ViewInject(R.id.btn_s)
    Button button_s;

    @ViewInject(R.id.toolbar_container_s)
    ToolBarContainerView containerView_s;

    boolean excute = false;
    boolean excute_s = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        ((View) containerView.getParent()).setOutlineProvider(new ShadowViewOutlineProvider());
//        ((View) containerView.getParent()).setClipToOutline(true);

        button.setOnClickListener(v -> {
            containerView.excuteAnim(excute ? 1 : 0, excute ? 0 : 1);
            excute = !excute;
        });

        button_s.setOnClickListener(v -> {
            containerView_s.excuteAnim(excute_s ? 1 : 0, excute_s ? 0 : 1);
            excute_s = !excute_s;
        });
    }

    public class ShadowViewOutlineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            int topMargin = 20;
            Rect selfRect = new Rect(0, topMargin, rect.right - rect.left, rect.bottom - rect.top);
            outline.setRoundRect(selfRect, 0);
        }
    }
}
