package cc.snser.cnblog5768102;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollControlViewPager extends ViewPager {
    
    private boolean mIsScrollEnabled = true;
    private boolean mIsAutoScrollEnabled = false;

    public ScrollControlViewPager(Context context) {
        super(context);
    }

    public ScrollControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        mIsAutoScrollEnabled = isEnabled;
    }
    
    
    /* Scroll Switch begin */
    @Override
    public final boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mIsScrollEnabled && ev.getAction() == MotionEvent.ACTION_MOVE) {
            super.onInterceptTouchEvent(ev);
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //performClick();
        }
        
        if (!mIsScrollEnabled && ev.getAction() == MotionEvent.ACTION_MOVE) {
            //super.onTouchEvent(ev);
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }
    
    @Override
    public boolean performClick() {
        return super.performClick();
    }
    
    @Override
    public void scrollBy(int x, int y) {
        if (mIsScrollEnabled) {
            super.scrollBy(x, y);
        }
    }
    
    @Override
    public void scrollTo(int x, int y) {
        if (mIsScrollEnabled) {
            super.scrollTo(x, y);
        }
    }
    /* Scroll Switch end */

}
