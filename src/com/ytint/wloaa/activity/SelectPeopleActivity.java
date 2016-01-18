package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.URLs;

/**
 * 选择联系人
 * 
 * @author wlj
 * 
 */
public class SelectPeopleActivity extends AbActivity {

	String TAG = "SelectPeopleActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	String host;
	@AbIocView(id = R.id.listView_people_list)
	ListView listView_people_list;
	private List<String> peoplelist = null;
	private List<String> groupkey = new ArrayList<String>();
	private List<String> aList = new ArrayList<String>();
	private List<String> bList = new ArrayList<String>();

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (MyApplication) this.getApplication();
		host = URLs.HTTP + application.getProperty("HOST") + ":"
				+ application.getProperty("PORT");
		Intent intent = getIntent();
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("选择联系人");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		// 设置文字边距，常用来控制高度：
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// 设置标题栏背景：
		mAbTitleBar.setTitleBarBackground(R.drawable.abg_top);
		// 左边图片右边的线：
		mAbTitleBar.setLogoLine(R.drawable.aline);
		// 左边图片的点击事件：
		mAbTitleBar.getLogoView().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}

				});

		setAbContentView(R.layout.layout_select_people);
		context = SelectPeopleActivity.this;
		loginKey = application.getProperty("loginKey");
		initData();
		MyAdapter adapter = new MyAdapter();
		listView_people_list.setAdapter(adapter);
	}

	public void initData() {
		peoplelist = new ArrayList<String>();  
        
        groupkey.add("A组");  
        groupkey.add("B组");  
          
        for(int i=0; i<10; i++){  
            aList.add("A组"+i);  
        }  
        peoplelist.add("A组");  
        peoplelist.addAll(aList);  
          
        for(int i=0; i<18; i++){  
            bList.add("B组"+i);  
        }  
        peoplelist.add("B组");  
        peoplelist.addAll(bList);  
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return peoplelist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return peoplelist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			if (groupkey.contains(getItem(position))) {
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			TextView text;
			if (groupkey.contains(getItem(position))) {
				view = LayoutInflater.from(getApplicationContext()).inflate(
						R.layout.addpeople_list_item_tag, null);
				text=(TextView)view.findViewById(R.id.addpeople_list_item_text);
			} else {
				view = LayoutInflater.from(getApplicationContext()).inflate(
						R.layout.addpeople_list_item, null);
				text=(TextView)view.findViewById(R.id.people_one);
			}
			text.setText((CharSequence) getItem(position));
//			TextView text = (TextView) view
//					.findViewById(R.id.addpeople_list_item_tagtext);
//			text.setText((CharSequence) getItem(position));
			return view;
		}

	}

}
