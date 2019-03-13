package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 11-09-2018
 */
public class QuestionTitleView extends LinearLayout {

    private static final int MAX_LINE = 5;

    public static final int MODE_LAST  = 0x011;
    public static final int MODE_MORE  = 0x012;
    public static final int MODE_LIMIT  = 0x013;
    public static final int MODE_GONE  = 0x014;

    public String mCurrentTitle = "";


    private AppCompatEditText mTitleEditView;

    private TextView mHintView;

    private Handler mHandler = new Handler();

    private Runnable handleHintRunnable = () -> {
        int length = mCurrentTitle.length();
        if (length < 4) {
            changeHineMode(MODE_LAST);
        } else if (length > 41 && length <= 50) {
            changeHineMode(MODE_MORE);
        } else if (length > 50) {
            changeHineMode(MODE_LIMIT);
        } else {
            changeHineMode(MODE_GONE);
        }
    };

    private List<ITitleEditChangeListener> iTitleEditChanges = new ArrayList<>();

    public interface ITitleEditChangeListener {
        void onTextChange(String title);
    }

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable charSequence) {
            String title = charSequence.toString().trim();
            notifyTextChange(title);
        }
    };

    public QuestionTitleView(@NonNull Context context) {
        this(context, null);
    }

    public QuestionTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        initView();
    }

    private void initView() {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View rootView = layoutInflater.inflate(R.layout.community_editor_layout_editor_edit_title_header,  this, true);

        mTitleEditView = rootView.findViewById(R.id.title);
        mTitleEditView.addTextChangedListener(mWatcher);

//        mTitleEditView = new ZHEditText(getContext(), null, R.style.CommunityEditor_Zhihu_Edit_Control_Light);
//        mTitleEditView.setTextAppearance(getContext(), com.zhihu.android.editor.R.style.Zhihu_TextAppearance_Medium_Normal_PrimaryLight);
//        mTitleEditView.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
////        mTitleEditView.setShowSoftInputOnFocus(false);
//        mTitleEditView.setMaxLines(MAX_LINE);
//        Optional.ofNullable(mTitleEditView.getBackground()).ifPresent(drawable -> drawable.setAlpha(0));
//        mTitleEditView.addTextChangedListener(mWatcher);
//        mTitleEditView.setHint("输入问题并以问号结尾");
//        mTitleEditView.setTextSize(20);
//        mTitleEditView.setLineSpacing(4, 1);
//        mTitleEditView.setFocusable(true);
//        mTitleEditView.setFocusableInTouchMode(true);
//        LayoutParams editTitleParams = new LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//
//        editTitleParams.topMargin = DisplayUtils.dpToPixel(getContext(), 4.0F);
//        editTitleParams.leftMargin = DisplayUtils.dpToPixel(getContext(), 14.0F);
//        editTitleParams.rightMargin = DisplayUtils.dpToPixel(getContext(), 14.0F);
//
//        addView(mTitleEditView, editTitleParams);

        mHintView = new TextView(getContext());
        mHintView.setTextSize(14);
        FrameLayout.LayoutParams editHintParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editHintParams.gravity = Gravity.END;

        addView(mHintView, editHintParams);

        mTitleEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCurrentTitle = s.toString().trim();
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(handleHintRunnable, 2000);
            }
        });
    }

    public void changeHineMode(int mode) {
        LinearLayout.LayoutParams editHintParams = (LinearLayout.LayoutParams) mHintView.getLayoutParams();
        if (editHintParams == null) {
            return;
        }
        if (mode == MODE_LAST) {
            mHintView.setVisibility(VISIBLE);
            mHintView.setText("至少输入 4 个字");
            editHintParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            editHintParams.topMargin = - mTitleEditView.getMeasuredHeight() + (mTitleEditView.getMeasuredHeight() / 2 - mHintView.getMeasuredHeight() / 2);
        } else if (mode == MODE_MORE) {
            mHintView.setVisibility(VISIBLE);
            int length = mCurrentTitle.length();
            mHintView.setText("还可以输入 "+ (50 - length) +" 个字");
            editHintParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            editHintParams.topMargin = 0;
        } else if (mode == MODE_LIMIT) {
            mHintView.setVisibility(VISIBLE);
            int length = mCurrentTitle.length();
            mHintView.setText("已超出 "+ (length - 50) +" 个字");
            editHintParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            editHintParams.topMargin = 0;
        } else {
            mHintView.setVisibility(GONE);
        }
    }

    public void addTextWatcher(TextWatcher watcher) {
        mTitleEditView.addTextChangedListener(watcher);
    }

    public void notifyTextChange(String title) {
        for (int i = 0; i < iTitleEditChanges.size(); i ++) {
            iTitleEditChanges.get(i).onTextChange(title);
        }
    }

    public void registerTextChangeListener(ITitleEditChangeListener listener) {
        iTitleEditChanges.add(listener);
    }

}
