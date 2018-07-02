package com.jarvis.zhihudemo.avtivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.view.hybrid.BridgeHandler;
import com.jarvis.zhihudemo.view.hybrid.BridgeWebView;
import com.jarvis.zhihudemo.view.hybrid.CallBackFunction;
import com.jarvis.zhihudemo.view.hybrid.DefaultHandler;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/22 下午4:32
 * @changeRecord [修改记录] <br/>
 */

@ContentView(R.layout.activity_hybrid)
public class HybridActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "HybridActivity";
    @ViewInject(R.id.webView)
    private BridgeWebView webView;
    @ViewInject(R.id.et)
    private EditText editText;
    @ViewInject(R.id.button)
    private Button btn;

    private int RESULT_CODE = 0;

    private ValueCallback<Uri> mUploadMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        webView.setDefaultHandler(new DefaultHandler());

        webView.setWebChromeClient(new WebChromeClient() {

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                pickFile();
            }
        });

        webView.loadUrl("file:///android_asset/demo.html");

        webView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
            }

        });

        btn.setOnClickListener(this);
    }

    public void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, RESULT_CODE);
    }


    @Override
    public void onClick(View view) {
        if (btn.equals(view)) {
            webView.callHandler(null, "data from Native", data -> {
                Toast.makeText(this, "callback from JS", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
