package com.jarvis.zhihudemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jarvis.zhihudemo.R;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/3/6 下午4:49
 * @changeRecord [修改记录] <br/>
 */

public class AnswerDialogFragment extends DialogFragment {

    private View mContainerView;
    private View mTipsView;
    private View mBtn;

    public static AnswerDialogFragment newInstance() {
        AnswerDialogFragment dialogFragment = new AnswerDialogFragment();
        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView();
        return mContainerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView() {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        mContainerView = inflater.inflate(R.layout.dialog_answer, null, false);

        mTipsView = ((LinearLayout)mContainerView).getChildAt(0);

        mBtn = ((LinearLayout)mContainerView).getChildAt(
                ((LinearLayout)mContainerView).getChildCount() - 1
        );
        mBtn.setOnClickListener(v -> {
            AnswerDialogFragment.this.dismiss();
        });

    }
}
