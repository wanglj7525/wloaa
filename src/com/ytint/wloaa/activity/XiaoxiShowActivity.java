package com.ytint.wloaa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.app.MyApplication;

public class XiaoxiShowActivity extends AbActivity {
	String TAG = "XiaoxiShowActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	private int from;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		AbTitleBar mAbTitleBar = this.getTitleBar();
		if (from == 1) {
			mAbTitleBar.setTitleText("消息公告-发布公告");
		} else if (from == 2) {
			mAbTitleBar.setTitleText("消息公告-公告列表");
		} else if (from == 3){
			mAbTitleBar.setTitleText("消息公告-群发消息");
		}else if (from == 4){
			mAbTitleBar.setTitleText("消息公告-消息列表");
		}else{
			mAbTitleBar.setTitleText("消息公告-公告消息列表");
		}
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

		setAbContentView(R.layout.layout_showmessage);
		context = XiaoxiShowActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");

	}

}