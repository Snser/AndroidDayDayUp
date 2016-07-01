package cc.snser.share.viewbase;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.widget.ImageView;

public class CanvasActivity extends Activity {
	
	private ImageView mImgDispFull;
	private ImageView mImgDispRegion;
	
	int rotate = Surface.ROTATION_270;
	float degree = getDegreesForRotation(rotate);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.canvas);
		mImgDispFull = (ImageView)findViewById(R.id.canvas_disp_full);
		mImgDispRegion = (ImageView)findViewById(R.id.canvas_disp_region);
		long tick = System.currentTimeMillis();
		drawFull();
		long tickfull = System.currentTimeMillis();
		Log.d("Fudai", "drawFull use" + (tickfull - tick) + "ms");
		drawRegion();
		Log.d("Fudai", "drawRegion use" + (System.currentTimeMillis() - tickfull) + "ms");
	}
	
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
     * @param rawScreenWidth 旋转前的原始宽度
     * @param rawScreenHeight 旋转前的原始高度
     * @param dispSubRegion 旋转后的子区域
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
	
	private void drawFull() {
		Bitmap bmpRaw = ((BitmapDrawable)getResources().getDrawable(R.drawable.raw)).getBitmap();
		int rawWidth = bmpRaw.getWidth();
		int rawHeight = bmpRaw.getHeight();

		Rect rectDispRegion = new Rect(0, 0, 700, 400);
		Rect rectRawRegion = calRawSubRegion(rotate, rawWidth, rawHeight, rectDispRegion);
		Rect rectRawBounds = new Rect(0, 0, rectRawRegion.width(), rectRawRegion.height());
		
		Bitmap bmpDisp = Bitmap.createBitmap(rectDispRegion.width(), rectDispRegion.height(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmpDisp);
		
		canvas.translate(rectDispRegion.width() / 2.0f, rectDispRegion.height() / 2.0f);
		canvas.rotate(degree);
		canvas.translate(-rectRawRegion.width() / 2.0f, -rectRawRegion.height() / 2.0f);
		
		canvas.drawBitmap(bmpRaw, rectRawRegion, rectRawBounds, null);
		mImgDispFull.setImageBitmap(bmpDisp);
	}
	
	private void drawRegion() {
		Bitmap bmpRaw = ((BitmapDrawable)getResources().getDrawable(R.drawable.raw)).getBitmap();
		int rawWidth = bmpRaw.getWidth();
		int rawHeight = bmpRaw.getHeight();
		

		Rect rectDispRegion = new Rect(100, 190, 500, 380);
		Rect rectRawRegion = calRawSubRegion(rotate, rawWidth, rawHeight, rectDispRegion);
		Rect rectRawBounds = new Rect(0, 0, rectRawRegion.width(), rectRawRegion.height());
		
		Bitmap bmpDisp = Bitmap.createBitmap(rectDispRegion.width(), rectDispRegion.height(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmpDisp);
		
		canvas.translate(rectDispRegion.width() / 2.0f, rectDispRegion.height() / 2.0f);
		canvas.rotate(degree);
		canvas.translate(-rectRawRegion.width() / 2.0f, -rectRawRegion.height() / 2.0f);

		canvas.drawBitmap(bmpRaw, rectRawRegion, rectRawBounds, null);
		mImgDispRegion.setImageBitmap(bmpDisp);
	}
}
