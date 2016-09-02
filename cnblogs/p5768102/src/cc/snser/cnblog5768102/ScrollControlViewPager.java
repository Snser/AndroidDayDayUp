package cc.snser.cnblog5768102;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class ScrollControlViewPager extends ViewPager {
    
    private boolean mIsScrollEnabled = true;
    private boolean mIsLoopScrollEnabled = false;
    private boolean mIsAutoScrollEnabled = false;
    
    private PagerAdapter mInternalAdapter;
    
    private OnPageChangeListener mOnPageChangeListener;
    private ArrayList<OnPageChangeListener> mOnPageChangeListeners;

    public ScrollControlViewPager(Context context) {
        super(context);
        init();
    }

    public ScrollControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        super.addOnPageChangeListener(new InternalOnPageChangeListener());
    }
    
    
    /* Scroll Switch begin */
    public boolean getScrollEnabled() {
        return mIsScrollEnabled;
    }
    
    public void setScrollEnabled(boolean isEnabled) {
        mIsScrollEnabled = isEnabled;
    }
    
    @Override
    public final boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsScrollEnabled && super.onInterceptTouchEvent(ev);
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        return mIsScrollEnabled && super.onTouchEvent(ev);
    }
    /* Scroll Switch end */
    
    
    /* Loop Scroll begin */
    public boolean getLoopScrollEnabled() {
        return true;//mIsLoopScrollEnabled;
    }
    
    public void setLoopScrollEnabled(boolean isEnabled) {
        mIsLoopScrollEnabled = isEnabled;
    }
    

    /**
     * 将InternalAdapter的position转为OriginalAdapter的position
     * @param internalPosition
     * @return
     */
    private int toOriginalPosition(int internalPosition) {
        final int originalPosition;
        if (getLoopScrollEnabled()) {
            final int originalCount = mInternalAdapter.getCount() - 2;
            originalPosition = (internalPosition - 1 + originalCount) % originalCount;
        } else {
            originalPosition = internalPosition;
        }
        //Log.d("Snser", "toOriginalPosition " + internalPosition + " >> " + originalPosition);
        return originalPosition;
    }
    
    /**
     * 将OriginalAdapter的position转为InternalAdapter的position
     * @param originalPosition
     * @return
     */
    private int toInternalPosition(int originalPosition) {
        final int internalPosition;
        if (getLoopScrollEnabled()) {
            internalPosition = originalPosition + 1;
        } else {
            internalPosition = originalPosition;
        }
        //Log.d("Snser", "toInternalPosition " + originalPosition + " >> " + internalPosition);
        return internalPosition;
    }
    
    /* 重写ViewPager的setAdapter，将用户传入的PagerAdapter进行一层封装 */
    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (adapter instanceof FragmentPagerAdapter) {
            if (adapter instanceof IFragmentPagerAdapter) {
                final FragmentManager fm = ((IFragmentPagerAdapter)adapter).getFragmentManager();
                super.setAdapter(mInternalAdapter = new InternalFragmentAdapter(fm, (FragmentPagerAdapter)adapter));
            }
        } else {
            super.setAdapter(mInternalAdapter = new InternalViewAdapter(adapter));
        }
    }
    
    private class InternalViewAdapter extends PagerAdapter {
        private PagerAdapter mOriginalAdapter;
        
        public InternalViewAdapter(PagerAdapter originalAdapter) {
            mOriginalAdapter = originalAdapter;
        }
        
        public PagerAdapter getOriginalAdapter() {
            return mOriginalAdapter;
        }
        
        @Override
        public int getCount() {
            if (mOriginalAdapter != null) {
                if (getLoopScrollEnabled()) {
                    return mOriginalAdapter.getCount() + 2;
                } else {
                    return mOriginalAdapter.getCount();
                }
            } else {
                return 0;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mOriginalAdapter != null ? mOriginalAdapter.isViewFromObject(view, object) : false;
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mOriginalAdapter != null) {
                if (getLoopScrollEnabled()) {
                    final Object item = mOriginalAdapter.instantiateItem(container, toOriginalPosition(position));
/*                    int indexOf = -1;
                    for (int c = 0; c != getChildCount(); ++c) {
                        Log.d("Snser", "instantiateItem getChild" + c + "=" + getChildAt(c).hashCode());
                        if (item == getChildAt(c)) {
                            indexOf = c;
                            break;
                        }
                    }*/
                    if (item instanceof View && indexOfChild((View)item) == -1) {
                        addViewInternal((View)item);
                    }
                    return item;
                } else {
                    return mOriginalAdapter.instantiateItem(container, position);
                }
            } else {
                return null;
            }
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d("Snser", "destroyItem pos=" + position + " item=" + object.hashCode());
            if (mOriginalAdapter != null) {
                mOriginalAdapter.destroyItem(container, position, object);
            }
        }
    }
    
    private class InternalFragmentAdapter extends FragmentPagerAdapter {
        private FragmentPagerAdapter mUserAdapter;
        
        public InternalFragmentAdapter(FragmentManager fm, FragmentPagerAdapter userAdapter) {
            super(fm);
            mUserAdapter = userAdapter;
        }
        
        public FragmentPagerAdapter getUserAdapter() {
            return mUserAdapter;
        }
        
        @Override
        public Fragment getItem(int position) {
            return mUserAdapter != null ? mUserAdapter.getItem(position) : null;
        }

        @Override
        public int getCount() {
            return mUserAdapter != null ? mUserAdapter.getCount() : 0;
        }
    }
    
    public interface IFragmentPagerAdapter {
        public FragmentManager getFragmentManager();
    }
    
    private class InternalOnPageChangeListener implements OnPageChangeListener {
        private int mScrollState = ViewPager.SCROLL_STATE_IDLE;
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final int originalPosition = toOriginalPosition(position);
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(originalPosition, positionOffset, positionOffsetPixels);
            }
            if (mOnPageChangeListeners != null) {
                for (OnPageChangeListener listener : mOnPageChangeListeners) {
                    if (listener != null) {
                        listener.onPageScrolled(originalPosition, positionOffset, positionOffsetPixels);
                    }
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            final int originalPosition = toOriginalPosition(position);
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(originalPosition);
            }
            if (mOnPageChangeListeners != null) {
                for (OnPageChangeListener listener : mOnPageChangeListeners) {
                    if (listener != null) {
                        listener.onPageSelected(originalPosition);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE && getLoopScrollEnabled()) {
                final int currentItem = getCurrentItem();
                final int count = mInternalAdapter.getCount();
                if (currentItem == 0) {
                    setCurrentItemInternal(count - 2, false);
                } else if (currentItem == count - 1) {
                    setCurrentItemInternal(1, false);
                }
            }
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
            if (mOnPageChangeListeners != null) {
                for (OnPageChangeListener listener : mOnPageChangeListeners) {
                    if (listener != null) {
                        listener.onPageScrollStateChanged(state);
                    }
                }
            }
        }
    }
    
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!getLoopScrollEnabled()) {
            super.addView(child, index, params);
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
        super.addView(child, index, params);
    }
    
    @Override
    public int getCurrentItem() {
        //Log.d("Snser", "viewpager getCurrentItem ")
        return toOriginalPosition(super.getCurrentItem());
    }
    
    public int getCurrentItemOriginal() {
        return 0;
    }
    
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(getLoopScrollEnabled() ? item + 1 : item);
    }
    
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(getLoopScrollEnabled() ? item + 1 : item, smoothScroll);
    }
    
    public void setCurrentItemOriginal(int item) {
        
    }
    
    public void setCurrentItemOriginal(int item, boolean smoothScroll) {
        
    }
    
    private void setCurrentItemInternal(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }
    
    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }
    
    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = new ArrayList<OnPageChangeListener>();
        }
        mOnPageChangeListeners.add(listener);
    }
    
    @Override
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.remove(listener);
        }
    }
    
    @Override
    public void clearOnPageChangeListeners() {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.clear();
        }
    }
    /* Loop Scroll end */
    
    
    /* Auto Scroll begin */
    public boolean getAutoScrollEnabled() {
        return mIsAutoScrollEnabled;
    }
    
    public void setAutoScrollEnabled(boolean isEnabled) {
        mIsAutoScrollEnabled = isEnabled;
    }
    /* Auto Scroll end */

}
