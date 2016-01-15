package com.ytint.wloaa.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.Project;
import com.ytint.wloaa.bean.URLs;

/**
 * 项目详情
 * 
 * @author wlj
 * 
 */
public class ProjectDetailActivity extends AbActivity {
	String TAG = "ProjectDetailActivity";
	private MyApplication application;
	private AbImageDownloader mAbImageDownloader = null;
	Context context = null;
	String host;

	static class ViewHolder {
		ImageView mImg;
	}

	@AbIocView(id = R.id.project_name)
	TextView project_name_detail;
	@AbIocView(id = R.id.project_address)
	TextView project_address;
	@AbIocView(id = R.id.project_starttime)
	TextView project_starttime;
	@AbIocView(id = R.id.project_endtime)
	TextView project_endtime;
	private String userType;
	Integer project_id;
	private Project project = new Project();

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
		AbTitleBar mAbTitleBar = this.getTitleBar();
		Intent intent = getIntent();
		mAbTitleBar.setTitleText("工程详情");
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

		setAbContentView(R.layout.layout_projectdetail);
		context = ProjectDetailActivity.this;
		userType = application.getProperty("userType");
		project_id = intent.getIntExtra("project_id", 0);
		loadDatas();
	}


	@SuppressLint("NewApi")
	private void loadDatas() {

		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		final String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		// mAbHttpUtil.get(host + URLs.SHENPIDETAIL + "?id=" + shenpi_id,
		// new AbStringHttpResponseListener() {
		// // 获取数据成功会调用这里
		// @Override
		// public void onSuccess(int statusCode, String content) {
		// Log.d(TAG, content);
		// try {
		//
		// ProjectInfo cList = ProjectInfo.parseJson(content);
		// if (cList.code == 200) {
		// project = cList.getInfo();
		// project_name_detail.setText(project.name);
		// project_address.setText(project.address);
		// project_starttime.setText(project.starttime);
		// project_endtime.setText(project.endtime);
		//
		// } else {
		// UIHelper.ToastMessage(context, cList.msg);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// showToast("数据解析失败");
		// }
		// };
		//
		// // 开始执行前
		// @Override
		// public void onStart() {
		// // 显示进度框
		// showProgressDialog();
		// }
		//
		// @Override
		// public void onFailure(int statusCode, String content,
		// Throwable error) {
		// showToast("网络连接失败！");
		// }
		//
		// // 完成后调用，失败，成功
		// @Override
		// public void onFinish() {
		// // 移除进度框
		// removeProgressDialog();
		// };
		//
		// });
	}
}
