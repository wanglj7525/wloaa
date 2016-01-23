package com.ytint.wloaa.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Project;
import com.ytint.wloaa.bean.ProjectInfo;
import com.ytint.wloaa.bean.QunfaInfo;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.widget.TitleBar;

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
	TextView project_name;
	@AbIocView(id = R.id.project_quyu)
	TextView project_quyu;
	@AbIocView(id = R.id.project_shigong)
	TextView project_shigong;
	@AbIocView(id = R.id.project_jianshe)
	TextView project_jianshe;
	@AbIocView(id = R.id.project_jianli)
	TextView project_jianli;
	@AbIocView(id = R.id.project_address)
	TextView project_address;
	@AbIocView(id = R.id.project_mianji)
	TextView project_mianji;
	@AbIocView(id = R.id.project_jiegou)
	TextView project_jiegou;
	@AbIocView(id = R.id.project_chuangyou)
	TextView project_chuangyou;
	@AbIocView(id = R.id.project_zaojia)
	TextView project_zaojia;
	@AbIocView(id = R.id.project_cengshu)
	TextView project_cengshu;
	@AbIocView(id = R.id.project_starttime)
	TextView project_starttime;
	@AbIocView(id = R.id.project_endtime)
	TextView project_endtime;
	@AbIocView(id = R.id.project_gaodu)
	TextView project_gaodu;
	@AbIocView(id = R.id.project_chuli)
	TextView project_chuli;
	@AbIocView(id = R.id.project_jindu)
	TextView project_jindu;
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
		Intent intent = getIntent();

		setAbContentView(R.layout.layout_projectdetail);
		context = ProjectDetailActivity.this;
		userType = application.getProperty("userType");
		project_id = intent.getIntExtra("project_id", 0);

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		final TitleBar titleBar = (TitleBar) findViewById(R.id.title_barpdetail);
		titleBar.setLeftImageResource(R.drawable.back_green);
		titleBar.setLeftText("返回");
		titleBar.setLeftTextColor(Color.WHITE);
		titleBar.setLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleBar.setTitle("工程详情");
		titleBar.setTitleColor(Color.WHITE);
		titleBar.setDividerColor(Color.GRAY);
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
		String url = host + URLs.PROJECTDETAIL + "?id="+project_id;
		Log.e(TAG, url);
		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {
				Log.e(TAG, content);
				try {
					
					ProjectInfo gList = ProjectInfo.parseJson(content);
					if (gList.code == 200) {
						Project project=gList.getInfo();
						project_name.setText(show(project.name));
						project_quyu.setText(show(project.regionname+" "));
						project_shigong.setText(show(project.cname+" "));
						project_jianshe.setText(show(project.bname+" "));
						project_jianli.setText(show(project.sname+" "));
						project_address.setText(show(project.address+" "));
						project_mianji.setText(show(project.area+" "));
						project_jiegou.setText(show(project.structure+" "));
						project_chuangyou.setText(show(project.goals+" "));
						project_zaojia.setText(show(project.cost+" "));
						project_cengshu.setText(show(project.layers+" "));
						project_starttime.setText(show(project.starttimeString+" "));
						project_endtime.setText(show(project.endtimeString+" "));
						project_gaodu.setText(show(project.height+" "));
						project_chuli.setText(show(project.result+" "));
						project_jindu.setText(show(project.schedule+" "));
//						shenpi = gList.getInfo();
//						msg_content.setText(shenpi.content);
//						msg_title.setText(shenpi.title);
//						msg_frompeple.setText(shenpi.push_user_name);
//						msg_topeople.setText(shenpi.receive_user_id);
//						message = shenpi.title;
//						push_user_id = shenpi.push_user_id;
//						if (loginKey.equals(shenpi.push_user_id + "")) {
//							to_msg.setVisibility(View.GONE);
//						}
//						msg_topeople.setText(shenpi.receive_user_names);
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
	public String show(String show){
		String result="无";
		if (show.trim().length()!=0) {
			result=show;
		}
		return result;
	}
}
