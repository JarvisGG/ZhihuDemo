package com.jarvis.zhihudemo.widgets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yyf @ Zhihu Inc.
 * @since 01-02-2019
 */
public class QuestionPagerAdapter extends FragmentStatePagerAdapter {

    public enum PageType {
        /**
         *
         */
        TYPE_1,
        TYPE_2,
        TYPE_3,
        TYPE_4,
        TYPE_5,
    }

    private List<PageInfo> mList;

    private Object mCurrentDisplayObject = null;

    public static QuestionPagerAdapter create(FragmentManager fm) {
        return new QuestionPagerAdapter(fm);
    }

    private QuestionPagerAdapter(FragmentManager fm) {
        super(fm);
        init();
    }
    private void init() {
        mList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        PageInfo pageInfo = mList.get(position);
        Class<? extends Fragment> targetClass = pageInfo.getFragmentClass();
        Fragment targetFragment = null;
        try {
            targetFragment = targetClass.newInstance();
            targetFragment.setArguments(pageInfo.mBundle);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return targetFragment;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (mCurrentDisplayObject == object) {
            return POSITION_UNCHANGED;
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentDisplayObject = object;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).mPageTitle;
    }

    public QuestionPagerAdapter addData(PageInfo info) {
        return addDatas(Collections.singletonList(info));
    }

    public QuestionPagerAdapter addDatas(List<PageInfo> list) {
        mList.addAll(list);
        return this;
    }

    public static class PageInfo {

        public Class<? extends Fragment> mFragmentClass;

        public PageType mType;

        public String mPageTitle;

        public Bundle mBundle;

        public PageInfo(Class<? extends Fragment> fragmentClass, PageType type, String pageTitle, String pageNum, Bundle bundle) {
            this.mFragmentClass = fragmentClass;
            this.mType = type;
            this.mPageTitle = pageTitle;
            this.mBundle = bundle;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return this.mFragmentClass;
        }

        public PageType getType() {
            return this.mType;
        }
    }
}
