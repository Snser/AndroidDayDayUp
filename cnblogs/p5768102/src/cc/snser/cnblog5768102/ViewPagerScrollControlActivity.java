package cc.snser.cnblog5768102;

import java.util.ArrayList;

import cc.snser.cnblog5768102.components.AnimFragment;
import cc.snser.cnblog5768102.components.ClickFragment;
import cc.snser.cnblog5768102.components.DateFragment;
import cc.snser.cnblog5768102.components.PagerFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPagerScrollControlActivity extends FragmentActivity implements View.OnClickListener {
    
    private static final boolean FLAG_USE_FRAGMENT_ADAPTER = true;
    private static final int DEFAULT_PAGE = 1; //默认页面
    
    private ImageView mImgIndicator;
    private ScrollControlViewPager mPager;
    private Button mBtnScrollSwitch;
    private Button mBtnAutoScroll;
    
    private PagerAdapter mAdapter;
    
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
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private void initView() {
        mImgIndicator = (ImageView)findViewById(R.id.viewpager_scroll_control_indicator);
        
        mPager = (ScrollControlViewPager)findViewById(R.id.viewpager_scroll_control_pager);
        mPager.setOnPageChangeListener(new ViewPageChangeListener());
        if (FLAG_USE_FRAGMENT_ADAPTER) {
            mPager.setAdapter(mAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager())); //这是fragment adapter的例子
        } else {
            mPager.setAdapter(mAdapter = new ViewPagerViewAdapter(this)); //这是view adapter的例子
            mPager.setBackgroundResource(R.drawable.viewpager_view_bg);
        }
        setCurrentItem(DEFAULT_PAGE);
        
        mBtnScrollSwitch = (Button)findViewById(R.id.viewpager_scroll_control_action_switch);
        mBtnScrollSwitch.setOnClickListener(this);
        mBtnAutoScroll = (Button)findViewById(R.id.viewpager_scroll_control_action_auto);
        mBtnAutoScroll.setOnClickListener(this);
    }
    
    private class ViewPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            setCurrentItem(position);
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    
    private void setCurrentItem(int item) {
        if (item == mPager.getCurrentItem()) {
            //此时是源于initView或onPageSelected的调用
            notifyPageChangeToFragments(item);
            switch (item) {
                case 0:
                    mImgIndicator.setImageResource(R.drawable.viewpager_scroll_control_indicator_1);
                    break;
                case 1:
                    mImgIndicator.setImageResource(R.drawable.viewpager_scroll_control_indicator_2);
                    break;
                case 2:
                    mImgIndicator.setImageResource(R.drawable.viewpager_scroll_control_indicator_3);
                    break;
                default:
                    break;
            }
        } else {
            //此时是源于initView的调用，后续会自动触发一次onPageSelected
            mPager.setCurrentItem(item);
        }
    }
    
    private void notifyPageChangeToFragments(int item) {
        if (mAdapter instanceof FragmentPagerAdapter) {
            final FragmentPagerAdapter adapter = (FragmentPagerAdapter)mAdapter;
            for (int page = 0; page != adapter.getCount(); ++page) {
                final Fragment fragment = adapter.getItem(page);
                if (fragment instanceof PagerFragment) {
                    if (page == item) {
                        ((PagerFragment)fragment).onPageIn();
                    } else {
                        ((PagerFragment)fragment).onPageOut();
                    }
                }
            }
        }
    }
    
    private class ViewPagerViewAdapter extends PagerAdapter {
        private final int mCount = 3;
        private LayoutInflater mInflater;
        private SparseArray<View> mPageCache = new SparseArray<View>();
        
        private ViewPagerViewAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = mPageCache.get(position);
            if (page == null) {
                page = mInflater.inflate(R.layout.viewpager_view_page, container, false);
                TextView txtTitle = (TextView)page.findViewById(R.id.viewpager_view_page_title);
                ImageView imgContent = (ImageView)page.findViewById(R.id.viewpager_view_page_content);
                switch (position) {
                    case 0:
                        txtTitle.setText(R.string.viewpager_view_page_title_1);
                        imgContent.setImageResource(R.drawable.viewpager_view_page_content_1);
                        break;
                    case 1:
                        txtTitle.setText(R.string.viewpager_view_page_title_2);
                        imgContent.setImageResource(R.drawable.viewpager_view_page_content_2);
                        break;
                    case 2:
                        txtTitle.setText(R.string.viewpager_view_page_title_3);
                        imgContent.setImageResource(R.drawable.viewpager_view_page_content_3);
                        break;
                    default:
                        break;
                }
                mPageCache.append(position, page);
            }
            container.addView(page);
            return page;
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
    
    private class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
        private ArrayList<PagerFragment> mFragments = new ArrayList<PagerFragment>();
        
        public ViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
            mFragments.add(new ClickFragment());
            mFragments.add(new DateFragment());
            mFragments.add(new AnimFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewpager_scroll_control_action_switch:
                onScrollSwitchClicked();
                break;
            case R.id.viewpager_scroll_control_action_auto:
                onAutoScrollClicked();
                break;
            default:
                break;
        }
    }
    
    private void onScrollSwitchClicked() {
        mPager.setScrollEnabled(!mPager.getScrollEnabled());
        mBtnScrollSwitch.setText(mPager.getScrollEnabled() ? "禁止滑动" : "允许滑动");
    }
    
    private void onAutoScrollClicked() {
        mPager.setAutoScrollEnabled(!mPager.getAutoScrollEnabled());
        mBtnAutoScroll.setText(mPager.getAutoScrollEnabled() ? "禁止自动滑动" : "开启自动滑动");
    }
    
}
