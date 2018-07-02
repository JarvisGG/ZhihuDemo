package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.*;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2017/12/28 上午11:45
 * @changeRecord [修改记录] <br/>
 */

@ContentView(R.layout.activity_material)
public class MaterialActivity extends BaseActivity {

    @ViewInject(R.id.m_rv)
    MaterialRecyclerView mRv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        List<MaterialRecyclerView.InnerData> list = new ArrayList<>();
        MaterialRecyclerView.InnerData data1 = new MaterialRecyclerView.InnerData();
        data1.cover = R.drawable.image1;
        list.add(data1);

        MaterialRecyclerView.InnerData data2 = new MaterialRecyclerView.InnerData();
        data2.cover = R.drawable.image2;
        list.add(data2);

        MaterialRecyclerView.InnerData data3 = new MaterialRecyclerView.InnerData();
        data3.cover = R.drawable.image3;
        list.add(data3);

        MaterialRecyclerView.InnerData data4 = new MaterialRecyclerView.InnerData();
        data4.cover = R.drawable.image4;
        list.add(data4);

        MaterialRecyclerView.InnerData data5 = new MaterialRecyclerView.InnerData();
        data5.cover = R.drawable.image5;
        list.add(data5);

        MaterialRecyclerView.InnerData data6 = new MaterialRecyclerView.InnerData();
        data6.cover = R.drawable.image1;
        list.add(data6);

        MaterialRecyclerView.InnerData data7 = new MaterialRecyclerView.InnerData();
        data7.cover = R.drawable.image2;
        list.add(data7);

        MaterialRecyclerView.InnerData data8 = new MaterialRecyclerView.InnerData();
        data8.cover = R.drawable.image3;
        list.add(data8);

        mRv.bindData(list);
    }
}
