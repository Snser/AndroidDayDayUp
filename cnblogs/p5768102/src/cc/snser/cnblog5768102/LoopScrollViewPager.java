package cc.snser.cnblog5768102;

import java.util.ArrayList;

import android.R.bool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class LoopScrollViewPager extends ViewPager {
    private boolean mIsLoopScrollEnabled = false;
    
    /**
     * 为实现循环滑动的内部Adapter，对{@link #setAdapter(PagerAdapter)}传入的<b>originalAdapter</b>做了一层封装
     */
    private PagerAdapter mInternalAdapter;
    
    private InternalOnPageChangeListener mInternalOnPageChangeListener;
    private OnPageChangeListener mOnPageChangeListener;
    private ArrayList<OnPageChangeListener> mOnPageChangeListeners;
    
    final ImageView mViewPrefix = new ImageView(getContext());
    final ImageView mViewPostfix = new ImageView(getContext());
    
    public LoopScrollViewPager(Context context) {
        super(context);
        init();
    }

    public LoopScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        super.addOnPageChangeListener(mInternalOnPageChangeListener = new InternalOnPageChangeListener());
    }
    
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
        if (getLoopScrollEnabled() && mInternalOnPageChangeListener.isPageManualSelected()) {
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
    
    /**
     * 设置adapter(<b>originalAdapter</b>)
     */
    @Override
    public void setAdapter(PagerAdapter originalAdapter) {
        if (originalAdapter instanceof FragmentPagerAdapter) {
            if (originalAdapter instanceof IFragmentPagerAdapter) {
                final FragmentManager fm = ((IFragmentPagerAdapter)originalAdapter).getFragmentManager();
                super.setAdapter(mInternalAdapter = new InternalFragmentAdapter(fm, (FragmentPagerAdapter)originalAdapter));
            }
        } else {
            super.setAdapter(mInternalAdapter = new InternalViewAdapter(originalAdapter));
        }
    }
    
    private class InternalViewAdapter extends PagerAdapter implements IInternalAdapter {
        private PagerAdapter mOriginalAdapter;
        
        public InternalViewAdapter(PagerAdapter originalAdapter) {
            mOriginalAdapter = originalAdapter;
            
            
            View page = (View) mOriginalAdapter.instantiateItem(LoopScrollViewPager.this, 0);
            
        }
        
        @Override
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
                final int originalPosition = toOriginalPosition(position);
                final int count = getCount();
                final Object item;
                if (getLoopScrollEnabled() && (position == 0 || position == count - 1)) {
                    mViewPrefix.setBackgroundColor(0xffff0000);
                    mViewPostfix.setBackgroundColor(0xff00ff00);
                    item = position == 0 ? mViewPrefix : mViewPostfix;
                } else {
                    item = mOriginalAdapter.instantiateItem(container, originalPosition);
                }
                Log.i("Snser", "instantiateItem originalPos=" + originalPosition + " internalPos=" + position + " internalCurItem=" + getCurrentItemInternal() + " item=" + item.hashCode());
                if (getLoopScrollEnabled() && item instanceof View) {
                    addViewInternal((View)item);
                }
                
/*                item = mOriginalAdapter.instantiateItem(container, originalPosition);
                Log.i("Snser", "instantiateItem originalPos=" + originalPosition + " internalPos=" + position + " internalCurItem=" + getCurrentItemInternal() + " item=" + item.hashCode());
                if (getLoopScrollEnabled() && item instanceof View && indexOfChild((View)item) == -1) {
                    addViewInternal((View)item);
                }*/
                
                return item;
            } else {
                return null;
            }
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mOriginalAdapter != null) {
                boolean destory = true;
                if (getLoopScrollEnabled()) {
                    final int originalCurrentPosition = getCurrentItemOriginal();
                    final int originalDestoryPosition = toOriginalPosition(position);
                    final int originalCount = getOriginalAdapter().getCount();
                    final int internalCount = getCount();
                    //destory = position != 0 && position != internalCount - 1;
                    destory = ((originalCurrentPosition - originalDestoryPosition + originalCount) % originalCount) > 1;
                }
                Log.i("Snser", "destroyItem originalPos=" + toOriginalPosition(position) + " internalPos=" + position + " internalCurItem=" + getCurrentItemInternal() + " item=" + object.hashCode() + " d=" + destory);
                //if (destory) {
                    container.removeView((View)object);
                //}
            }
        }
    }
    
    private class InternalFragmentAdapter extends FragmentPagerAdapter implements IInternalAdapter {
        private FragmentPagerAdapter mOriginalAdapter;
        
        public InternalFragmentAdapter(FragmentManager fm, FragmentPagerAdapter originalAdapter) {
            super(fm);
            mOriginalAdapter = originalAdapter;
        }
        
        @Override
        public PagerAdapter getOriginalAdapter() {
            return mOriginalAdapter;
        }
        
        @Override
        public Fragment getItem(int position) {
            return mOriginalAdapter != null ? mOriginalAdapter.getItem(position) : null;
        }

        @Override
        public int getCount() {
            return mOriginalAdapter != null ? mOriginalAdapter.getCount() : 0;
        }
    }
    
    public abstract class LoopScrollViewPagerAdapter extends PagerAdapter {
        public abstract Object instantiateItemNewInstance(ViewGroup container, int position);
    }
    
    public abstract class LoopScrollFragmentApapter extends FragmentPagerAdapter {
        public LoopScrollFragmentApapter(FragmentManager fm) {
            super(fm);
        }
        public abstract Fragment getItemNewInstance(int position);
    }
    
    public interface IFragmentPagerAdapter {
        public FragmentManager getFragmentManager();
    }
    
    private interface IInternalAdapter {
        public PagerAdapter getOriginalAdapter();
    }
    
    /* override addView begin */
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
    /* override addView end */
    
    
    /* override getCurrentItem begin */
    /**
     * getCurrentItem
     * @deprecated </br>本接口与 {@link #setAdapter(PagerAdapter)} 传入的 originalAdapter<b> 不对应</b>，请用 {@link #getCurrentItemOriginal()} 代替
     * @see #getCurrentItemOriginal()
     * @see #setAdapter(PagerAdapter)
     */
    @Override
    public final int getCurrentItem() {
        return super.getCurrentItem();
    }
    
    /**
     * getCurrentItem</br>本接口与 {@link #setAdapter(PagerAdapter)} 传入的 originalAdapter <b>对应</b>
     * @see #getCurrentItem()
     * @see #setAdapter(PagerAdapter)
     * @return
     */
    public int getCurrentItemOriginal() {
        return toOriginalPosition(getCurrentItemInternal());
    }
    
    /**
     * getCurrentItemInternal</br>本接口与 {@link #mInternalAdapter} <b>对应</b></br>本接口与 {@link #setAdapter(PagerAdapter)} 传入的 originalAdapter <b>不对应</b>
     * @see #getCurrentItemOriginal()
     * @see #setAdapter(PagerAdapter)
     * @return
     */
    private int getCurrentItemInternal() {
        return super.getCurrentItem();
    }
    /* override getCurrentItem end */
    
    
    /* override setCurrentItem begin */
    /**
     * setCurrentItem
     * @deprecated </br>本接口与 {@link #setAdapter(PagerAdapter)} 传入的 originalAdapter<b> 不对应</b>，请用 {@link #setCurrentItemOriginal(int)} 代替
     * @see #setCurrentItemOriginal(int)
     * @see #setAdapter(PagerAdapter)
     */
    @Override
    public final void setCurrentItem(int item) {
        Log.d("Snser", "super.setCurrentItem item=" + item);
        super.setCurrentItem(item);
    }
    
    /**
     * setCurrentItem
     * @deprecated </br>本接口与 {@link #setAdapter(PagerAdapter)} 传入的 originalAdapter<b> 不对应</b>，请用 {@link #setCurrentItemOriginal(int, boolean)} 代替
     * @see #setCurrentItemOriginal(int, boolean)
     * @see #setAdapter(PagerAdapter)
     */
    @Override
    public final void setCurrentItem(int item, boolean smoothScroll) {
        Log.d("Snser", "super.setCurrentItem item=" + item);
        super.setCurrentItem(item, smoothScroll);
    }
    
    /**
     * setCurrentItem</br>本接口与 {@link #setAdapter(PagerAdapter)} 传入的 originalAdapter <b>对应</b>
     * @see #setCurrentItem(int)
     * @see #setAdapter(PagerAdapter)
     * @return
     */
    public void setCurrentItemOriginal(int item) {
        super.setCurrentItem(toInternalPosition(item));
    }
    
    /**
     * setCurrentItem</br>本接口与 {@link #setAdapter(PagerAdapter)} 传入的 originalAdapter <b>对应</b>
     * @see #setCurrentItem(int, boolean)
     * @see #setAdapter(PagerAdapter)
     * @return
     */
    public void setCurrentItemOriginal(int item, boolean smoothScroll) {
        super.setCurrentItem(toInternalPosition(item), smoothScroll);
    }
    
    /**
     * setCurrentItemInternal</br>本接口与 {@link #mInternalAdapter} <b>对应</b></br>本接口与 {@link #setAdapter(PagerAdapter)} 传入的 originalAdapter <b>不对应</b>
     * @see #setCurrentItemOriginal(int)
     * @see #setCurrentItemOriginal(int, boolean)
     * @see #setAdapter(PagerAdapter)
     */
    private void setCurrentItemInternal(int item, boolean smoothScroll) {
        Log.d("Snser", "setCurrentItemInternal position=" + item + " originalPos=" + toOriginalPosition(item));
        super.setCurrentItem(item, smoothScroll);
    }
    /* override setCurrentItem end */
    
    
    /* override OnPageChangeListener begin */
    private class InternalOnPageChangeListener implements OnPageChangeListener {
        private int mScrollState = ViewPager.SCROLL_STATE_IDLE;
        private boolean mIsPageManualSelected = false;
        
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
            setPageManualSelected(true);
            final int originalPosition = toOriginalPosition(position);
            final int count = mInternalAdapter.getCount();
            Log.d("Snser", "onPageSelected position=" + position + " originalPos=" + originalPosition);
            //if (!(getLoopScrollEnabled() && (position == 0 || position == count - 1))) {
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
            //}
                
/*            if (position != 0 && position != count - 1) {
                if (originalPosition == 0) {
                    final View page = getChildAt(position);
                    Bitmap cache = page.getDrawingCache();
                    if (cache != null) {
                        mViewPostfix.setImageBitmap(cache);
                    }
                }
                
            }*/
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
            if (getLoopScrollEnabled() && mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                final int internalPosition = getCurrentItemInternal();
                final int count = mInternalAdapter.getCount();
                if (internalPosition == 0 || internalPosition == count - 1) {
                    setCurrentItemInternal(toInternalPosition(toOriginalPosition(internalPosition)), false);
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
        
        public int getScrollState() {
            return mScrollState;
        }
        
        private void setPageManualSelected(boolean isPageManualSelected) {
            mIsPageManualSelected = isPageManualSelected;
        }
        
        public boolean isPageManualSelected() {
            return mIsPageManualSelected;
        }
    }
    
    /**
     * @deprecated Method setOnPageChangeListener is deprecated
     */
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
    /* override OnPageChangeListener end */
    

}
