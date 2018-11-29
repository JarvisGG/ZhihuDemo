package com.jarvis.zhihudemo.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jarvis.zhihudemo.R;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-23-2018
 */
public class ToolBarContainerView extends FrameLayout {

    private AppCompatTextView mTitle;
    private AppCompatTextView mSubTitle;

    private LinearLayout mInfoContainer;
    private FrameLayout mSearchContainer;

    private AppCompatTextView mWriteBtn;
    private ImageView mSearchIcon;

    private ValueAnimator mAnimator;

    public ToolBarContainerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ToolBarContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ToolBarContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mInfoContainer = new LinearLayout(getContext());
        mInfoContainer.setOrientation(LinearLayout.VERTICAL);
        LayoutParams infoParams = new LayoutParams(
                ExplosionUtils.dp2Px(198),
                ExplosionUtils.dp2Px(36)
        );
        infoParams.topMargin = ExplosionUtils.dp2Px(8 + 8);
        infoParams.bottomMargin = ExplosionUtils.dp2Px(8);

        mTitle = new AppCompatTextView(getContext());
        mTitle.setTextSize(14);
        mTitle.setText("三星手机中国不");
        TextPaint tp = mTitle.getPaint();
        tp.setFakeBoldText(true);
        LayoutParams titleParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ExplosionUtils.dp2Px(16)
        );
        mInfoContainer.addView(mTitle, titleParams);

        mSubTitle = new AppCompatTextView(getContext());
        mSubTitle.setTextSize(12);
        mSubTitle.setTextColor(getContext().getColor(R.color.BK06));
        mSubTitle.setText("1580 个回答");
        LayoutParams subParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ExplosionUtils.dp2Px(14)
        );
        subParams.topMargin = ExplosionUtils.dp2Px(4);
        mInfoContainer.addView(mSubTitle, subParams);

        addView(mInfoContainer, infoParams);
        mInfoContainer.setAlpha(0);






        mWriteBtn = new AppCompatTextView(getContext());
        mWriteBtn.setMaxLines(1);
        mWriteBtn.setGravity(Gravity.CENTER);
        mWriteBtn.setText("写回答");
        mWriteBtn.setTextColor(getContext().getColor(R.color.BL01));
        mWriteBtn.setTextSize(16);
        mWriteBtn.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        FrameLayout.LayoutParams writeParams = new FrameLayout.LayoutParams(
                ExplosionUtils.dp2Px(48), ExplosionUtils.dp2Px(48)
        );
        writeParams.gravity = Gravity.RIGHT;
        writeParams.topMargin = ExplosionUtils.dp2Px(8);

        addView(mWriteBtn, writeParams);
        mWriteBtn.setAlpha(0);





        mSearchContainer = new FrameLayout(getContext());
        mSearchIcon = new ImageView(getContext());
        mSearchIcon.setImageResource(R.drawable.ic_search);
        FrameLayout.LayoutParams searchParams = new FrameLayout.LayoutParams(
                ExplosionUtils.dp2Px(24), ExplosionUtils.dp2Px(24)
        );
        searchParams.gravity = Gravity.CENTER;
        mSearchContainer.addView(mSearchIcon, searchParams);

        FrameLayout.LayoutParams searchContainerParams = new LayoutParams(
                ExplosionUtils.dp2Px(48), ExplosionUtils.dp2Px(48)
        );
        searchContainerParams.gravity = Gravity.RIGHT;
//        searchContainerParams.topMargin = ExplosionUtils.dp2Px(8);
        addView(mSearchContainer, searchContainerParams);
        mSearchContainer.setAlpha(1);
    }

    public void excuteAnim(int start, int end) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.addUpdateListener(animation -> changeAnim((float)animation.getAnimatedValue()));
        mAnimator.setInterpolator(new DecelerateInterpolator(2));
        mAnimator.setDuration(300);
        mAnimator.start();
    }

    private void changeAnim(float value) {
        mWriteBtn.setAlpha(1 * value);
        mWriteBtn.setTranslationY(- ExplosionUtils.dp2Px(8) * value);
        mSearchContainer.setAlpha(1 - value);
        mSearchContainer.setTranslationY(ExplosionUtils.dp2Px(8) * value);


        mInfoContainer.setAlpha(1 * value);
        mInfoContainer.setTranslationY(- ExplosionUtils.dp2Px(8) * value);



//        ((View) this.getParent()).setTranslationZ(ExplosionUtils.dpToPixel(getContext(), (10 * value)));

        ViewCompat.setElevation((View) this.getParent(), ExplosionUtils.dpToPixel(getContext(), (10 * value)));
    }



}
