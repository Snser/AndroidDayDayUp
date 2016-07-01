package cc.snser.share.viewbase;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ViewPagerViewActivity extends Activity {
    
    private ViewPager mPager;
    private ImageView mImgPoint;
    private ArrayList<Integer> mImgPointResids = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_view);
        initView();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    
    private void initView() {
        mPager = (ViewPager)findViewById(R.id.viewpager_view_pager);
        mImgPoint = (ImageView)findViewById(R.id.viewpager_view_point);
        mImgPointResids.add(R.drawable.viewpager_view_point_1);
        mImgPointResids.add(R.drawable.viewpager_view_point_2);
        mImgPointResids.add(R.drawable.viewpager_view_point_3);
        mPager.setAdapter(new ViewPagerAdapter(ViewPagerViewActivity.this));
        mPager.addOnPageChangeListener(new OnViewPageChangeListener());
    }
    
    private class ViewPagerAdapter extends PagerAdapter {
        private final int mCount = 3;
        private LayoutInflater mInflater;
        
        private ViewPagerAdapter(Context context) {
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
            View page = mInflater.inflate(R.layout.viewpager_view_page, container, false);
            ImageView imgContent = (ImageView)page.findViewById(R.id.viewpager_view_page_content);
            Button btnStart = (Button)page.findViewById(R.id.viewpager_view_page_start);
            
            switch (position) {
                case 0:
                    imgContent.setImageResource(R.drawable.viewpager_view_page_content_1);
                    break;
                case 1:
                    imgContent.setImageResource(R.drawable.viewpager_view_page_content_2);
                    break;
                case 2:
                    imgContent.setImageResource(R.drawable.viewpager_view_page_content_3);
                    btnStart.setVisibility(View.VISIBLE);
                    btnStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewPagerViewActivity.this.finish();
                        }
                    });
                    break;
                default:
                    break;
            }
            
            container.addView(page);
            return page;
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
    
    private class OnViewPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    mImgPoint.setImageResource(R.drawable.viewpager_view_point_1);
                    mImgPoint.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mImgPoint.setImageResource(R.drawable.viewpager_view_point_2);
                    mImgPoint.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    mImgPoint.setImageResource(R.drawable.viewpager_view_point_3);
                    mImgPoint.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
