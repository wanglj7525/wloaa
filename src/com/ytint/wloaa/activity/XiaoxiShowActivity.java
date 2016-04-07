package com.ytint.wloaa.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	Integer message_id;
	String message;
	Integer push_user_id;
	String push_user_name;
	private Qunfa shenpi = new Qunfa();
	@AbIocView(id = R.id.showTitle)
	TextView showTitle;
	@AbIocView(id = R.id.zhuanfa)
	Button zhuanfa;
	@AbIocView(id = R.id.msg_content)
	TextView msg_content;
	@AbIocView(id = R.id.msg_title)
	TextView msg_title;
	@AbIocView(id = R.id.msg_topeople)
	TextView msg_topeople;
	@AbIocView(id = R.id.msgun_topeople)
	TextView msgun_topeople;
	@AbIocView(id = R.id.msg_frompeople)
	TextView msg_frompeple;
	@AbIocView(id = R.id.to_msg)
	Button to_msg;
	@AbIocView(id = R.id.showtopeople)
	LinearLayout showtopeople;
	@AbIocView(id = R.id.showtounpeople)
	LinearLayout showtounpeople;

	@AbIocView(id = R.id.addShenpi)
	RelativeLayout addShenpi;

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
		application = (MyApplication) this.getApplication();
		host = URLs.HTTP + application.getProperty("HOST") + ":"
				+ application.getProperty("PORT");
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		context = XiaoxiShowActivity.this;
		loginKey = application.getProperty("loginKey");
		message_id = Integer.parseInt(intent.getExtras().get("message_id")
				.toString());
		setAbContentView(R.layout.layout_showmessage);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		if (from == 0) {
			showTitle.setText("公告详情");
			to_msg.setVisibility(View.GONE);
			zhuanfa.setVisibility(View.GONE);
		} else {
			showTitle.setText("消息详情");
			// 转发
			zhuanfa.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String msg = msg_content.getText().toString();

					Intent intent = new Intent(XiaoxiShowActivity.this,
							SendXiaoGaoActivity.class);
					intent.putExtra("from", 4);
					intent.putExtra("msg_info", msg);
					startActivity(intent);
				}
			});
		}

		loadDatas();

		addShenpi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		to_msg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(XiaoxiShowActivity.this,
						SendXiaoGaoActivity.class);
				intent.putExtra("from", 3);
				intent.putExtra("message_id", message_id);
				intent.putExtra("message", message);
				intent.putExtra("push_user_id", push_user_id);
				intent.putExtra("push_user_name", push_user_name);
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
		String url = host + URLs.MSGDETAIL + "?id=" + message_id + "&user_id="
				+ loginKey;
		Log.e(TAG, url);
		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {
				Log.e(TAG, content);
				try {

					QunfaInfo gList = QunfaInfo.parseJson(content);
					if (gList.code == 200) {
						shenpi = gList.getInfo();
						msg_content.setText(shenpi.content);
						msg_title.setText(shenpi.title);
						msg_frompeple.setText(shenpi.push_user_name);
						msg_topeople.setText(shenpi.receive_user_id);
						message = shenpi.title;
						push_user_id = shenpi.push_user_id;
						push_user_name = shenpi.push_user_name;
						if (loginKey.equals(shenpi.push_user_id + "")) {
							to_msg.setVisibility(View.GONE);
							// 公告发送者可以查看 已读 未读人信息
							msg_topeople.setText(shenpi.receive_user_names
									.length() == 0 ? "无"
									: shenpi.receive_user_names);
							msgun_topeople.setText(shenpi.unreceive_user_names
									.length() == 0 ? "无"
									: shenpi.unreceive_user_names);
						} else {
							showtopeople.setVisibility(View.GONE);
							showtounpeople.setVisibility(View.GONE);
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