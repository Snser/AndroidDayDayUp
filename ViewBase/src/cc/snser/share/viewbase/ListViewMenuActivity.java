package cc.snser.share.viewbase;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewMenuActivity extends Activity {
    private int mExpandedMenuPos = -1;
    private ListViewAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_menu);
        
        ArrayList<Integer> data = new ArrayList<Integer>();
        for (int index = 0; index != 40; ++index) {
            data.add(index);
        }
        
        ListView list = (ListView)findViewById(R.id.listview_menu_list);
        list.setAdapter(mAdapter = new ListViewAdapter(this, data));
        list.setOnItemClickListener(new OnListItemClickListenser());
    }
    
    private class ListViewAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<Integer> mListData;
        
        private OnMenuClickListenser mOnMenuClickListenser = new OnMenuClickListenser();
        
        private class ViewHolder {
            public ViewHolder (View viewRoot) {
                root = viewRoot;
                txt = (TextView)viewRoot.findViewById(R.id.listview_menu_item_txt);
                menu = viewRoot.findViewById(R.id.listview_menu_item_menu);
                btnToast = (Button)viewRoot.findViewById(R.id.listview_menu_item_menu_toast);
                btnCollapse = (Button)viewRoot.findViewById(R.id.listview_menu_item_menu_collapse);
            }
            public View root;
            public TextView txt;
            public View menu;
            public Button btnToast;
            public Button btnCollapse;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.listview_menu_item, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            if (convertView != null && convertView.getTag() instanceof ViewHolder) {
                final ViewHolder holder = (ViewHolder)convertView.getTag();
                holder.txt.setText(String.valueOf(getItem(position)));
                if (position % 2 == 0) {
                    holder.root.setBackgroundColor(0xFFC9EEFE);
                } else {
                    holder.root.setBackgroundColor(0xFFFFFFFF);
                }
                holder.menu.setVisibility(position == mExpandedMenuPos ? View.VISIBLE : View.GONE);
                holder.btnToast.setText("提示" + position);
                holder.btnCollapse.setText("收起" + position);
                holder.btnToast.setOnClickListener(mOnMenuClickListenser);
                holder.btnCollapse.setOnClickListener(mOnMenuClickListenser);
            }
            return convertView;
        }
        
        private class OnMenuClickListenser implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                final int id = v.getId();
                if (id == R.id.listview_menu_item_menu_toast) {
                    Toast.makeText(ListViewMenuActivity.this, "提示" + mExpandedMenuPos, Toast.LENGTH_SHORT).show();
                } else if (id == R.id.listview_menu_item_menu_collapse) {
                    mExpandedMenuPos = -1;
                    notifyDataSetChanged();
                }
            }
        }
    }
    
    private class OnListItemClickListenser implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == mExpandedMenuPos) {
                mExpandedMenuPos = -1;
            } else {
                mExpandedMenuPos = position;
            }
            mAdapter.notifyDataSetChanged();
        }
    }
    
}
