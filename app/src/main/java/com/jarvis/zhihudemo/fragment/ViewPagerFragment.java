package com.jarvis.zhihudemo.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.jarvis.library.widget.ArrayObjectAdapter;
import com.jarvis.library.widget.Presenter;
import com.jarvis.library.widget.SinglePresenterSelector;
import com.jarvis.library.widget.ZhihuRecyclerView;
import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.view1.ScrollRecyclerView;

import java.util.Random;

import jp.wasabeef.blurry.Blurry;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/4/2 下午2:47
 * @changeRecord [修改记录] <br/>
 */

public class ViewPagerFragment extends Fragment implements ScrollRecyclerView.OnBoundFlingListener {

    public static final String URL = "url";
    public static final String POSITION = "position";

    private Bundle arg;

    private WebView mWebView;

    private ScrollRecyclerView mRecycler;

    private int ranColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arg = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        Random random = new Random();
        ranColor = 0xff000000 | random.nextInt(0x00ffffff);
//        view.setBackgroundColor(ranColor);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = view.findViewById(R.id.web_view);
//        mRecycler = view.findViewById(R.id.recycler_v);
//
//        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
//        SinglePresenterSelector presenterSelector = new SinglePresenterSelector(new InnerPresenter(getContext()));
//        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
//        mRecycler.setObjectAdapter(adapter);
//        mRecycler.setNestedScrollingEnabled(true);
//        mRecycler.setBoundFlingListener(this);
//
//        for (int i = 0 ; i < 20; i++) {
//            adapter.add(new InnerData(i+"", arg.getInt(POSITION)));
//        }
        initWebSettings();
        initWebViewClient();

        mWebView.loadUrl(arg.getString(URL));
    }

    private void initWebSettings() {
        WebSettings settings = this.mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.supportMultipleWindows();
        settings.setSupportMultipleWindows(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(this.mWebView.getContext().getCacheDir().getAbsolutePath());
        settings.setAllowFileAccess(true);
        if(Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }

        settings.setDefaultTextEncodingName("UTF-8");
    }

    private void initWebViewClient() {
        this.mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if(!"about:blank".equals(url)) {
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if("about:blank".equals(url)) {
                } else {
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);

            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }
        });
    }

    @Override
    public void onFlingStarted(int velocityY) {

    }

    @Override
    public void onFlingStopped(int velocityY) {

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
            return new InnerPresenter.InnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item, int position) {
            final InnerData data = (InnerData) item;
            final InnerPresenter.InnerViewHolder holder = (InnerPresenter.InnerViewHolder) viewHolder;
            holder.tv.setText(data.title);
            holder.view.setBackgroundColor(ranColor);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Blurry.with(getContext())
                            .radius(10)
                            .sampling(8)
                            .async()
                            .capture(holder.iv)
                            .into(holder.iv);
                }
            });

        }

        @Override
        public void onUnBindViewHolder(ViewHolder viewHolder) {

        }

        class InnerViewHolder extends ViewHolder {

            public TextView tv;
            public ImageView iv;
            public InnerViewHolder(View view) {
                super(view);
                tv = view.findViewById(R.id.main_tv);
                iv = view.findViewById(R.id.main_iv);
            }
        }
    }

    class InnerData {
        String title;
        int position;

        public InnerData(String text, int positiom) {
            this.title = text;
            this.position = positiom;
        }
    }

    public static ViewPagerFragment newInstance(Bundle args) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
