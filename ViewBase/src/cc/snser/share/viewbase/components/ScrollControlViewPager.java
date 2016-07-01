package cc.snser.share.viewbase.components;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class ScrollControlViewPager extends ViewPager {
    public static final int SCROLL_DIRECTTION_NEXT = 0x01;
    public static final int SCROLL_DIRECTTION_PRIVIOUS = 0x02;
    public static final int DEFAULT_SCROLL_INTERVAL = 3 * 1000;
    
    private boolean mIsScrollEnabled = true;
    private boolean mIsAutoScrollEnabled = false;;
    private boolean mIsLoopScrollEnabled = false;
    
    private int mScrollState = ViewPager.SCROLL_STATE_IDLE;
    
    private final int mDefaultOverScrollMode = getOverScrollMode();
    
    private PagerAdapter mAdapter;
    
    private LoopScrollHandler mHandler = new LoopScrollHandler(this);
    

    public ScrollControlViewPager(Context context) {
        super(context);
        init();
    }

    public ScrollControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        addOnPageChangeListener(new ScrollControlPageChangeListener());
    }
    
    
    public boolean getScrollEnabled() {
        return mIsScrollEnabled;
    }
    
    public void setScrollEnabled(boolean isEnabled) {
        mIsScrollEnabled = isEnabled;
    }
    
    public boolean getAutoScrollEnabled() {
        return mIsAutoScrollEnabled;
    }
    
    public void setAutoScrollEnabled(boolean isEnabled) {
        setAutoScrollEnabled(isEnabled, DEFAULT_SCROLL_INTERVAL, SCROLL_DIRECTTION_NEXT);
    }
    
    public void setAutoScrollEnabled(boolean isEnabled, long intervalMills, int direction) {
        if (mIsLoopScrollEnabled) {
            mIsAutoScrollEnabled = isEnabled;
            if (mIsAutoScrollEnabled) {
                mHandler.setScrollInterval(intervalMills);
                mHandler.setScrollDirection(direction);
                mHandler.start();
            } else {
                mHandler.cancel();
            }
        }
    }
    
    public int getScrollState() {
        return mScrollState;
    }
    
    
    @Override
    public void setAdapter(PagerAdapter adapter) {
        setAdapter(adapter, false);
    }
    
    public void setAdapter(PagerAdapter adapter, boolean isLoopScrollEnabled) {
        boolean isFragment = false;
        if (adapter instanceof FragmentPagerAdapter) {
            if (adapter instanceof IFragmentPagerAdapter) {
                isFragment = true;
            } else {
                return;
            }
        }
        if (isLoopScrollEnabled) {
            if (adapter == null || adapter.getCount() <= 1) {
                isLoopScrollEnabled = false;
            }
        }
        if (isLoopScrollEnabled) {
            final Object userPage = isFragment ? ((FragmentPagerAdapter)adapter).getItem(0) : adapter.instantiateItem(this, 0);
            final Object userPageCopy = isFragment ? ((FragmentPagerAdapter)adapter).getItem(0) : adapter.instantiateItem(this, 0);
            //传入的adapter需要保证每次instantiateItem时，都实例化出不同的item，否则后续的addview会有问题
            if (userPage == userPageCopy) {
                isLoopScrollEnabled = false;
            }
        }
        mIsLoopScrollEnabled = isLoopScrollEnabled;
        setOverScrollMode(mIsLoopScrollEnabled ? View.OVER_SCROLL_NEVER : mDefaultOverScrollMode);
        if (isFragment) {
            final FragmentManager manager = ((IFragmentPagerAdapter)adapter).getFragmentManager();
            super.setAdapter(mAdapter = new ScrollControlFragmentPagerAdapter(manager, (FragmentPagerAdapter)adapter));
        } else {
            super.setAdapter(mAdapter = new ScrollControlViewPagerAdapter(adapter));
        }
    }
    
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return !mIsScrollEnabled || super.onTouchEvent(ev);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !mIsScrollEnabled || super.onInterceptTouchEvent(ev);
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return super.dispatchTouchEvent(ev);
    }
    
    /* setCurrentItem begin */
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(mIsLoopScrollEnabled ? item + 1 : item);
    }
    
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(mIsLoopScrollEnabled ? item + 1 : item, smoothScroll);
    }
    
    private void setCurrentItemInternal(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }
    
    public void scrollToNext(boolean smoothScroll) {
        if (getAdapter() != null) {
            setCurrentItemInternal((getCurrentItem() + 1) % getAdapter().getCount(), smoothScroll);
        }
    }
    
    public void scrollToPrevious(boolean smoothScroll) {
        if (getAdapter() != null) {
            setCurrentItemInternal((getCurrentItem() - 1) % getAdapter().getCount(), smoothScroll);
        }
    }
    /* setCurrentItem end */
    
    
    /* addView begin */
    @Override
    public void addView(View child) {
        if (mAdapter instanceof ScrollControlFragmentPagerAdapter) {
            super.addView(child);
        }
    }
    
    @Override
    public void addView(View child, int index) {
        if (mAdapter instanceof ScrollControlFragmentPagerAdapter) {
            super.addView(child, index);
        }
    }
    
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mAdapter instanceof ScrollControlFragmentPagerAdapter) {
            super.addView(child, index, params);
        }
    }
    
    @Override
    public void addView(View child, int width, int height) {
        if (mAdapter instanceof ScrollControlFragmentPagerAdapter) {
            super.addView(child, width, height);
        }
    }
    
    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (mAdapter instanceof ScrollControlFragmentPagerAdapter) {
            super.addView(child, params);
        }
    }
    
    private void addViewInternal(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
            if (params == null) {
                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
            }
        }
        addViewInternal(child, -1, params);
    }
    
    private void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, -1, params);
    }
    /* addView end */
    
    
    private class ScrollControlViewPagerAdapter extends PagerAdapter {
        private PagerAdapter mUserAdapter;
        
        public ScrollControlViewPagerAdapter(PagerAdapter userAdapter) {
            mUserAdapter = userAdapter;
        }
        
        @Override
        public int getCount() {
            if (mUserAdapter != null) {
                if (mIsLoopScrollEnabled) {
                    return mUserAdapter.getCount() + 2;
                } else {
                    return mUserAdapter.getCount();
                }
            } else {
                return 0;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            if (mUserAdapter != null) {
                return mUserAdapter.isViewFromObject(view, object);
            } else {
                return false;
            }
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Object item;
            if (mUserAdapter == null) {
                item = null;
            } else if (!mIsLoopScrollEnabled) {
                item = mUserAdapter.instantiateItem(container, position);
            } else if (position == 0) {
                item = mUserAdapter.instantiateItem(container, mUserAdapter.getCount() - 1);
            } else if (position == getCount() - 1) {
                item = mUserAdapter.instantiateItem(container, 0);
            } else {
                item = mUserAdapter.instantiateItem(container, position - 1);
            }
            if (item instanceof View) {
                addViewInternal((View)item);
            }
            return item;
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
    
    
    public static interface IFragmentPagerAdapter {
        public FragmentManager getFragmentManager();
    }
    
    private class ScrollControlFragmentPagerAdapter extends FragmentPagerAdapter {
        private FragmentPagerAdapter mUserAdapter;

        public ScrollControlFragmentPagerAdapter(FragmentManager fm, FragmentPagerAdapter userAdapter) {
            super(fm);
            mUserAdapter = userAdapter;
        }
        
        @Override
        public int getCount() {
            if (mUserAdapter != null) {
                if (mIsLoopScrollEnabled) {
                    return mUserAdapter.getCount() + 2;
                } else {
                    return mUserAdapter.getCount();
                }
            } else {
                return 0;
            }
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment item;
            if (mUserAdapter == null) {
                item = null;
            } else if (!mIsLoopScrollEnabled) {
                item = mUserAdapter.getItem(position);
            } else if (position == 0) {
                item = mUserAdapter.getItem(mUserAdapter.getCount() - 1);
            } else if (position == getCount() - 1) {
                item = mUserAdapter.getItem(0);
            } else {
                item = mUserAdapter.getItem(position - 1);
            }
            return item;
        }
    }
    
    
    private class ScrollControlPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
            if (mIsLoopScrollEnabled && state == ViewPager.SCROLL_STATE_IDLE) {
                final int currentItem = getCurrentItem();
                final int count = mAdapter.getCount();
                if (currentItem == 0) {
                    setCurrentItemInternal(count - 2, false);
                } else if (currentItem == count - 1) {
                    setCurrentItemInternal(1, false);
                }
            }
        }
    }
    
    
    private static class LoopScrollHandler extends Handler {
        private static final int MSG_AUTO_SCROLL = 0x101;
        
        private WeakReference<ScrollControlViewPager> mPager;
        private long mScrollIntervalMills = Integer.MAX_VALUE;
        private int mDirection = SCROLL_DIRECTTION_NEXT;
        
        public LoopScrollHandler(ScrollControlViewPager pager) {
            mPager = new WeakReference<ScrollControlViewPager>(pager);
        }
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_AUTO_SCROLL) {
                cancel();
                scroll();
                start();
            }
        }
        
        public void setScrollInterval(long intervalMills) {
            mScrollIntervalMills = intervalMills;
        }
        
        public void setScrollDirection(int direction) {
            mDirection = direction;
        }
        
        public void start() {
            sendEmptyMessageDelayed(MSG_AUTO_SCROLL, mScrollIntervalMills);
        }
        
        public void cancel() {
            removeMessages(MSG_AUTO_SCROLL);
        }
        
        private void scroll() {
            final ScrollControlViewPager pager = mPager != null ? mPager.get() : null;
            if (pager != null && pager.getScrollState() == ViewPager.SCROLL_STATE_IDLE) {
                if (mDirection == SCROLL_DIRECTTION_NEXT) {
                    pager.scrollToNext(true);
                } else if (mDirection == SCROLL_DIRECTTION_PRIVIOUS) {
                    pager.scrollToPrevious(true);
                }
            }
        }
    }

}
