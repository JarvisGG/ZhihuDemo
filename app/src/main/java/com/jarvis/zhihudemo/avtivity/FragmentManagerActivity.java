package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.fragment.test.Fragment1;
import com.jarvis.zhihudemo.fragment.test.Fragment2;
import com.jarvis.zhihudemo.fragment.test.Fragment3;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;

/**
 * @author yyf @ Zhihu Inc.
 * @since 04-27-2018
 */

@ContentView(R.layout.fragment_manager_activity)
public class FragmentManagerActivity extends BaseActivity {

    @ViewInject(R.id.button1)
    Button add;

    @ViewInject(R.id.button2)
    Button remove;

    @ViewInject(R.id.button3)
    Button replace;

    @ViewInject(R.id.container)
    FrameLayout container;

    FragmentManager fragmentManager;

    int index = 0;

    String TAG1 = "tag1";
    String TAG2 = "tag2";
    String TAG3 = "tag3";
    String TAG4 = "tag4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);
        fragmentManager = getSupportFragmentManager();

        Fragment1 fragment1 = new Fragment1();

        Fragment2 fragment2 = new Fragment2();

        Fragment3 fragment3 = new Fragment3();

        add.setOnClickListener(v -> {
            if (index == 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.container, fragment1, TAG1)
                        .addToBackStack(TAG1)
                        .commit();
                index ++;

            } else if (index == 1) {
                fragmentManager.beginTransaction()
                        .add(R.id.container, fragment2, TAG2)
                        .addToBackStack(TAG2)
                        .commit();
                index ++;
            } else if (index == 2) {
                fragmentManager.beginTransaction()
                        .add(R.id.container, fragment3, TAG3)
                        .addToBackStack(TAG3)
                        .commit();
                index ++;
            }
        });

        remove.setOnClickListener(v -> {
            fragmentManager.beginTransaction()
                    .remove(fragment2)
                    .addToBackStack(TAG4)
                    .commit();

        });

        replace.setOnClickListener(v -> fragmentManager.popBackStackImmediate(TAG3, 0)
);
    }
}
