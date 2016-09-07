package cc.snser.cnblog5768102;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollControlViewPager extends LoopScrollViewPager {
    
    private boolean mIsScrollEnabled = true;
    private boolean mIsAutoScrollEnabled = false;
    
    public ScrollControlViewPager(Context context) {
        super(context);
        init();
    }

    public ScrollControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
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
