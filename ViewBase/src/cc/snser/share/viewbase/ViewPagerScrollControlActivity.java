package cc.snser.share.viewbase;

import java.util.ArrayList;

import cc.snser.share.viewbase.components.AnimFragment;
import cc.snser.share.viewbase.components.ClickFragment;
import cc.snser.share.viewbase.components.DateFragment;
import cc.snser.share.viewbase.components.ScrollControlViewPager;
import cc.snser.share.viewbase.components.ScrollControlViewPager.IFragmentPagerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ViewPagerScrollControlActivity extends FragmentActivity {
    private Button mBtnScrollSwitch;
    private Button mBtnAutoScroll;
    private ScrollControlViewPager mPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_scroll_control);
        initView();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private void initView() {
        mBtnScrollSwitch = (Button)findViewById(R.id.viewpager_scroll_control_btn_scroll_switch);
        mBtnScrollSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setScrollEnabled(!mPager.getScrollEnabled());
                mBtnScrollSwitch.setText(mPager.getScrollEnabled() ? "禁止滑动" : "开启滑动");
            }
        });
        mBtnAutoScroll = (Button)findViewById(R.id.viewpager_scroll_control_btn_scroll_auto);
        mBtnAutoScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setAutoScrollEnabled(!mPager.getAutoScrollEnabled(), 3 * 1000, ScrollControlViewPager.SCROLL_DIRECTTION_NEXT);
                mBtnAutoScroll.setText(mPager.getAutoScrollEnabled() ? "禁止自动滑动" : "开启自动滑动");
            }
        });
        mPager = (ScrollControlViewPager)findViewById(R.id.viewpager_scroll_control_pager);
        mPager.setAdapter(new ViewPagerAdapter(this), true); //这是view adapter的例子
        //mPager.setAdapter(new FragmentViewPagerAdapter(getSupportFragmentManager()), true); //这是fragment adapter的例子
        mPager.setCurrentItem(1);
    }
    
    private class ViewPagerAdapter extends PagerAdapter {
        private final int mCount = 3;
        private ArrayList<View> mViews = new ArrayList<View>();
        private LayoutInflater mInflater;
        
        public ViewPagerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }
        
        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.viewpager_scroll_control_page, container, false);
            Button btn = (Button)view.findViewById(R.id.viewpager_scroll_control_page_btn);
            btn.setText("page " + (position));
            switch (position) {
                case 0:
                    btn.setBackgroundColor(Color.argb(0x80, 0xFF, 0xFF, 0x00));
                    break;
                case 1:
                    btn.setBackgroundColor(Color.argb(0x80, 0xFF, 0x00, 0xFF));
                    break;
                case 2:
                    btn.setBackgroundColor(Color.argb(0x80, 0x00, 0xFF, 0xFF));
                    break;
                default:
                    break;
            }
            //这里的addView操作实际上会被ScrollControlViewPager拦截掉
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }
    }
    
    private class FragmentViewPagerAdapter extends FragmentPagerAdapter implements IFragmentPagerAdapter {
        private final FragmentManager mManager;
        private final int sCount = 3;

        public FragmentViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ClickFragment();
                case 1:
                    return new DateFragment();
                case 2:
                    return new AnimFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return sCount;
        }

        @Override
        public FragmentManager getFragmentManager() {
            return mManager;
        }
    }

}
