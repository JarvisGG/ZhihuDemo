package com.jarvis.zhihudemo.view.hybrid;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/25 上午10:00
 * @changeRecord [修改记录] <br/>
 */

public class DefaultHandler implements BridgeHandler {
    @Override
    public void handler(String data, CallBackFunction function) {
        if(function != null){
            function.onCallBack("DefaultHandler response data");
        }
    }
}
