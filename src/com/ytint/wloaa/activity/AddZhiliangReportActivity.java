package com.ytint.wloaa.activity;

import java.io.Serializable;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.PeopleList;
import com.ytint.wloaa.bean.ShenpiInfo;
import com.ytint.wloaa.bean.URLs;

public class AddZhiliangReportActivity extends AbActivity {
	String TAG = "AddShenpiActivity";
	private MyApplication application;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	private long people = 0;
	private List<People> peoples;
	
	Context context = null;
	private String loginKey;
	@AbIocView(id = R.id.task_people)
	EditText task_people;
	@AbIocView(id = R.id.addshenpi_full)
	LinearLayout addxiapai_full;
//	@AbIocView(id = R.id.select_shenpi_people)
//	Spinner peopleSpinner;
	@AbIocView(id = R.id.commitShenpi)
	Button add;
	@AbIocView(id = R.id.task_name)
	EditText task_name;
	@AbIocView(id = R.id.task_tell)
	EditText task_tell;
//	@AbIocView(id = R.id.shenpi_close)
//	TextView shenpi_close;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("质量检查-上报质检任务");
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
		 
		setAbContentView(R.layout.layout_addzhiliangreport);
		context = AddZhiliangReportActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		initUi();
		//加载联系人下拉框
		peoples = (List<People>) application.readObject("peoples");
		if(null==peoples||peoples.size()<=0){
			loadPeoples();
		}else{
			people_names = new String[peoples.size()];
			int i = 0;
			for (People cn : peoples) {
				people_names[i] = cn.name;
				i++;
			}
			initEdittext();
		}
	}
	private void initEdittext(){
		People onePeople=peoples.get(Integer.parseInt(loginKey)-1);
		task_people.setText(onePeople.name);
	}
	@SuppressLint("NewApi")
	private void loadPeoples() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(URLs.USERLIST ,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						try {
							PeopleList cList = PeopleList.parseJson(content);
							if (cList.code == 200) {
								peoples = cList.getInfo();
								application.saveObject((Serializable) peoples,"peoples");
								people_names = new String[peoples.size()];
								int i = 0;
								for (People cn : peoples) {
									people_names[i] = cn.name;
									i++;
								}
								initEdittext();
							} else {
								showToast(cList.msg);
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

	private void initUi() {
		
		
		//添加
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) AddZhiliangReportActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				
				submitShenpi();
			}
		});
//		shenpi_close.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
		
		
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) AddZhiliangReportActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		addxiapai_full.setClickable(true);
		addxiapai_full.setOnClickListener(keyboard_hide);
	}
	
	/**
	 * 提交申请
	 * @author wlj
	 * @date 2015-6-16下午7:21:20
	 */
	private void submitShenpi(){
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		
		AbRequestParams params = new AbRequestParams();
		params.put("androidApplyVerifyInfo.title", task_name.getText().toString());
		params.put("androidApplyVerifyInfo.content", task_tell.getText().toString());
		params.put("androidApplyVerifyInfo.apply_user_id", loginKey);
		Log.d(TAG, String.format("%s?", URLs.ADDSHENPI,
				params));
		mAbHttpUtil.post(URLs.ADDSHENPI ,params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							ShenpiInfo gList = ShenpiInfo
									.parseJson(content);
							if (gList.code == 200) {
								showToast("申请提交成功！");
								finish();
							} else {
								UIHelper.ToastMessage(context, gList.msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							UIHelper.ToastMessage(context, "数据解析失败");
						}
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						UIHelper.ToastMessage(context, "网络连接失败！");
					}

					@Override
					public void onStart() {
						showProgressDialog(null);
					}

					// 完成后调用
					@Override
					public void onFinish() {
						finish();
					};
				});
	}

}