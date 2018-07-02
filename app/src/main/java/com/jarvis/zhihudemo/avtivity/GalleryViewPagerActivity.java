package com.jarvis.zhihudemo.avtivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.MainActivity;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.fragment.TestFragmentAdapter;
import com.jarvis.zhihudemo.view.AnswerPagerActionPanelView;
import com.jarvis.zhihudemo.view.DirectionalViewPager;
import com.jarvis.zhihudemo.view.DragCommentView;
import com.jarvis.zhihudemo.view.GalleryRecyclerView;
import com.jarvis.zhihudemo.view.OverScrollLayout;
import com.jarvis.zhihudemo.view.SlidingUpPanelLayout;
import com.jarvis.zhihudemo.view.VerticalViewPager;
import com.jarvis.zhihudemo.view.VerticalViewPager2;
import com.jarvis.zhihudemo.view.widget.ViewPagerInter;
import com.jarvis.zhihudemo.view.ZHDirectionalViewPager;
import com.jarvis.zhihudemo.view.widget.AnswerScrollComponent;
import com.jarvis.zhihudemo.view.widget.DirectionViewPagerBoundView;
import com.jarvis.zhihudemo.view.widget.DirectionalViewPager1;
import com.jarvis.zhihudemo.view.widget.ViewPagerInfo;
import com.jarvis.zhihudemo.view.widget.ZHDragViewInfo;
import com.jarvis.zhihudemo.view.widget.ZHViewPagerInfo;
import com.jarvis.zhihudemo.widgets.AdUtils;
import com.jarvis.zhihudemo.widgets.ExplosionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static com.jarvis.zhihudemo.view.DirectionalViewPager.NEXT;
import static com.jarvis.zhihudemo.view.DirectionalViewPager.PRE;
import static com.jarvis.zhihudemo.view.VerticalViewPager2.SCROLL_STATE_DRAGGING;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/19 下午3:09
 * @changeRecord [修改记录] <br/>
 */

public class GalleryViewPagerActivity extends BaseActivity {

    private ViewPagerInter viewPager;

    private SlidingUpPanelLayout slidingUpPanelLayout;

    private ZhihuRecyclerView recyclerView;

    private AnswerScrollComponent scrollComponent;

    private FrameLayout comment;

    private FrameLayout mMainComment;

    private int dragx = 0, dragy = 0;

    private int currentItem = 0;

    private DirectionViewPagerBoundView topView;
    private DirectionViewPagerBoundView bottomView;

    private int originHeaderTop;

