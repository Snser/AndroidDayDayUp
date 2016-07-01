package cc.snser.share.viewbase;

import java.util.ArrayList;

import cc.snser.share.viewbase.components.pulltorefresh.ILoadingLayout;
import cc.snser.share.viewbase.components.pulltorefresh.PullToRefreshBase;
import cc.snser.share.viewbase.components.pulltorefresh.PullToRefreshBase.Mode;
import cc.snser.share.viewbase.components.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import cc.snser.share.viewbase.components.pulltorefresh.PullToRefreshListView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//https://github.com/chrisbanes/Android-PullToRefresh

public class ListViewPullToRefreshActivity extends Activity {
    private PullToRefreshListView mList;
    
    private static Handler sHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_ptr);
        
        ArrayList<Integer> data = new ArrayList<Integer>();
        for (int index = 0; index != 40; ++index) {
            data.add(index);
        }
        
        mList = (PullToRefreshListView)findViewById(R.id.listview_ptr_list);
        mList.setMode(Mode.BOTH);
        mList.setOnRefreshListener(new OnListRefreshListener());
        mList.setAdapter(new ListViewAdapter(this, data));
        
        ILoadingLayout footerLayout = mList.getLoadingLayoutProxy(false, true);
        footerLayout.setPullLabel("上拉加载更多");
        footerLayout.setReleaseLabel("松手加载更多");
        footerLayout.setRefreshingLabel("正在加载更多...");
    }
    
    private class OnListRefreshListener implements OnRefreshListener2<ListView> {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mList.onRefreshComplete();
                    Toast.makeText(ListViewPullToRefreshActivity.this, "下拉刷新完成", Toast.LENGTH_SHORT).show();
                }
            }, 3000);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mList.onRefreshComplete();
                    Toast.makeText(ListViewPullToRefreshActivity.this, "上拉加载更多完成", Toast.LENGTH_SHORT).show();
                }
            }, 3000);
        }
    }
    
    private class ListViewAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<Integer> mListData;
        
        private class ViewHolder {
            public ViewHolder (View viewRoot) {
                txt = (TextView)viewRoot.findViewById(R.id.listview_raw_item_txt);
            }
            public TextView txt;
        }
        
        public ListViewAdapter(Context context, ArrayList<Integer> data) {
            mLayoutInflater = LayoutInflater.from(context);
            mListData = data;
        }
        
        @Override
        public int getCount() {
            return mListData == null ? 0 : mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData == null ? 0 : mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.listview_raw_item, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            if (convertView != null && convertView.getTag() instanceof ViewHolder) {
                ViewHolder holder = (ViewHolder)convertView.getTag();
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
}
