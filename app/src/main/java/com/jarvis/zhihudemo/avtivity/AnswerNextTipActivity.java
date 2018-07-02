package com.jarvis.zhihudemo.avtivity;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.widget.Button;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.fragment.AnswerDialogFragment;
import com.jarvis.zhihudemo.view.AnswerTipsView;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/5 下午3:17
 * @changeRecord [修改记录] <br/>
 */

public class AnswerNextTipActivity extends BaseActivity {

    private AnswerDialogFragment mDialog;
    private AnswerTipsView mTipView;

    private Button button;

    private boolean isShow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);

        mTipView = findViewById(R.id.tip);

        findViewById(R.id.tip_btn).setOnClickListener(v -> {
//            showDialog();
            if (isShow) {
                isShow = false;
                mTipView.excuteAnim(1, 0);
            } else {
                isShow = true;
                mTipView.excuteAnim(0, 1);
            }
        });

        button = findViewById(R.id.shot_share_action);
        button.setOnClickListener(v -> {
            ((AnimatedVectorDrawable) button.getCompoundDrawables()[1]).start();
        });
    }

    private void showDialog() {
        mDialog = AnswerDialogFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        mDialog.show(fm, "123");
    }
}
