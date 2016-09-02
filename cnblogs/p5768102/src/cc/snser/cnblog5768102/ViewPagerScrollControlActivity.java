package cc.snser.cnblog5768102;

import java.util.ArrayList;

import cc.snser.cnblog5768102.ScrollControlViewPager.IFragmentPagerAdapter;
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
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ViewPagerScrollControlActivity extends FragmentActivity implements OnCheckedChangeListener {
    
    private static final boolean FLAG_USE_FRAGMENT_ADAPTER = false;
    private static final int DEFAULT_PAGE = 1; //默认页面
    
    private ImageView mImgIndicator;
    private ScrollControlViewPager mPager;
    private ToggleButton mBtnScrollSwitch;
    private ToggleButton mBtnLoopScroll;
    private ToggleButton mBtnAutoScroll;
    
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
        mPager.addOnPageChangeListener(new ViewPageChangeListener());
        if (FLAG_USE_FRAGMENT_ADAPTER) {
            mPager.setAdapter(mAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager())); //这是fragment adapter的例子
        } else {
            mPager.setAdapter(mAdapter = new ViewPagerViewAdapter(this)); //这是view adapter的例子
            mPager.setBackgroundResource(R.drawable.viewpager_view_bg);
        }
        setCurrentItem(DEFAULT_PAGE);
        
        mBtnScrollSwitch = (ToggleButton)findViewById(R.id.viewpager_scroll_control_action_switch);
        mBtnScrollSwitch.setOnCheckedChangeListener(this);
        mBtnLoopScroll = (ToggleButton)findViewById(R.id.viewpager_scroll_control_action_loop);
        mBtnLoopScroll.setOnCheckedChangeListener(this);
        mBtnAutoScroll = (ToggleButton)findViewById(R.id.viewpager_scroll_control_action_auto);
        mBtnAutoScroll.setOnCheckedChangeListener(this);
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
        Log.d("Snser", "Activity setCurrentItem item=" + item + " getCurrentItem=" + mPager.getCurrentItem());
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
    
    private class ViewPagerFragmentAdapter extends FragmentPagerAdapter implements IFragmentPagerAdapter {
        private final FragmentManager mFragmentManager;
        private ArrayList<PagerFragment> mFragments = new ArrayList<PagerFragment>();
        
        public ViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
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

        @Override
        public FragmentManager getFragmentManager() {
            return mFragmentManager;
        }
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mBtnScrollSwitch) {
            mPager.setScrollEnabled(isChecked);
        } else if (buttonView == mBtnLoopScroll) {
            mPager.setLoopScrollEnabled(isChecked);
            Log.d("Snser", "instantiateItem test getChildCount=" + mPager.getChildCount());
            for (int c = 0; c != mPager.getChildCount(); ++c) {
                Log.d("Snser", "instantiateItem test getChild" + c + "=" + mPager.getChildAt(c).hashCode());
            }
        } else if (buttonView == mBtnAutoScroll) {
            mPager.setAutoScrollEnabled(isChecked);
        }
    }
    
}
