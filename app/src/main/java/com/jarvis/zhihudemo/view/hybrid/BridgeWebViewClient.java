package com.jarvis.zhihudemo.view.hybrid;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/22 下午5:54
 * @changeRecord [修改记录] <br/>
 */

public class BridgeWebViewClient extends WebViewClient {
    private BridgeWebView mWebView;

    public BridgeWebViewClient(BridgeWebView webView) {
        this.mWebView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (url.startsWith(BridgeUtils.YY_RETURN_DATA)) {
            mWebView.handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeUtils.YY_OVERRIDE_SCHEMA)) {
            mWebView.flushMessageQueue();
            return true;
        } else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (BridgeWebView.toLoadJs != null) {
            BridgeUtils.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);
        }
        //
        if (mWebView.getStartupMessage() != null) {
            for (Message m : mWebView.getStartupMessage()) {
                mWebView.dispatchMessage(m);
            }
            mWebView.setStartupMessage(null);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }
}
