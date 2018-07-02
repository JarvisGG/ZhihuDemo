package com.jarvis.zhihudemo.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.jarvis.zhihudemo.R;

/**
 * @author yyf @ Zhihu Inc.
 * @since 06-26-2018
 */
public class WebviewFragment extends Fragment {

    private WebView mContainer;
    private String mUrl;
    public static final String URL = "url";

    public static final WebviewFragment build(Context context, String url) {
        WebviewFragment f = new WebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString(URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View containter = inflater.inflate(R.layout.fragment_webview, container, true);
        return containter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContainer = view.findViewById(R.id.container);
        mContainer.loadUrl(mUrl);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
