package cc.snser.share.viewbase;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewRawActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_raw);
        
        ArrayList<Integer> data = new ArrayList<Integer>();
        for (int index = 0; index != 40; ++index) {
            data.add(index);
        }
        
        ListView list = (ListView)findViewById(R.id.listview_raw_list);
        //list.setAdapter(new ListViewRawAdapter(this, data)); //最原始的Adapter
        list.setAdapter(new ListViewOptmAdapter(this, data)); //经过优化的Adapter
    }
    
    private class ListViewRawAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<Integer> mListData;
        
        public ListViewRawAdapter(Context context, ArrayList<Integer> data) {
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
            View viewRoot = mLayoutInflater.inflate(R.layout.listview_raw_item, null);
            if (viewRoot != null) {
                TextView txt = (TextView)viewRoot.findViewById(R.id.listview_raw_item_txt);
                txt.setText(getItem(position) + "");
                if (position % 2 == 0) {
                    txt.setTextColor(Color.rgb(0x00, 0x80, 0xFF));
                }
            }
            return viewRoot;
        }
    }
    
    private class ListViewOptmAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<Integer> mListData;
        
        private class ViewHolder {
            public ViewHolder (View viewRoot) {
                txt = (TextView)viewRoot.findViewById(R.id.listview_raw_item_txt);
            }
            public TextView txt;
        }
        
        public ListViewOptmAdapter(Context context, ArrayList<Integer> data) {
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
                //第三个参数attachToRoot要传false，否则inflate过程中，会调用parent.addView(temp, params)从而抛出异常
                //异常原因是：parent(ListView)继承自AdapterView，而AdapterView在addView方法直接抛出异常
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
