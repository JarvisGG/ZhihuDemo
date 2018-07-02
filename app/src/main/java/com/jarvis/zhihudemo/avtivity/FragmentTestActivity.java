package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.fragment.test.Fragment1;
import com.jarvis.zhihudemo.fragment.test.Fragment2;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/2/26 上午10:52
 * @changeRecord [修改记录] <br/>
 */

public class FragmentTestActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);

        findViewById(R.id.btn_add_frag1).setOnClickListener(view -> {
            Fragment1 fragment1 = new Fragment1();
            addFragment(fragment1, "fragment1");
        });

        findViewById(R.id.btn_add_frag2).setOnClickListener(view -> {
            Fragment2 fragment2 = new Fragment2();
            addFragment(fragment2, "fragment2");
        });

        findViewById(R.id.btn_remove_frag2).setOnClickListener(view -> {
            removeFragment("fragment2");
        });

        findViewById(R.id.btn_repalce_frag1).setOnClickListener(view -> {
            replaceFragment();
        });
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }

    private void removeFragment(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(tag);
        transaction.remove(fragment);
        transaction.commit();
    }

    private void replaceFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment2 fragment2 = new Fragment2();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment2);
        transaction.commit();
    }

}
