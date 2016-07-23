package cc.snser.share.viewbase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.viewpager_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    startActivity(new Intent(MainActivity.this, ViewPagerViewActivity.class));
			}
		});
        findViewById(R.id.viewpager_fragment).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    startActivity(new Intent(MainActivity.this, ViewPagerFragmentActivity.class));
			}
		});
        findViewById(R.id.viewpager_scroll_control).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    startActivity(new Intent(MainActivity.this, ViewPagerScrollControlActivity.class));
			}
		});
        
        findViewById(R.id.listview_raw).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    startActivity(new Intent(MainActivity.this, ListViewRawActivity.class));
			}
		});
        findViewById(R.id.listview_menu).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    startActivity(new Intent(MainActivity.this, ListViewMenuActivity.class));
			}
		});
        findViewById(R.id.listview_multi_type).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    startActivity(new Intent(MainActivity.this, ListViewMultiTypeActivity.class));
			}
		});
        findViewById(R.id.listview_pull_to_refresh).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    startActivity(new Intent(MainActivity.this, ListViewPullToRefreshActivity.class));
			}
		});
        findViewById(R.id.listview_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListViewInputActivity.class));
            }
        });
    }
    
}

/*
viewpager
viewpager场景和实例，点点点和底部tab不是viewpager哦
viewpager+view 轮播图/引导页
viewpager+fragment 复杂页面/设置页
两级viewpager
viewpager 禁止滑动/循环滑动

listview
listview场景和实例
实现最普通的listview - adapter
listview多种item混搭以及点击item出菜单
pulltorefresh list view
listview性能优化
*/
