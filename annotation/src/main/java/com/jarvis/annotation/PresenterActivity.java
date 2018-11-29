package com.jarvis.annotation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jarvis.annotation.presenter.FirstPresenter;
import com.jarvis.annotation.presenter.SecondPresenter;
import com.jarvis.item.annotation.Presenter.Presenter;

/**
 * @author yyf @ Zhihu Inc.
 * @since 07-09-2018
 */
@Presenter(
        value = {
                FirstPresenter.class,
                SecondPresenter.class
        }
)
public class PresenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

}
