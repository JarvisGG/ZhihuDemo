package com.jarvis.zhihudemo.widgets.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yyf @ Zhihu Inc.
 * @since 05-28-2018
 */
public class ObjectAdapter extends RecyclerView.Adapter<ObjectAdapter.EditInnerViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private AdapterListener mAdapterListener;
    private IOnbindOperator mIOnbindOperator;
    private Map<Class<?>, Map<Class<? extends EditInnerViewHolder>, Integer>> mHolderMapper;
    private List<Class> mDataMapper;

    private List mDatas;


    private ObjectAdapter(Context context) {
        this.mContext = context;
        this.mDatas = new ArrayList();
        this.mInflater = LayoutInflater.from(context);
        this.mHolderMapper = new ArrayMap<>();
        this.mDataMapper = new ArrayList<>();
        this.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mAdapterListener.onChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                mAdapterListener.onItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                mAdapterListener.onItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mAdapterListener.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                mAdapterListener.onItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                mAdapterListener.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        });
    }

    @Override
    public EditInnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAdapterListener.onCreateHolder(parent);
        Class<? extends EditInnerViewHolder> holder = null;
        int layoutRes = -1;
        HashMap<Class<? extends EditInnerViewHolder>, Integer> innerMapper = (HashMap<Class<? extends EditInnerViewHolder>, Integer>) mHolderMapper.get(mDataMapper.get(viewType));
        for (Map.Entry<Class<? extends EditInnerViewHolder>, Integer> entry : innerMapper.entrySet()) {
            holder = entry.getKey();
            layoutRes = entry.getValue();
        }
        View view = mInflater.inflate(layoutRes, parent,false);

        try {
            return holder.getDeclaredConstructor(View.class).newInstance(view);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onBindViewHolder(EditInnerViewHolder holder, int position) {
        holder.excuteBindData(mDatas.get(position));
        mIOnbindOperator.onBind(holder, mDatas.get(position), position);
        mAdapterListener.onBind(holder, mDatas.get(position), position);
    }

    @Override
    public void onViewRecycled(EditInnerViewHolder holder) {
        super.onViewRecycled(holder);
        mAdapterListener.onUnBind(holder);
    }

    @Override
    public void onViewAttachedToWindow(EditInnerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        mAdapterListener.onAttachedToWindow(holder);

    }

    @Override
    public void onViewDetachedFromWindow(EditInnerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mAdapterListener.onDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return this.mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataMapper.indexOf(mDatas.get(position).getClass());
    }

    public static ObjectAdapter create(Context context) {
        return new ObjectAdapter(context);
    }

    public ObjectAdapter addLayer(Class data, Class vh, @LayoutRes int res) {
        if (!mDataMapper.contains(data)) {
            mDataMapper.add(data);
            mHolderMapper.put(data, new HashMap<Class<? extends EditInnerViewHolder>, Integer>() {{ put(vh, res); }});
        }
        return this;
    }

    public ObjectAdapter registerObserver(AdapterListener adapterListener) {
        this.mAdapterListener = adapterListener;
        return this;
    }

    public ObjectAdapter bindOperator(IOnbindOperator iOnbindOperator) {
        this.mIOnbindOperator = iOnbindOperator;
        return this;
    }

    public ObjectAdapter bindList(List datas) {
        this.mDatas = datas;
        return this;
    }

    public ObjectAdapter build() {
        return this;
    }

    public List getDatas() {
        return mDatas;
    }

    public <T extends Object> void add(T data) {
        mDatas.add(data);
        this.notifyItemInserted(mDatas.size());
//        this.notifyItemRangeChanged(mDatas.size() - 1, 1);
    }

    public <T extends Object> void add(T data, int pos) {
        mDatas.add(pos, data);
        this.notifyItemInserted(pos);
//        this.notifyItemRangeChanged(pos, mDatas.size() - pos);
    }

    public <T extends Object> void addAll(List<T> datas) {
        if (datas != null && !datas.isEmpty()){
            int oldSize = mDatas.size();
            mDatas.addAll(datas);
            notifyItemRangeInserted(oldSize, datas.size());
        }
    }

    public <T extends Object> void addAll(List<T> datas, int pos) {
        mDatas.addAll(pos, datas);
        this.notifyItemRangeInserted(pos, datas.size());
    }

    public void remove(int pos) {
        mDatas.remove(pos);
        this.notifyItemRemoved(pos);
//        this.notifyItemRangeChanged(pos, mDatas.size() - pos);
    }

    public void removeAll() {
        mDatas.clear();
        this.notifyDataSetChanged();
    }

    public int getSameModel(Class model) {
        int count = 0;
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getClass() == model) {
                count++;
            }
        }
        return count;
    }

    protected static void cancelAnimationsRecursive(View view) {
        if (view != null && view.hasTransientState()) {
            view.animate().cancel();
            if (view instanceof ViewGroup) {
                final int count = ((ViewGroup) view).getChildCount();
                for (int i = 0; view.hasTransientState() && i < count; i++) {
                    cancelAnimationsRecursive(((ViewGroup) view).getChildAt(i));
                }
            }
        }
    }

    public interface IOnbindOperator {
        public void onBind(EditInnerViewHolder holder, Object data, int position);
    }


    public static class AdapterListener {

        public void onCreateHolder(ViewGroup parent) {

        }

        public void onBind(EditInnerViewHolder holder, Object data, int position) {

        }

        public void onUnBind(EditInnerViewHolder holder) {

        }

        public void onAttachedToWindow(EditInnerViewHolder holder) {

        }

        public void onDetachedFromWindow(EditInnerViewHolder holder) {

        }

        public void onChanged() {
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        }
    }

    public static abstract class EditInnerViewHolder<T> extends RecyclerView.ViewHolder {

        public EditInnerViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void excuteBindData(T data);

        public abstract void bindOperator();
    }


}
