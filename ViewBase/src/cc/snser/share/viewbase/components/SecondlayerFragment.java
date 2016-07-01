package cc.snser.share.viewbase.components;

import java.util.ArrayList;
import java.util.List;
import cc.snser.share.viewbase.R;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SecondlayerFragment extends PagerFragment {
    
    private ListView mList;
    private ViewPagerAdapter mPagerAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.viewpager_fragment_secondlayer, container, false);
        initView(viewRoot);
        return viewRoot;
    }
    
    private void initView(View root) {
        mPagerAdapter =  new ViewPagerAdapter(this.getContext());
        
        final List<Integer> messages = new ArrayList<Integer>();
        for (int m = 0; m != 40; ++m) {
            messages.add(m);
        }
        mList = (ListView)root.findViewById(R.id.viewpager_fragment_secondlayer_list);
        mList.setAdapter(new MultiTypeAdapter(getContext(), messages));
    }
    
    private class MultiTypeAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<Integer> mMessages;
        
        public MultiTypeAdapter(Context context, List<Integer> messages) {
            mInflater = LayoutInflater.from(context);
            mMessages = messages;
        }
        
        private class PagerViewHolder {
            public PagerViewHolder(View viewRoot) {
                pager = (ScrollControlViewPager)viewRoot.findViewById(R.id.viewpager_fragment_secondlayer_list_item_pager);
            }
            public ScrollControlViewPager pager;
        }
        
        private class NormalViewHolder {
            public NormalViewHolder(View viewRoot) {
                txt = (TextView)viewRoot.findViewById(R.id.viewpager_fragment_secondlayer_list_item_normal);
            }
            public TextView txt;
        }
        
        @Override
        public int getViewTypeCount() {
            return 2;
        }
        
        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }
        
        @Override
        public int getCount() {
            return mMessages.size();
        }

        @Override
        public Integer getItem(int position) {
            return mMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case 0:
                    return handleGetPagerView(position, convertView, parent);
                case 1:
                    return handleGetNormalView(position, convertView, parent);
                default:
                    return null;
            }
        }
        
        private View handleGetPagerView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.viewpager_fragment_secondlayer_list_item_pager, parent, false);
                convertView.setTag(new PagerViewHolder(convertView));
            }
            if (convertView != null && convertView.getTag() instanceof PagerViewHolder) {
                final PagerViewHolder holder = (PagerViewHolder)convertView.getTag();
                holder.pager.setAdapter(mPagerAdapter, true);
                holder.pager.setAutoScrollEnabled(true);
                holder.pager.setCurrentItem(0);
            }
            return convertView;
        }
        
        private View handleGetNormalView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.viewpager_fragment_secondlayer_list_item_normal, parent, false);
                convertView.setTag(new NormalViewHolder(convertView));
            }
            if (convertView != null && convertView.getTag() instanceof NormalViewHolder) {
                final NormalViewHolder holder = (NormalViewHolder)convertView.getTag();
                holder.txt.setText(String.valueOf(getItem(position)));
                if (position % 2 == 0) {
                    holder.txt.setTextColor(Color.rgb(0x00, 0x80, 0xFF));
                } else {
                    holder.txt.setTextColor(Color.rgb(0x00, 0x00, 0x00));
                }
            }
            return convertView;
        }
        
    }
    
    private class ViewPagerAdapter extends PagerAdapter {
        private final int mCount = 3;
        private ArrayList<View> mViews = new ArrayList<View>();
        private LayoutInflater mInflater;
        
        public ViewPagerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }
        
        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.viewpager_scroll_control_page, container, false);
            Button btn = (Button)view.findViewById(R.id.viewpager_scroll_control_page_btn);
            btn.setText("page " + (position));
            switch (position) {
                case 0:
                    btn.setBackgroundColor(Color.argb(0x80, 0xFF, 0xFF, 0x00));
                    break;
                case 1:
                    btn.setBackgroundColor(Color.argb(0x80, 0xFF, 0x00, 0xFF));
                    break;
                case 2:
                    btn.setBackgroundColor(Color.argb(0x80, 0x00, 0xFF, 0xFF));
                    break;
                default:
                    break;
            }
            //这里的addView操作实际上会被ScrollControlViewPager拦截掉
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }
    }
    
}
