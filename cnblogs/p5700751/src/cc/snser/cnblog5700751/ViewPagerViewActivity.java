package cc.snser.cnblog5700751;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPagerViewActivity extends Activity implements View.OnClickListener {
    
    private ViewPager mPager;
    private ImageView mImgPoint;
    
    private SparseArray<View> mPageCache = new SparseArray<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_view);
        initView();
    }
    
    private void initView() {
        mPager = (ViewPager)findViewById(R.id.viewpager_view_pager);
        mImgPoint = (ImageView)findViewById(R.id.viewpager_view_point);
        mPager.setAdapter(new ViewPagerAdapter(ViewPagerViewActivity.this));
        mPager.addOnPageChangeListener(new OnViewPageChangeListener());
        findViewById(R.id.viewpager_view_register).setOnClickListener(this);
        findViewById(R.id.viewpager_view_login).setOnClickListener(this);
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
            View page = mPageCache.get(position);
            if (page == null) {
                page = mInflater.inflate(R.layout.viewpager_view_page, container, false);
                TextView txtTitle = (TextView)page.findViewById(R.id.viewpager_view_page_title);
                ImageView imgContent = (ImageView)page.findViewById(R.id.viewpager_view_page_content);
                switch (position) {
                    case 0:
                        txtTitle.setText(R.string.viewpager_view_page_title_1);
                        imgContent.setImageResource(R.drawable.viewpager_view_page_content_1);
                        break;
                    case 1:
                        txtTitle.setText(R.string.viewpager_view_page_title_2);
                        imgContent.setImageResource(R.drawable.viewpager_view_page_content_2);
                        break;
                    case 2:
                        txtTitle.setText(R.string.viewpager_view_page_title_3);
                        imgContent.setImageResource(R.drawable.viewpager_view_page_content_3);
                        break;
                    default:
                        break;
                }
                mPageCache.append(position, page);
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
                    break;
                case 1:
                    mImgPoint.setImageResource(R.drawable.viewpager_view_point_2);
                    break;
                case 2:
                    mImgPoint.setImageResource(R.drawable.viewpager_view_point_3);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