    private int originFooterTop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callery1);

        init();

    }

    public static int getStatusBarHeight(Context context) {
        final int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void init() {

        topView = findViewById(R.id.top);

        bottomView = findViewById(R.id.bottom);

        topView.post(() -> originHeaderTop = topView.getTop());

        bottomView.post(() -> originFooterTop = bottomView.getTop());

        mMainComment = findViewById(R.id.main_co);

        viewPager = findViewById(R.id.main_pager);
        scrollComponent = new AnswerScrollComponent();
        ZHViewPagerInfo pagerInfo = new ZHViewPagerInfo.Builder()
                .setOffsetScreenPageLimit(10)
                .setTopLimitOffset(0.114f)
                .setBottomLimitOffset(0.15f)
//                .setInterpolator(new OvershootInterpolator(0.8f))
                .setInterpolator(new DecelerateInterpolator(2))
                .setTopFator(0.45f)
                .setBottomFactor(0.8f)
                .setTime(300)
                .setNextOffset(0)
                .setPreOffset(0)
                .builder();
//        viewPager.setAdapter(new TestFragmentAdapter(getSupportFragmentManager()));
        viewPager.setAdapter(new MyPagerAdapter(createViewList()));
        viewPager.bind(pagerInfo);
//        viewPager.setOnPageChangeListener(new ViewPagerInter.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels, int direction) {
//
//                Log.e("getLocationStatusBar ->", getStatusBarHeight(GalleryViewPagerActivity.this) + "");
//                View view1 = viewPager.findViewWithTag(viewPager.getCurrentItem());
//                int[] rect = new int[2];
//                view1.getLocationOnScreen(rect);
//                Log.e("getLocationOnScreen -> ", rect[0] + " - " + rect[1]);
//                view1.getLocationInWindow(rect);
//                Log.e("getLocationInWindow -> ", rect[0] + " - " + rect[1]);
//
//
//                Log.e("onPageScrolled -----> ", "position: " + position + " positionOffset: " + positionOffset
//                + " positionOffsetPixels: " + positionOffsetPixels + " direction: " + (direction == PRE ? "PRE" : "NEXT"));
//                if (direction == NEXT) {
//                    if (positionOffset > 0.5 && viewPager.getCurrentItem() > 0) {
//                        View view = viewPager.findViewWithTag(viewPager.getCurrentItem() - 1);
//                        int offsetY = (int) (view.getMeasuredHeight() * (1 - positionOffset));
//                        if (offsetY > ExplosionUtils.dp2Px(40)) {
//                            int a = ExplosionUtils.dp2Px(60) - offsetY;
//                            if (a > 0) {
//                                float res = 180 * ((float) a / (float) ExplosionUtils.dp2Px(40));
//                                DirectionViewPagerBoundView boundView = ((DirectionViewPagerBoundView) view.findViewById(R.id.bottom_bound));
//                                if (boundView != null) {
//                                    if (boundView.getVisibility() == GONE) {
//                                        boundView.setVisibility(View.VISIBLE);
//                                    }
//                                    boundView.changeArrow(-res);
//                                }
//                                ViewGroup containerView = view.findViewById(R.id.container_view);
//                                if (containerView != null && containerView.getVisibility() == VISIBLE) {
//                                    containerView.setVisibility(View.INVISIBLE);
//                                }
//                            }
//                        }
//                    } else if (positionOffset < 0.5 && viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
//                        View view = viewPager.findViewWithTag(viewPager.getCurrentItem() + 1);
//                        int offsetY = (int) (view.getMeasuredHeight() * positionOffset);
//                        if (offsetY < ExplosionUtils.dp2Px(100)) {
//                            int a = ExplosionUtils.dp2Px(120) - offsetY;
//                            if (a > 0) {
//                                float res = 360 * ((float) a / (float) ExplosionUtils.dp2Px(40));
//                                DirectionViewPagerBoundView boundView = ((DirectionViewPagerBoundView) view.findViewById(R.id.top_bound));
//                                if (boundView != null) {
//                                    if (boundView.getVisibility() == GONE) {
//                                        boundView.setVisibility(View.VISIBLE);
//                                    }
//                                    boundView.changeArrow(res);
//                                }
//                                ViewGroup containerView = view.findViewById(R.id.container_view);
//                                if (containerView != null && containerView.getVisibility() == VISIBLE) {
//                                    containerView.setVisibility(View.INVISIBLE);
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    if (positionOffset > 0.5 && viewPager.getCurrentItem() > 0) {
//                        View view = viewPager.findViewWithTag(viewPager.getCurrentItem() - 1);
//                        int offsetY = (int) (view.getMeasuredHeight() * (1 - positionOffset));
//                        if (offsetY > ExplosionUtils.dp2Px(40)) {
//                            int a = ExplosionUtils.dp2Px(60) - offsetY;
//                            if (a > 0) {
//                                float res = 360 * ((float) a / (float) ExplosionUtils.dp2Px(40));
//                                DirectionViewPagerBoundView boundView = ((DirectionViewPagerBoundView) view.findViewById(R.id.bottom_bound));
//                                if (boundView != null) {
//                                    if (boundView.getVisibility() == GONE) {
//                                        boundView.setVisibility(View.VISIBLE);
//                                    }
//                                    boundView.changeArrow(res);
//                                }
//                                ViewGroup containerView = view.findViewById(R.id.container_view);
//                                if (containerView != null && containerView.getVisibility() == VISIBLE) {
//                                    containerView.setVisibility(View.INVISIBLE);
//                                }
//                            }
//                        }
//                    } else if (positionOffset < 0.5 && viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
//                        View view = viewPager.findViewWithTag(viewPager.getCurrentItem() + 1);
//                        int offsetY = (int) (view.getMeasuredHeight() * positionOffset);
//                        if (offsetY < ExplosionUtils.dp2Px(100)) {
//                            int a = ExplosionUtils.dp2Px(120) - offsetY;
//                            if (a > 0) {
//                                float res = 360 * ((float) a / (float) ExplosionUtils.dp2Px(40));
//                                DirectionViewPagerBoundView boundView = ((DirectionViewPagerBoundView) view.findViewById(R.id.top_bound));
//                                if (boundView != null) {
//                                    if (boundView.getVisibility() == GONE) {
//                                        boundView.setVisibility(View.VISIBLE);
//                                    }
//                                    boundView.changeArrow(-res);
//                                }
//                                ViewGroup containerView = view.findViewById(R.id.container_view);
//                                if (containerView != null && containerView.getVisibility() == VISIBLE) {
//                                    containerView.setVisibility(View.INVISIBLE);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                if (direction == NEXT) {
//
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.e("onPageScrollState --> ", "state: " + state);
//                View view = viewPager.findViewWithTag(viewPager.getCurrentItem());
//                view.findViewById(R.id.bottom_bound).setVisibility(GONE);
//                view.findViewById(R.id.top_bound).setVisibility(GONE);
//                view.findViewById(R.id.container_view).setVisibility(VISIBLE);
//            }
//        });
//        viewPager.addDirectionOnPageChangeListener(new DirectionalViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels, int duration, boolean isScrolling) {

//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        viewPager.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Log.e("onScrollChange ----> ", "scrollY: " + i1 + " oldScrollY: " + i3);
            }
        });
        viewPager.setOnPageChangeListener(new VerticalViewPager2.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels, int direction) {
                Log.e("onPageScrolled -----> ", "position: " + position + " positionOffset: " + positionOffset
                        + " positionOffsetPixels: " + positionOffsetPixels + " direction: " + (direction == PRE ? "PRE" : "NEXT"));
//                if (direction == PRE) {
//                    if (positionOffset < 0.5) {
//                        bottomView.layout(
//                                bottomView.getLeft(),
//                                originFooterTop - positionOffsetPixels,
//                                bottomView.getLeft() + bottomView.getMeasuredWidth(),
//                                originFooterTop + bottomView.getMeasuredHeight() - positionOffsetPixels
//                        );
//                    } else {
//                        topView.layout(
//                                topView.getLeft(),
//                                originHeaderTop + viewPager.getMeasuredHeight() - positionOffsetPixels,
//                                topView.getLeft() + bottomView.getMeasuredWidth(),
//                                originHeaderTop + viewPager.getMeasuredHeight() - positionOffsetPixels + topView.getMeasuredHeight()
//                        );
//                    }
//                } else {
//                    if (positionOffset > 0.5) {
//                        topView.layout(
//                                topView.getLeft(),
//                                originHeaderTop + viewPager.getMeasuredHeight() + positionOffsetPixels,
//                                topView.getLeft() + bottomView.getMeasuredWidth(),
//                                originHeaderTop + viewPager.getMeasuredHeight() + positionOffsetPixels + topView.getMeasuredHeight()
//                        );
//                    } else {
//                        bottomView.layout(
//                                bottomView.getLeft(),
//                                originFooterTop - positionOffsetPixels,
//                                bottomView.getLeft() + bottomView.getMeasuredWidth(),
//                                originFooterTop + bottomView.getMeasuredHeight() - positionOffsetPixels
//                        );
//                    }
//                }
//                if (direction == NEXT) {
//                    if (positionOffset > 0.5 && viewPager.getCurrentItem() > 0) {
//                        Fragment fragment = ((AnswerPagerAdapter)viewPager.getAdapter()).findFragment(viewPager.getCurrentItem() - 1);
//                        if (fragment == null) {
//                            return;
//                        }
//                        View view = fragment.getView();
//                        if (view == null) {
//                            return;
//                        }
//                        int offsetY = (int) (view.getMeasuredHeight() * (1 - positionOffset));
//                        DirectionViewPagerBoundView boundView = ((DirectionViewPagerBoundView) view.findViewById(R.id.bottom_bound));
//                        if (boundView != null && boundView.getVisibility() == GONE) {
//                            boundView.setVisibility(View.VISIBLE);
//                        }
//                        ViewGroup containerView = view.findViewById(R.id.web_frame);
//                        if (containerView != null && containerView.getVisibility() == VISIBLE) {
//                            containerView.setVisibility(View.INVISIBLE);
//                        }
//                        if (offsetY > ExplosionUtils.dp2Px(40)) {
//                            int a = ExplosionUtils.dp2Px(60) - offsetY;
//                            if (a > 0) {
//                                float res = 180 * ((float) a / (float) ExplosionUtils.dp2Px(40));
//                                if (boundView != null) {
//                                    boundView.changeArrow(-res);
//                                }
//                            }
//                        }
//                    } else if (positionOffset < 0.5 && viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
//                        Fragment fragment = ((AnswerPagerAdapter)viewPager.getAdapter()).findFragment(viewPager.getCurrentItem() + 1);
//                        if (fragment == null) {
//                            return;
//                        }
//                        View view = fragment.getView();
//                        if (view == null) {
//                            return;
//                        }
//                        int offsetY = (int) (view.getMeasuredHeight() * positionOffset);
//                        DirectionViewPagerBoundView boundView = ((DirectionViewPagerBoundView) view.findViewById(R.id.top_bound));
//                        if (boundView != null && boundView.getVisibility() == GONE) {
//                            boundView.setVisibility(View.VISIBLE);
//                        }
//                        ViewGroup containerView = view.findViewById(R.id.web_frame);
//                        if (containerView != null && containerView.getVisibility() == VISIBLE) {
//                            containerView.setVisibility(View.INVISIBLE);
//                        }
//                        if (offsetY < ExplosionUtils.dp2Px(100)) {
//                            int a = ExplosionUtils.dp2Px(120) - offsetY;
//                            if (a > 0) {
//                                float res = 360 * ((float) a / (float) ExplosionUtils.dp2Px(40));
//                                if (boundView != null) {
//                                    boundView.changeArrow(res);
//                                }
//                            }
//                        }
//                    }
//                } else if (direction == PRE) {
//                    if (positionOffset > 0.5 && viewPager.getCurrentItem() > 0) {
//                        Fragment fragment = ((AnswerPagerAdapter)viewPager.getAdapter()).findFragment(viewPager.getCurrentItem() - 1);
//                        if (fragment == null) {
//                            return;
//                        }
//                        View view = fragment.getView();
//                        if (view == null) {
//                            return;
//                        }
//                        int offsetY = (int) (view.getMeasuredHeight() * (1 - positionOffset));
//                        DirectionViewPagerBoundView boundView = ((DirectionViewPagerBoundView) view.findViewById(R.id.bottom_bound));
//                        if (boundView != null && boundView.getVisibility() == GONE) {
//                            boundView.setVisibility(View.VISIBLE);
//                        }
//                        ViewGroup containerView = view.findViewById(R.id.web_frame);
//                        if (containerView != null && containerView.getVisibility() == VISIBLE) {
//                            containerView.setVisibility(View.INVISIBLE);
//                        }
//                        if (offsetY > ExplosionUtils.dp2Px(40)) {
//                            int a = ExplosionUtils.dp2Px(60) - offsetY;
//                            if (a > 0) {
//                                float res = 360 * ((float) a / (float) ExplosionUtils.dp2Px(40));
//                                if (boundView != null) {
//                                    boundView.changeArrow(res);
//                                }
//                            }
//                        }
//                    } else if (positionOffset < 0.5 && viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
//                        Fragment fragment = ((AnswerPagerAdapter)viewPager.getAdapter()).findFragment(viewPager.getCurrentItem() + 1);
//                        if (fragment == null) {
//                            return;
//                        }
//                        View view = fragment.getView();
//                        if (view == null) {
//                            return;
//                        }
//                        int offsetY = (int) (view.getMeasuredHeight() * positionOffset);
//                        DirectionViewPagerBoundView boundView = ((DirectionViewPagerBoundView) view.findViewById(R.id.top_bound));
//                        if (boundView != null && boundView.getVisibility() == GONE) {
//                            boundView.setVisibility(View.VISIBLE);
//                        }
//                        ViewGroup containerView = view.findViewById(R.id.web_frame);
//                        if (containerView != null && containerView.getVisibility() == VISIBLE) {
//                            containerView.setVisibility(View.INVISIBLE);
//                        }
//                        if (offsetY < ExplosionUtils.dp2Px(100)) {
//                            int a = ExplosionUtils.dp2Px(120) - offsetY;
//                            if (a > 0) {
//                                float res = 360 * ((float) a / (float) ExplosionUtils.dp2Px(40));
//                                if (boundView != null) {
//                                    boundView.changeArrow(-res);
//                                }
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onPageSelected(int position) {
                bottomView.setVisibility(GONE);
                topView.setVisibility(GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if (state == SCROLL_STATE_DRAGGING) {
//                    bottomView.setVisibility(VISIBLE);
//                    topView.setVisibility(VISIBLE);
//                }
            }
        });

//        viewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                Log.e("onLayoutChange ----> ", "top : " + top + "oldTop : " + oldTop);
//                int boundOffset = top;
//                if (boundOffset > 0) {
//                    topView.layout(
//                            topView.getLeft(),
//                            originHeaderTop + boundOffset,
//                            topView.getLeft() + bottomView.getMeasuredWidth(),
//                            originHeaderTop + boundOffset + topView.getMeasuredHeight());
//                } else if (boundOffset < 0) {
//                    bottomView.layout(
//                            bottomView.getLeft(),
//                            originFooterTop+ boundOffset + AdUtils.getScreenSizeY(GalleryViewPagerActivity.this),
//                            bottomView.getLeft() + bottomView.getMeasuredWidth(),
//                            originFooterTop + bottomView.getMeasuredHeight()+ boundOffset + AdUtils.getScreenSizeY(GalleryViewPagerActivity.this)
//                    );
//                }
//            }
//        });

        viewPager.addOnBoundDragListener(new ViewPagerInter.BoundDragListener() {
            @Override
            public void onBoundDragCallback(int boundOffset) {
                Log.e("boundOffset ----> ", "" +boundOffset);
                if (boundOffset > 0) {
                    topView.layout(
                            topView.getLeft(),
                            originHeaderTop + boundOffset,
                            topView.getLeft() + bottomView.getMeasuredWidth(),
                            originHeaderTop + boundOffset + topView.getMeasuredHeight());
                } else if (boundOffset < 0) {
                    bottomView.layout(
                            bottomView.getLeft(),
                            originFooterTop+ boundOffset,
                            bottomView.getLeft() + bottomView.getMeasuredWidth(),
                            originFooterTop + bottomView.getMeasuredHeight()+ boundOffset
                    );
                }
            }
        });

        AnswerPagerActionPanelView pagerActionPanelView = findViewById(R.id.main_panel);

        slidingUpPanelLayout = findViewById(R.id.main_drag);

//        scrollComponent.init(pagerInfo, viewPager, pagerActionPanelView);

        recyclerView = findViewById(R.id.main_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SinglePresenterSelector presenterSelector = new SinglePresenterSelector(new InnerPresenter(this));
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
        recyclerView.setObjectAdapter(adapter);

        for (int i = 0 ; i < 4; i++) {
            adapter.add(new InnerData(i+""));
        }
        final Window window = getWindow();
        int oririnColor = window.getStatusBarColor();

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("slidingUpPanelLayout", "onPanelSlide, offset " + slideOffset);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("slidingUpPanelLayout", "onPanelStateChanged " + newState);
            }

            @Override
            public void onPanelLayerColor(int color) {

                Log.i("slidingUpPanelLayout", "onPanelSlide, color " + color);
                Log.i("slidingUpPanelLayout", "onPanelSlide, originColor " + oririnColor);

                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(color + oririnColor);

                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("普通的对话框的标题");
        builder.setMessage("这是一个普通的对话框的内容");
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();

        View container = findViewById(android.R.id.content);

        comment = findViewById(R.id.comment);
        comment.setOnClickListener(view -> {
            if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
//            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            lp.alpha = 0.5f;
//            getWindow().setAttributes(lp);
//            final Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        });

    }

    private void changeFragmentAppViewState(int position, float state) {
        FragmentPagerAdapter pagerAdapter = (FragmentPagerAdapter)viewPager.getAdapter();
        if (pagerAdapter == null) {
            return;
        }
        android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) pagerAdapter.instantiateItem(viewPager, position);
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }
        view.setAlpha(state);
    }

    private void excuteAnim(boolean show) {

    }

    static class MyPagerAdapter extends PagerAdapter {

        private List<View> mViewList;

        public MyPagerAdapter(List<View> list) {
            mViewList = list;
        }

        public Object getItem(int position) {
            return mViewList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return mViewList.indexOf(object);
        }

        @Override
        public int getCount() {
            return mViewList == null ? 0 : mViewList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position), 0);
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private List<View> createViewList() {
        List<View> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int ranColor = 0xff000000 | random.nextInt(0x00ffffff);

            View view = LayoutInflater.from(this).inflate(R.layout.pager_view, null);
//            view.findViewById(R.id.container_view).setBackgroundColor(ranColor);
//            TextView textView = (TextView) view.findViewById(R.id.text1);
//            textView.setText("ViewPager. \t\t" + i);
//            textView.setTextColor(Color.WHITE);
            view.setTag(i);
            list.add(view);
        }
        return list;
    }


    class InnerPresenter extends Presenter {

        private Context mContext;
        private LayoutInflater mInflater;

        public InnerPresenter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View view = mInflater.inflate(R.layout.item_float_view, parent, false);
            return new InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
            final InnerData data = (InnerData) item;
            final InnerViewHolder holder = (InnerViewHolder) viewHolder;
            if (position == 10) {
                View child = viewHolder.view;
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.height = ExplosionUtils.dp2Px(180);
            }
            holder.tv.setText(data.title);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

        @Override
        public void onUnBindViewHolder(ViewHolder viewHolder) {

        }

        class InnerViewHolder extends ViewHolder {

            public TextView tv;
            public InnerViewHolder(View view) {
                super(view);
                tv = view.findViewById(R.id.main_tv);
            }
        }


    }

    static class InnerData {
        String title;

        public InnerData(String text) {
            this.title = text;
        }
    }


}
