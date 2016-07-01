package cc.snser.share.viewbase;

import java.util.ArrayList;

import cc.snser.share.viewbase.components.KeyboardObserver;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewInputActivity extends FragmentActivity {

    //https://github.com/Jacksgong/JKeyboardPanelSwitch
    //https://code.google.com/p/android/issues/detail?id=4203
    
    private View mViewRoot;
    private View mViewPanel;
    private EditText mEditInput;
    private Button mBtnShowPanel;
    
    private KeyboardObserver mKeyboardObserver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_input);
        
        ArrayList<Integer> data = new ArrayList<Integer>();
        for (int index = 0; index != 40; ++index) {
            data.add(index);
        }
        
        mKeyboardObserver = new KeyboardObserver(this, dip2px(this, 200.0f));
        
        final ListView list = (ListView)findViewById(R.id.listview_input_list);
        list.setAdapter(new ListViewOptmAdapter(this, data));
        
        mViewRoot = findViewById(R.id.listview_input_root);
        
        mViewPanel = findViewById(R.id.listview_input_panel);
        
        mEditInput = (EditText) findViewById(R.id.listview_input_input);
        mEditInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("Snser", "setOnFocusChangeListener hasFocus=" + hasFocus);
/*                if (hasFocus) {
                    list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    //list.setSelection(list.getCount() - 1);
                } else {
                    list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                }*/
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    InputMethodManager imm = (InputMethodManager)mEditInput.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );     
                    if ( imm.isActive( ) ) {     
                        //imm.hideSoftInputFromWindow( input.getApplicationWindowToken( ) , 0 );   
                        //list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                    }    
                }
                Log.d("Snser", "onScrollStateChanged scrollState=" + scrollState);
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                
            }
        });
        mBtnShowPanel = (Button)findViewById(R.id.listview_input_show_panel);
        mBtnShowPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isKeyboardVisiable = isKeyboardVisiable();
                boolean isPanelVisiable = isPanelVisiable();
                Log.d("Snser", "isKeyboardVisiable=" + isKeyboardVisiable + " isPanelVisiable=" + isPanelVisiable);
                if (isKeyboardVisiable) {
                    if (isPanelVisiable) {
                        setKeyboardVisiable(false);
                    } else {
                        //ok
                        setWindowAutoResize(false);
                        setPanelVisiable(true);
                        setKeyboardVisiable(false);
                    }
                } else {
                    if (isPanelVisiable) {
                        //ok
                        setKeyboardVisiable(true);
/*                        setWindowAutoResize(true);
                        setPanelVisiable(false);*/
                    } else {
                        //ok
                        setPanelVisiable(true);
                        setWindowAutoResize(true);
                    }
                }
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        if (!isKeyboardVisiable() && isPanelVisiable()) {
            setPanelVisiable(false);
        } else {
            super.onBackPressed();
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
    
    
    private boolean isPanelVisiable() {
        return mViewPanel.getLayoutParams().height > 0;
    }
    
    private void setPanelVisiable(boolean visiable) {
        ViewGroup.LayoutParams params = mViewPanel.getLayoutParams();
        params.height = visiable ? mKeyboardObserver.getKeyboardHeight() : 0;
        mViewPanel.setLayoutParams(params);
        if (visiable) {
            mBtnShowPanel.setBackgroundResource(R.drawable.wechat_icon_normal);
        } else {
            mBtnShowPanel.setBackgroundResource(R.drawable.wechat_icon_transparent);
        }
    }
    
    private boolean isKeyboardVisiable() {
        return mKeyboardObserver.isKeyboardVisiable();
    }
    
    private void setKeyboardVisiable(boolean visiable) {
        if (visiable) {
            mBtnShowPanel.setBackgroundResource(R.drawable.wechat_icon_transparent);
            mKeyboardObserver.setKeyboardVisiable(true);
        } else {
            mBtnShowPanel.setBackgroundResource(R.drawable.wechat_icon_normal);
            mKeyboardObserver.setKeyboardVisiable(false);
        }
    }
    
    private void setWindowAutoResize(boolean autoResize) {
        if (autoResize) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN 
                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN 
                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
    }
    
    private int dip2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * density + 0.5f);
    }
    
    
    
}
