package com.ytint.wloaa.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Qunfa;
import com.ytint.wloaa.bean.QunfaInfo;
import com.ytint.wloaa.bean.URLs;

public class XiaoxiShowActivity extends AbActivity {
	String TAG = "XiaoxiShowActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	private int from;
	Integer shenpi_id;
	Integer push_user_id;
	private Qunfa shenpi = new Qunfa();
	@AbIocView(id = R.id.msg_content)
	TextView msg_content;
	@AbIocView(id = R.id.msg_title)
	TextView msg_title;
	@AbIocView(id = R.id.to_msg)
	Button to_msg;
	String host;
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
		application= (MyApplication) this.getApplication();
		host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		AbTitleBar mAbTitleBar = this.getTitleBar();
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
		if (from == 0) {
			mAbTitleBar.setTitleText("公告详情");
			to_msg.setVisibility(View.GONE);
		} else {
			mAbTitleBar.setTitleText("消息详情");
		}
		context = XiaoxiShowActivity.this;
		loginKey = application.getProperty("loginKey");

		shenpi_id=Integer.parseInt(intent.getExtras().get("shenpi_id").toString());
		loadDatas();
		
		to_msg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(XiaoxiShowActivity.this,AddXiaoxiSendActivity.class);
				intent.putExtra("from", 3);
				intent.putExtra("shenpi_id", shenpi_id);
				intent.putExtra("push_user_id", push_user_id);
				startActivity(intent);
				
			}
		});
	}
	
	@SuppressLint("NewApi")
	private void loadDatas() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		final String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(host+URLs.MSGDETAIL + "?id=" + shenpi_id,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							
							QunfaInfo gList = QunfaInfo
									.parseJson(content);
							if (gList.code == 200) {
								shenpi = gList.getInfo();
								msg_content.setText(shenpi.content);
								msg_title.setText(shenpi.title);
								push_user_id=shenpi.push_user_id;
								if (loginKey.equals(shenpi.push_user_id+"")) {
									to_msg.setVisibility(View.GONE);
								}
							} else {
								UIHelper.ToastMessage(context, gList.msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							showToast("数据解析失败");
						}
					};

					// 开始执行前
					@Override
					public void onStart() {
						// 显示进度框
						showProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showToast("网络连接失败！");
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						// 移除进度框
						removeProgressDialog();
					};

				});
	}

}