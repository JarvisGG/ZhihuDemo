package com.jarvis.zhihudemo.view1;

import android.content.Context;
import android.database.Observable;
import android.support.v4.os.TraceCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 11-27-2018
 */
public class TopicLabelLayout extends ViewGroup {

    private Adapter mAdapter;

    private AdapterDataObserver mAdapterDataObserver;

    private List<Integer> mLineHeight = new ArrayList<>();
    private List<Integer> mLineWidth = new ArrayList<>();
    private List<Integer> mLineCount = new ArrayList<>();

    public TopicLabelLayout(Context context) {
        super(context);
    }

    public TopicLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopicLabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;

        int lineCount = 0;

        int childCount = getChildCount();

        int lineWidth = 0, lineHight = 0;

        mLineHeight.clear();
        mLineWidth.clear();
        mLineCount.clear();

        for (int i = 0; i < childCount; i++) {

            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (lineWidth + childWidth > sizeWidth) {
                mLineHeight.add(lineHight);
                mLineWidth.add(lineWidth);
                mLineCount.add(lineCount);
                width = Math.max(lineWidth, width);
                height += lineHight;
                lineHight = 0;
                lineWidth = 0;
                lineCount = 0;
                i--;
                continue;
            }

            lineWidth += childWidth;
            lineHight = Math.max(lineHight, childHeight);
            lineCount++;
        }

        if (lineWidth > 0) {
            height += lineHight;
            width = Math.max(lineWidth, width);
            mLineCount.add(lineCount);
            mLineWidth.add(lineWidth);
            mLineHeight.add(lineHight);
        }

        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width,
                (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        int line = 0;
        int top = 0;
        int left = 0;
        int fillCount = 0;

        for (int i = 0; i < childCount; i ++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                fillCount++;
                continue;
            }

            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (line >= mLineCount.size()) {
                return;
            }

            if (i - fillCount < mLineCount.get(line)) {

                child.layout(
                        left + params.leftMargin,
                        top + params.topMargin,
                        left + childWidth - params.rightMargin,
                        top + childHeight - params.bottomMargin);

                left += childWidth;
            } else {
                top += mLineHeight.get(line);
                fillCount += mLineCount.get(line);
                line++;
                left = 0;
                i--;
            }
        }
    }

    public void setAdapterInternal(Adapter adapter) {
        if (mAdapterDataObserver == null) {
            mAdapterDataObserver = new LabelDataObserver();
        } else {
            adapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }
        adapter.registerAdapterDataObserver(mAdapterDataObserver);
        adapter.init(this);
        mAdapter = adapter;
    }

    public static class ViewHolder {


        public final View itemView;

        public int mPosition;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }
    }


    public abstract static class Adapter<VH extends ViewHolder, D extends Object> {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        public ViewGroup mParent;

        protected List<D> mData = new ArrayList<>();
        private final List<VH> mHolder = new ArrayList<>();

        private void init(ViewGroup parent) {
            mParent = parent;
            for (int i = 0; i < mData.size(); i++) {
                parent.addView(addHolder(i).itemView, i);
            }
        }

        public final VH createViewHolder(ViewGroup parent) {
            return onCreateViewHolder(parent);
        }

        public abstract VH onCreateViewHolder(ViewGroup parent);

        public final void bindViewHolder(VH holder, int position) {
            holder.mPosition = position;
            onBindViewHolder(holder, position);
        }

        public abstract void onBindViewHolder(VH holder, int position);

        public int getItemCount() {
            return mData.size();
        }

        private VH addHolder(int position) {
            VH holder = createViewHolder(mParent);
            bindViewHolder(holder, position);
            mHolder.add(position, holder);
            return holder;
        }

        public void addData(D data, int position) {
            mData.add(position, data);
        }

        public void removeData(int position) {
            mData.remove(position);
        }

        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

        public final void notifyItemRangeInserted(int positionStart, int itemCount) {
            addHolder(positionStart);
            for (int i = positionStart; i < mHolder.size(); i++) {
                mHolder.get(i).mPosition = i;
            }
            mObservable.notifyItemRangeInserted(positionStart, itemCount);
        }

        public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
            mHolder.remove(positionStart);
            mObservable.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    private class LabelDataObserver extends AdapterDataObserver {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            for (int i = 0; i < itemCount; i++) {
                ViewHolder holder = (ViewHolder) mAdapter.mHolder.get(positionStart + i);
                addView(holder.itemView, positionStart + i);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = 0; i < itemCount; i++) {
                removeViewAt(positionStart + i);
            }
        }
    }

    public abstract static class AdapterDataObserver {

        public void onItemRangeInserted(int positionStart, int itemCount) {

        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeInserted(positionStart, itemCount);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRangeRemoved(positionStart, itemCount);
            }
        }
    }
}
