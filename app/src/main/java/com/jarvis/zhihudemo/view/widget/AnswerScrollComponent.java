package com.jarvis.zhihudemo.view.widget;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.jarvis.zhihudemo.view.DirectionalViewPager;
import com.jarvis.zhihudemo.widgets.AdUtils;

import static com.jarvis.zhihudemo.view.DirectionalViewPager.NEXT;
import static com.jarvis.zhihudemo.view.DirectionalViewPager.PRE;

/**
 * @author Jarvis @ Zhihu Inc.
 * @version 1.0
 * @title ZhihuDemo
 * @description 该类主要功能描述
 * @create 2018/1/25 下午12:52
 * @changeRecord [修改记录] <br/>
 */

public class AnswerScrollComponent {
    private DirectionalViewPager viewPager;
    int mPosition = -1;

    public void init(ViewPagerInfo info,
                     DirectionalViewPager viewPager,
                     IViewOperators... views) {
        viewPager.bind(info);
        viewPager.addDirectionOnPageChangeListener(new DirectionalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels, int duration, boolean isScrolling) {
                Log.e("onPageScrolled -----> ", "position: " + position + " " +
                        "positionOffset: " + positionOffset + " positionOffsetPixels: " + positionOffsetPixels);
                notifyScrolled(views, info, position, positionOffsetPixels, duration, isScrolling);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("onPageSelected -----> ", "position: " + position);
                mPosition = position;
                notifyPageSelected(views, position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.viewPager = viewPager;
    }

    private void notifyPageSelected(IViewOperators[] views, int position) {
        for (int i = 0; i < views.length; i++) {
            views[i].intercept(1, 0);
        }
    }

    private void notifyScrolled(IViewOperators[] views,
                                ViewPagerInfo info,
                                int position,
                                int positionOffsetPixels,
                                int duraiton,
                                boolean isScrolling) {
        for (int i = 0; i < views.length; i++) {
            if (duraiton == PRE) {
                if (!isScrolling) {
                    if (mPosition > position) {
                        views[i].intercept(info.preOffset, AdUtils.getScreenSizeY(viewPager.getContext()) - positionOffsetPixels);
                    } else {
                        views[i].intercept(info.nextOffset, positionOffsetPixels);
                    }
                }
            } else if (duraiton == NEXT) {
                if (!isScrolling) {
                    views[i].intercept(info.nextOffset, positionOffsetPixels);
                }
            }
        }
    }

    public interface IViewOperators {
        /**
         *
         * @param total
         * @param current
         */
        public void intercept(float total, float current);
    }
}
