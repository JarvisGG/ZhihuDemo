package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.fragment.ViewPagerFragment;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.view1.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/4/2 下午2:41
 * @changeRecord [修改记录] <br/>
 */

@ContentView(R.layout.activity_viewpager)
public class ViewPagerActivity extends BaseActivity {

    @ViewInject(R.id.view_pager)
    VerticalViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        List<Fragment> list = new ArrayList<>();

        Bundle bundle1 = new Bundle();
        bundle1.putString(ViewPagerFragment.URL, "http://m.meten.com/xxl/adult.html");
        bundle1.putInt(ViewPagerFragment.POSITION, 0);
        Fragment fg1 = ViewPagerFragment.newInstance(bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putString(ViewPagerFragment.URL, "http://blood.sdo.com/web3/mobile/");
        bundle2.putInt(ViewPagerFragment.URL, 1);
        Fragment fg2 = ViewPagerFragment.newInstance(bundle2);

        Bundle bundle3 = new Bundle();
        bundle3.putString(ViewPagerFragment.URL, "http://m.meten.com/xxl/adult.html");
        bundle3.putInt(ViewPagerFragment.URL, 2);
        Fragment fg3 = ViewPagerFragment.newInstance(bundle3);

        list.add(fg1);
        list.add(fg2);
        list.add(fg3);

        InnerAdapter adapter = new InnerAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(adapter);

    }

    class InnerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mList;

        public InnerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }
}
