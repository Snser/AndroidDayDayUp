package cc.snser.cnblog5539749;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewMultiTypeActivity extends Activity {

    private ListView mList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_multi_type);
        
        JsonListData data = null;
        try {
            InputStream is = getResources().getAssets().open("listview_multitype_data.json");
            InputStreamReader isr = new InputStreamReader(is);
            Gson gson = new GsonBuilder().serializeNulls().create();
            data = gson.fromJson(isr, JsonListData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (data != null && data.messages != null) {
            mList = (ListView)findViewById(R.id.listview_multi_type_list);
            mList.setAdapter(new MultiTypeAdapter(ListViewMultiTypeActivity.this, data.messages));
        }
    }
    
    private static class JsonListData {
        public static class Message {
            public static final int TYPE_COUNT = 3;
            public static final int TYPE_DATE = 0x00;
            public static final int TYPE_TXT_SENT = 0x01;
            public static final int TYPE_TXT_RECV = 0x02;
            public int type;
            public String txt;
            public long time;
        }
        public List<Message> messages = new ArrayList<Message>();
    }
    
    private class MultiTypeAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<JsonListData.Message> mMessages;
        private SimpleDateFormat mSdfDate = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        
        public MultiTypeAdapter(Context context, List<JsonListData.Message> messages) {
            mInflater = LayoutInflater.from(context);
            mMessages = messages;
        }
        
        private class DateViewHolder {
            public DateViewHolder(View viewRoot) {
                date = (TextView)viewRoot.findViewById(R.id.listview_multi_type_item_date_txt);
            }
            public TextView date;
        }
        
        private class TxtSentViewHolder {
            public TxtSentViewHolder(View viewRoot) {
                txt = (TextView)viewRoot.findViewById(R.id.listview_multi_type_item_txt_sent_txt);
            }
            public TextView txt;
        }
        
        private class TxtRecvViewHolder {
            public TxtRecvViewHolder(View viewRoot) {
                txt = (TextView)viewRoot.findViewById(R.id.listview_multi_type_item_txt_recv_txt);
            }
            public TextView txt;
        }
        
        @Override
        public int getViewTypeCount() {
            return JsonListData.Message.TYPE_COUNT;
        }
        
        @Override
        public int getItemViewType(int position) {
            return getItem(position).type;
        }
        
        @Override
        public int getCount() {
            return mMessages.size();
        }

        @Override
        public JsonListData.Message getItem(int position) {
            return mMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case JsonListData.Message.TYPE_DATE:
                    return handleGetDateView(position, convertView, parent);
                case JsonListData.Message.TYPE_TXT_SENT:
                    return handleGetTxtSentView(position, convertView, parent);
                case JsonListData.Message.TYPE_TXT_RECV:
                    return handleGetTxtRecvView(position, convertView, parent);
                default:
                    return null;
            }
        }
        
        private View handleGetDateView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview_multi_type_item_date, parent, false);
                convertView.setTag(new DateViewHolder(convertView));
            }
            if (convertView != null && convertView.getTag() instanceof DateViewHolder) {
                final DateViewHolder holder = (DateViewHolder)convertView.getTag();
                holder.date.setText(formatTime(getItem(position).time));
            }
            return convertView;
        }
        
        private View handleGetTxtSentView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview_multi_type_item_txt_sent, parent, false);
                convertView.setTag(new TxtSentViewHolder(convertView));
            }
            if (convertView != null && convertView.getTag() instanceof TxtSentViewHolder) {
                final TxtSentViewHolder holder = (TxtSentViewHolder)convertView.getTag();
                holder.txt.setText(getItem(position).txt);
            }
            return convertView;
        }
        
        private View handleGetTxtRecvView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview_multi_type_item_txt_recv, parent, false);
                convertView.setTag(new TxtRecvViewHolder(convertView));
            }
            if (convertView != null && convertView.getTag() instanceof TxtRecvViewHolder) {
                final TxtRecvViewHolder holder = (TxtRecvViewHolder)convertView.getTag();
                holder.txt.setText(getItem(position).txt);
            }
            return convertView;
        }
        
        private String formatTime(long time) {
            return mSdfDate.format(new Date(time * 1000));
        }
    }
    
}
