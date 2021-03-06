package cc.snser.share.viewbase.components;

import cc.snser.share.viewbase.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TitleFragment extends Fragment {
    private View mRootView;
    private TextView mTxt;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Snser", "TitleFragment onCreateView");
        mRootView = inflater.inflate(R.layout.viewpager_fragment_title, container, false);
        initView(mRootView);
        return mRootView;
    }
    
    private void initView(View root) {
        mTxt = (TextView)root.findViewById(R.id.viewpager_fragment_title_txt);
    }
    
    public void setCurrentTab(int tab) {
        if (mTxt != null) {
            switch (tab) {
                case 0:
                    mTxt.setText("#ClickFragment");
                    break;
                case 1:
                    mTxt.setText("#DateFragment");
                    break;
                case 2:
                    mTxt.setText("#AnimFragment");
                    break;
                default:
                    break;
            }
        }
    }
}
