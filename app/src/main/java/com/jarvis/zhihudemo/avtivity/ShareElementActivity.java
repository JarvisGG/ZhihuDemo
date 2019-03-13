package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.widget.Button;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.fragment.test.Fragment1;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.view1.NumberCountView;

/**
 * @author yyf @ Zhihu Inc.
 * @since 03-07-2019
 */
@ContentView(R.layout.activity_element)
public class ShareElementActivity extends BaseActivity {

    @ViewInject(R.id.button)
    Button excute;

    @ViewInject(R.id.number)
    NumberCountView countView;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        countView.setNumber(723, 7146);

        excute.setOnClickListener(v -> {

            countView.start();

//            fragmentManager = getSupportFragmentManager();
//
//            Fragment1 fragment1 = new Fragment1();
//
//            fragmentManager.beginTransaction()
//                    .add(R.id.container, fragment1, "")
//                    .addToBackStack("")
//                    .commit();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
