package cc.snser.share.viewbase.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;

public class KeyboardObserver {
    private static final String PREFS_KEY_KEYBOARD_HEIGHT = "";
    
    private View mDecorView;
    private Activity mActivity;
    
    private int mKeyboardDefaultHeight = 0;
    private int mKeyboardHeight = mKeyboardDefaultHeight;
    
    private int mViewRawHeight = 0;
    private int mViewCurrentHeight = 0;
    
    private boolean mIsMaunalShowKeyboard = false;
    
    private Rect mRectWindowVisiable = new Rect();
    
    private InputMethodManager mInputMgr;
    
    public KeyboardObserver(Activity activity) {
        this(activity, 0);
    }
    
    public KeyboardObserver(Activity activity, int defaultKeyboardHeight) {
        mActivity = activity;
        mDecorView = mActivity.getWindow().getDecorView();
        mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(new OnViewGlobalLayoutListener());
        mKeyboardDefaultHeight = defaultKeyboardHeight;
        mInputMgr = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    
    private class OnViewGlobalLayoutListener implements OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            mDecorView.getWindowVisibleDisplayFrame(mRectWindowVisiable);
            final int windowHeight = mRectWindowVisiable.bottom - mRectWindowVisiable.top;
            if (windowHeight > 0) {
                mViewCurrentHeight = windowHeight;
                if (mViewRawHeight == 0) {
                    mViewRawHeight = mViewCurrentHeight;
                } else if (mKeyboardHeight == 0 && mViewCurrentHeight < mViewRawHeight) {
                    mKeyboardHeight = mViewRawHeight - mViewCurrentHeight;
                    setPrefsKeyboardHeight(mKeyboardHeight);
                }
            }
            Log.d("Snser", "onGlobalLayout raw=" + mViewRawHeight + " current=" + mViewCurrentHeight + " visiable=" + isKeyboardVisiable());
        }
    }
    
    private int getPrefsKeyboardHeight() {
        return mActivity.getPreferences(Context.MODE_PRIVATE).getInt(PREFS_KEY_KEYBOARD_HEIGHT, 0);
    }
    
    private void setPrefsKeyboardHeight(int height) {
        mActivity.getPreferences(Context.MODE_PRIVATE).edit().putInt(PREFS_KEY_KEYBOARD_HEIGHT, height).apply();
    }
    
    public int getKeyboardHeight() {
        if (mKeyboardHeight > 0) {
            return mKeyboardHeight;
        } else {
            int prefsHeight = getPrefsKeyboardHeight();
            if (prefsHeight > 0) {
                mKeyboardHeight = prefsHeight;
                return mKeyboardHeight;
            }
        }
        return mKeyboardDefaultHeight;
    }
    
    public boolean isKeyboardVisiable() {
        if (isWindowAutoResize()) {
            return mViewRawHeight > mViewCurrentHeight;
        } else {
            return mIsMaunalShowKeyboard;
        }
    }
    
    public void setKeyboardVisiable(boolean isVisiable) {
        if (isVisiable) {
            mInputMgr.showSoftInput(mActivity.getCurrentFocus(), 0);
        } else {
            mInputMgr.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0);
        }
        mIsMaunalShowKeyboard = isVisiable;
    }
    
    private boolean isWindowAutoResize() {
        final int softInputMode = mActivity.getWindow().getAttributes().softInputMode;
        final int maskAdjust = WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST;
        final int autoResize = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        return (softInputMode & maskAdjust) == autoResize;
    }
    
    
}
