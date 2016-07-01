package cc.snser.share.viewbase.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class ScreenshotUtils {

    private static float getDegreesForRotation(int rotate) {
        switch (rotate) {
            case Surface.ROTATION_90:
                return 360f - 90f;
            case Surface.ROTATION_180:
                return 360f - 180f;
            case Surface.ROTATION_270:
                return 360f - 270f;
        }
        return 0f;
    }
    
    /**
     * 根据旋转后的子区域计算旋转前对应的原始子区域
     * @param rotate 旋转
     * @param rawScreenWidth 原始屏幕宽度
     * @param rawScreenHeight 原始屏幕高度
     * @param dispSubRegion 旋转后的视觉子区域
     * @return 旋转前的原始子区域
     */
    private static Rect calRawSubRegion(int rotate, int rawScreenWidth, int rawScreenHeight, Rect dispSubRegion) {
        Rect rawSubRegion = new Rect();
        switch (rotate) {
            case Surface.ROTATION_90:
                rawSubRegion.set(rawScreenWidth - dispSubRegion.bottom, 
                                 dispSubRegion.left, 
                                 rawScreenWidth - dispSubRegion.top, 
                                 dispSubRegion.right);
                break;
            case Surface.ROTATION_180:
                rawSubRegion.set(rawScreenWidth - dispSubRegion.right, 
                                 rawScreenHeight - dispSubRegion.bottom, 
                                 rawScreenWidth - dispSubRegion.left, 
                                 rawScreenHeight - dispSubRegion.top);
                break;
            case Surface.ROTATION_270:
                rawSubRegion.set(dispSubRegion.top, 
                                 rawScreenHeight - dispSubRegion.right, 
                                 dispSubRegion.bottom, 
                                 rawScreenHeight - dispSubRegion.left);
                break;
            default:
                rawSubRegion.set(dispSubRegion);
                break;
        }
        return rawSubRegion;
    }
    
    
    public static Bitmap takeScreenshot(Context context) {
        return ScreenshotUtils.takeScreenshot(context, null);
    }

    public static Bitmap takeScreenshot(Context context, Rect subRegion) {
        Bitmap bmpRawScreen = null;
        
        long tickInit = System.currentTimeMillis();
        final Matrix matrix = new Matrix();
        final WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        
        int rawScreenWidth = displayMetrics.widthPixels;
        int rawScreenHeight = displayMetrics.heightPixels;
        int dispScreenWidth = displayMetrics.widthPixels;
        int dispScreenHeight = displayMetrics.heightPixels;
        
        final int rotate = display.getRotation();
        final float degrees = getDegreesForRotation(rotate);
        final boolean requiresRotation = (degrees > 0);
        if (requiresRotation) {
        	float[] dimens = {rawScreenWidth, rawScreenHeight};
            matrix.reset();
            matrix.preRotate(-degrees);
            matrix.mapPoints(dimens);
            rawScreenWidth = (int)Math.abs(dimens[0]);
            rawScreenHeight = (int)Math.abs(dimens[1]);
        }

        // Take the screenshot
        try {
            Method method = Class.forName("android.view.SurfaceControl").getMethod("screenshot", int.class, int.class); 
            bmpRawScreen = (Bitmap)method.invoke(null, rawScreenWidth, rawScreenHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bmpRawScreen == null) {
            return null;
        }
        
        long tickShotDone = System.currentTimeMillis();
        // Check subRegion
        if (subRegion != null) {
            if (subRegion.left < 0 || subRegion.top < 0 
                || subRegion.right <= subRegion.left || subRegion.bottom <= subRegion.top
                || subRegion.width() > dispScreenWidth || subRegion.height() > dispScreenHeight) {
                subRegion = null;
            }
        }

        if (requiresRotation || subRegion != null) {
            Rect rectDispSubRegion = subRegion != null ? new Rect(subRegion) : new Rect(0, 0, dispScreenWidth, dispScreenHeight);
            Rect rectRawSubRegion = calRawSubRegion(rotate, rawScreenWidth, rawScreenHeight, rectDispSubRegion);
            Rect rectRawBounds = new Rect(0, 0, rectRawSubRegion.width(), rectRawSubRegion.height());
            Bitmap bmpDispSubRegion = Bitmap.createBitmap(rectDispSubRegion.width(), rectDispSubRegion.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmpDispSubRegion);
            canvas.translate(rectDispSubRegion.width() / 2.0f, rectDispSubRegion.height() / 2.0f);
            canvas.rotate(degrees);
            canvas.translate(-rectRawSubRegion.width() / 2.0f, -rectRawSubRegion.height() / 2.0f);
            canvas.drawBitmap(bmpRawScreen, rectRawSubRegion, rectRawBounds, null);
            canvas.setBitmap(null);
            bmpRawScreen.recycle();
            bmpRawScreen = bmpDispSubRegion;
        }
        
        long tickRotateSubDone = System.currentTimeMillis();
        // Optimizations
        bmpRawScreen.setHasAlpha(false);
        bmpRawScreen.prepareToDraw();
        
        long tickAllDone = System.currentTimeMillis();
        Log.d("Snser", "takeScreenshot total=" + (tickAllDone - tickInit) + "ms");
        Log.d("Snser", "takeScreenshot shot=" + (tickShotDone - tickInit) + "ms");
        Log.d("Snser", "takeScreenshot rotate&SubRegion=" + (tickRotateSubDone - tickShotDone) + "ms");
        Log.d("Snser", "takeScreenshot optm=" + (tickAllDone - tickRotateSubDone) + "ms");
        return bmpRawScreen;
    }

}
