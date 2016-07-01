package cc.snser.share.viewbase;

import java.util.ArrayList;

import cc.snser.share.viewbase.components.AnimFragment;
import cc.snser.share.viewbase.components.ClickFragment;
import cc.snser.share.viewbase.components.DateFragment;
import cc.snser.share.viewbase.components.PagerFragment;
import cc.snser.share.viewbase.components.TabFragment;
import cc.snser.share.viewbase.components.TitleFragment;
import cc.snser.share.viewbase.components.TabFragment.OnTabClickListenser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

//  各个Fragment的加载顺序：
//      如果 DEFAULT_PAGE = 1：
//          ViewPagerFragmentActivity onCreate
//          ViewPagerFragmentActivity onCreate setContentView
//          TitleFragment onCreateView
//          ViewPagerFragmentActivity onCreate initView
//          TabFragment onCreateView
//          ViewPagerFragmentActivity onResume
//          DateFragment onCreateView
//          ClickFragment onCreateView(viewpager默认页左边的页面会被预加载)
//          AnimFragment onCreateView(viewpager默认页右边的页面会被预加载)
//      如果 DEFAULT_PAGE = 0：
//          ViewPagerFragmentActivity onCreate
//          ViewPagerFragmentActivity onCreate setContentView
//          TitleFragment onCreateView
//          ViewPagerFragmentActivity onCreate initView
//          TabFragment onCreateView
//          ViewPagerFragmentActivity onResume
//          ClickFragment onCreateView
//          DateFragment onCreateView(viewpager默认页右边的页面会被预加载)
//          AnimFragment onCreateView(viewpager切换到DateFragment时才会预加载AnimFragment)

public class ViewPagerFragmentActivity extends FragmentActivity {
    private FragmentManager mManager = getSupportFragmentManager();
    
    private TitleFragment mTitleFragment;
    private TabFragment mTabFragment;
    private ViewPager mPager;
    
    private ViewPagerAdapter mAdapter;
    
    private static final int DEFAULT_PAGE = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Snser", "ViewPagerFragmentActivity onCreate");
        super.onCreate(savedInstanceState);
        Log.d("Snser", "ViewPagerFragmentActivity onCreate setContentView");
        setContentView(R.layout.viewpager_fragment);
        Log.d("Snser", "ViewPagerFragmentActivity onCreate initView");
        initView();
    }
    
    private void initView() {
        mTitleFragment = (TitleFragment)mManager.findFragmentById(R.id.viewpager_fragment_title);
        mTabFragment = new TabFragment();
        mTabFragment.setOnTabClickListenser(new ViewPageTabClickListenser());
        mManager.beginTransaction().replace(R.id.viewpager_fragment_container, mTabFragment).commit();
        mPager = (ViewPager)findViewById(R.id.viewpager_fragment_pager);
        mPager.setAdapter(mAdapter = new ViewPagerAdapter(mManager));
        mPager.addOnPageChangeListener(new ViewPageChangeListener());
        mPager.setCurrentItem(DEFAULT_PAGE);
        //如果DEFAULT_PAGE为0，页面初始化时不会触发onPageSelected，需额外调用一次notifyPageChangeToFragments
        if (mPager.getCurrentItem() == DEFAULT_PAGE) {
            notifyPageChangeToFragments(DEFAULT_PAGE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Snser", "ViewPagerFragmentActivity onResume");
    }
    
    private class ViewPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            setCurrentItem(position, false);
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<PagerFragment> mFragments = new ArrayList<PagerFragment>();
        
        public ViewPagerAdapter(FragmentManager fm) {
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
    
    private class ViewPageTabClickListenser implements OnTabClickListenser {
        @Override
        public void onTabClick(int tab) {
            setCurrentItem(tab, true);
        }
    }
       
    private void setCurrentItem(int item, boolean isFromClickTab) {
        //来源于手动点击tab的调用，不用通知Fragment了，后续onPageSelected会去通知
        if (!isFromClickTab) {
            notifyPageChangeToFragments(item);
        }
        //来源于onPageSelected的调用，不用再setCurrentItem了
        if (item != mPager.getCurrentItem()) {
            mPager.setCurrentItem(item);
        }
    }
    
    private void notifyPageChangeToFragments(int item) {
        for (int page = 0; page != mAdapter.getCount(); ++page) {
            final Fragment fragment = mAdapter.getItem(page);
            if (fragment instanceof PagerFragment) {
                if (page == item) {
                    ((PagerFragment)fragment).onPageIn();
                } else {
                    ((PagerFragment)fragment).onPageOut();
                }
            }
        }
        mTitleFragment.setCurrentTab(item);
        mTabFragment.setCurrentTab(item);
    }
}
