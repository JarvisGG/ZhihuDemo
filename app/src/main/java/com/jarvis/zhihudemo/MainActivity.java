package com.jarvis.zhihudemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.avtivity.AndroidRDrawable;
import com.jarvis.zhihudemo.avtivity.AnswerNextTipActivity;
import com.jarvis.zhihudemo.avtivity.AnswerPagerActivity;
import com.jarvis.zhihudemo.avtivity.AnswerSkeletionActivity;
import com.jarvis.zhihudemo.avtivity.BottomSheetActivity;
import com.jarvis.zhihudemo.avtivity.ButtonActivity;
import com.jarvis.zhihudemo.avtivity.ClipViewActivity;
import com.jarvis.zhihudemo.avtivity.CommentRecyclerActivity;
import com.jarvis.zhihudemo.avtivity.ConfettiActivity;
import com.jarvis.zhihudemo.avtivity.CustomLayoutActivity;
import com.jarvis.zhihudemo.avtivity.CustomLayoutManager2Activity;
import com.jarvis.zhihudemo.avtivity.CustomLayoutMangerActivity;
import com.jarvis.zhihudemo.avtivity.EditTextActivity;
import com.jarvis.zhihudemo.avtivity.FloatDragViewActivity;
import com.jarvis.zhihudemo.avtivity.FloatViewActivity;
import com.jarvis.zhihudemo.avtivity.FragmentManagerActivity;
import com.jarvis.zhihudemo.avtivity.FragmentTestActivity;
import com.jarvis.zhihudemo.avtivity.GalleryActivity;
import com.jarvis.zhihudemo.avtivity.GalleryViewPagerActivity;
import com.jarvis.zhihudemo.avtivity.GlideCrossActivity;
import com.jarvis.zhihudemo.avtivity.HybridActivity;
import com.jarvis.zhihudemo.avtivity.InnerCardActivity;
import com.jarvis.zhihudemo.avtivity.MaterialActivity;
import com.jarvis.zhihudemo.avtivity.NestedScrollActivity;
import com.jarvis.zhihudemo.avtivity.NumberCountActivity;
import com.jarvis.zhihudemo.avtivity.OverScrollViewActivity;
import com.jarvis.zhihudemo.avtivity.PanelActivity;
import com.jarvis.zhihudemo.avtivity.ShareElementActivity;
import com.jarvis.zhihudemo.avtivity.TextViewActivity;
import com.jarvis.zhihudemo.avtivity.TipsHeaderRecyclerActivity;
import com.jarvis.zhihudemo.avtivity.ToolbarActivity;
import com.jarvis.zhihudemo.avtivity.TopicActivity;
import com.jarvis.zhihudemo.avtivity.TopicLabelActivity;
import com.jarvis.zhihudemo.avtivity.ViewModelActivity;
import com.jarvis.zhihudemo.avtivity.ViewPagerActivity;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    String json = "{\n" +
            "\"total\":3,\n" +
            "\"rows\":[abcdefg123,abcdefg456,abcdefg789]" +
            "}";

    @ViewInject(R.id.main_rv)
    private ZhihuRecyclerView recyclerView;
    Class[] demoClasses = new Class[]{
            FragmentTestActivity.class,
            GalleryActivity.class,
            FloatViewActivity.class,
            FloatDragViewActivity.class,
            AndroidRDrawable.class,
            GlideCrossActivity.class,
            HybridActivity.class,
            TextViewActivity.class,
            MaterialActivity.class,
            InnerCardActivity.class,
            ConfettiActivity.class,
            CustomLayoutMangerActivity.class,
            GalleryViewPagerActivity.class,
            AnswerSkeletionActivity.class,
            AnswerNextTipActivity.class,
            NestedScrollActivity.class,
            CustomLayoutActivity.class,
            ViewPagerActivity.class,
            TipsHeaderRecyclerActivity.class,
            CommentRecyclerActivity.class,
            FragmentManagerActivity.class,
            TopicActivity.class,
            AnswerPagerActivity.class,
            OverScrollViewActivity.class,
            ViewModelActivity.class,
            ToolbarActivity.class,
            ClipViewActivity.class,
            TopicLabelActivity.class,
            EditTextActivity.class,
            PanelActivity.class,
            BottomSheetActivity.class,
            NumberCountActivity.class,
            ShareElementActivity.class,
            ButtonActivity.class,
            CustomLayoutManager2Activity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SinglePresenterSelector presenterSelector = new SinglePresenterSelector(new InnerPresenter(this));
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
        recyclerView.setObjectAdapter(adapter);

        for (Class<? extends BaseActivity> ac : demoClasses) {
            adapter.add(new InnerData(ac));
        }
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
            View view = mInflater.inflate(R.layout.item_main, parent, false);
            return new InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
            final InnerData data = (InnerData) item;
            final InnerViewHolder holder = (InnerViewHolder) viewHolder;
            holder.tv.setText(data.title);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, data.target);
                    MainActivity.this.startActivity(intent);
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

    class InnerData {
        String title;
        Class<? extends BaseActivity> target;

        public InnerData(Class<? extends BaseActivity> t) {
            this.title = t.getSimpleName();
            this.target = t;
        }
    }
}
