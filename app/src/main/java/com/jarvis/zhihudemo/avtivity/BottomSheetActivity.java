package com.jarvis.zhihudemo.avtivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view1.IndicatorLineView;
import com.jarvis.zhihudemo.view1.NestedTouchScrollingLayout;
import com.jarvis.zhihudemo.widgets.DisplayUtils;
import com.jarvis.zhihudemo.widgets.utils.BlurUtils;

/**
 * @author Jarvis.
 * @since 12-04-2018
 */
public class BottomSheetActivity extends BaseActivity {

    private int mInnerItemsCount = 30;

    public static int mHalfWindowHeight = 400; // dp

    public static int mVelocityYBound = 1300;

    private RecyclerView mContainerRecycler;

    private NestedTouchScrollingLayout mNestedTouchScrollingLayout;

    private IndicatorLineView mIndicatorLineView;

    private FrameLayout mContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);
        ImageView rootView = findViewById(R.id.root_view);

        findViewById(R.id.btn_open).setOnClickListener(view -> {

            rootView.destroyDrawingCache();
            rootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);
            rootView.setImageBitmap(BlurUtils.$
                    .bitmap(bitmap)
                    .radius(20)
                    .blur());

            mNestedTouchScrollingLayout.expand();
        });

        mContainer = findViewById(R.id.container);
        mContainer.setClipToOutline(true);

        mIndicatorLineView = findViewById(R.id.indicator);

        mContainerRecycler = findViewById(R.id.container_rv);
        mContainerRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mContainerRecycler.setAdapter(new InnerAdapter(this, 0x9966CC));
        mContainerRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 30;
            }
        });
        mContainerRecycler.post(() -> mContainer.setOutlineProvider(new ShadowViewOutlineProvider()));

        mNestedTouchScrollingLayout = findViewById(R.id.wrapper);
        mNestedTouchScrollingLayout.registerNestScrollChildCallback(new NestedTouchScrollingLayout.INestChildScrollChange() {
            @Override
            public void onNestChildScrollChange(float deltaY) {

            }

            @Override
            public void onNestChildScrollRelease(final float deltaY, final int velocityY) {
                int totalYRange = mNestedTouchScrollingLayout.getMeasuredHeight();

                int helfLimit = (totalYRange - DisplayUtils.dpToPixel(BottomSheetActivity.this, mHalfWindowHeight)) / 2;

                int hideLimit = totalYRange - DisplayUtils.dpToPixel(BottomSheetActivity.this, mHalfWindowHeight) / 2;

                int helfHeight = totalYRange - DisplayUtils.dpToPixel(BottomSheetActivity.this, mHalfWindowHeight);

                if (velocityY > mVelocityYBound && velocityY > 0) {
                    if (Math.abs(deltaY) > helfHeight) {
                        mNestedTouchScrollingLayout.hiden(() -> rootView.setImageResource(R.drawable.bg_main_1));
                    } else {
                        mNestedTouchScrollingLayout.peek(mNestedTouchScrollingLayout.getMeasuredHeight() - DisplayUtils.dpToPixel(BottomSheetActivity.this,400));
                    }
                } else if (velocityY < -mVelocityYBound && velocityY < 0) {
                    if (Math.abs(deltaY) < helfHeight) {
                        mNestedTouchScrollingLayout.expand();
                    } else {
                        mNestedTouchScrollingLayout.peek(mNestedTouchScrollingLayout.getMeasuredHeight() - DisplayUtils.dpToPixel(BottomSheetActivity.this,400));
                    }
                } else {
                    if (Math.abs(deltaY) > hideLimit) {
                        mNestedTouchScrollingLayout.hiden(() -> rootView.setImageResource(R.drawable.bg_main_1));
                    } else if (Math.abs(deltaY) > helfLimit) {
                        mNestedTouchScrollingLayout.peek(mNestedTouchScrollingLayout.getMeasuredHeight() - DisplayUtils.dpToPixel(BottomSheetActivity.this, 400));
                    } else {
                        mNestedTouchScrollingLayout.expand();
                    }
                }
            }

            @Override
            public void onFingerUp(float velocityY) {
                mIndicatorLineView.updateVelocity(1700);
            }

            @Override
            public void onNestChildHorizationScroll(MotionEvent event, float deltaX, float deltaY) {

            }
        });

        mNestedTouchScrollingLayout.setSheetDirection(NestedTouchScrollingLayout.SheetDirection.BOTTOM);
        mNestedTouchScrollingLayout.post(() -> mNestedTouchScrollingLayout.recover(mNestedTouchScrollingLayout.getMeasuredHeight(), null, 0));
    }

    public class InnerAdapter extends RecyclerView.Adapter<InnerViewHolder> {

        private Context mContext;
        private LayoutInflater mInflater;
        private @ColorInt int mBgColor;

        public InnerAdapter(Context context, @ColorInt int color) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
            mBgColor = color;
        }

        @NonNull
        @Override
        public InnerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.main_item, viewGroup, false);
            return new InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InnerViewHolder innerViewHolder, int i) {
            innerViewHolder.tv.setText("Jarvis ----> " + i);
        }

        @Override
        public int getItemCount() {
            return mInnerItemsCount;
        }
    }

    public class InnerViewHolder extends RecyclerView.ViewHolder {

        public TextView tv;

        public InnerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.main_tv);
        }
    }

    public class ShadowViewOutlineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            int topMargin = 20;
            Rect selfRect = new Rect(0, 0, rect.right - rect.left, rect.bottom - rect.top + topMargin);
            outline.setRoundRect(selfRect, 30);
        }
    }
}