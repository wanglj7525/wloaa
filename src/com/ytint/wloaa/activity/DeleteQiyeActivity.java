package com.ytint.wloaa.activity;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.People;

public class DeleteQiyeActivity extends AbActivity {
	String TAG = "AddXiapaiActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	private long people = 0;
	private List<People> peoples;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("企业信息-删除企业");
		mAbTitleBar.setLogo(R.drawable.button_selector_back); 
//		 设置文字边距，常用来控制高度：
		 mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//		 设置标题栏背景：
		 mAbTitleBar.setTitleBarBackground(R.drawable.abg_top); 
//		 左边图片右边的线：
		 mAbTitleBar.setLogoLine(R.drawable.aline);
//		  左边图片的点击事件：
		 mAbTitleBar.getLogoView().setOnClickListener(new View.OnClickListener() {
		     @Override
		     public void onClick(View v) {
		        finish();
		     }

		 }); 
		 
		setAbContentView(R.layout.layout_deleteqiye);
		context = DeleteQiyeActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		
		initUi();
		
	}

	private void initUi() {
		
//		search_close.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
		
		
//		OnClickListener keyboard_hide = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				InputMethodManager imm = (InputMethodManager) DeleteQiyeActivity.this
//						.getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//			}
//
//		};
//		addxiapai_full.setClickable(true);
//		addxiapai_full.setOnClickListener(keyboard_hide);
	}

}