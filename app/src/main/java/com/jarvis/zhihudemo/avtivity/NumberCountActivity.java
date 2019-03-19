package com.jarvis.zhihudemo.avtivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.jarvis.zhihudemo.R;
import com.jarvis.zhihudemo.base.BaseActivity;
import com.jarvis.zhihudemo.view.annotations.ContentView;
import com.jarvis.zhihudemo.view.annotations.ViewInject;
import com.jarvis.zhihudemo.view.annotations.ViewInjectUtils;
import com.jarvis.zhihudemo.view1.NumberCountView;

/**
 * @author yyf @ Zhihu Inc.
 * @since 03-19-2019
 */
@ContentView(R.layout.activity_number_count)
public class NumberCountActivity extends BaseActivity {

    @ViewInject(R.id.start)
    Button start;

    @ViewInject(R.id.reset)
    Button reset;

    @ViewInject(R.id.duration)
    EditText duration;

    @ViewInject(R.id.delay)
    EditText delay;

    @ViewInject(R.id.beginAlpha)
    EditText beginAlpha;

    @ViewInject(R.id.middleAlpha)
    EditText middleAlpha;


    @ViewInject(R.id.number)
    NumberCountView countView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtils.inject(this);

        countView.setNumber(976, 12714624);
        countView.setInfo(Integer.valueOf(duration.getText().toString()), Integer.valueOf(delay.getText().toString()));

        start.setOnClickListener(v -> {

            countView.setAlphaRange(Integer.valueOf(beginAlpha.getText().toString()), Integer.valueOf(middleAlpha.getText().toString()));
            countView.setInfo(Integer.valueOf(duration.getText().toString()), Integer.valueOf(delay.getText().toString()));
            countView.start();


        });

        reset.setOnClickListener(v -> {
            countView.reset();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
