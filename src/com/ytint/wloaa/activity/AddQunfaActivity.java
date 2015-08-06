package com.ytint.wloaa.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.QunfaInfo;
import com.ytint.wloaa.bean.URLs;

public class AddQunfaActivity extends BaseActivity {
	String TAG = "AddQunfaActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	@AbIocView(id = R.id.addxiaoxi_full)
	LinearLayout addxiaoxi_full;
	@AbIocView(id = R.id.commitXiaoxi)
	Button add;
	@AbIocView(id = R.id.xiaoxi_info)
	EditText xiaoxi_info;
	@AbIocView(id = R.id.xiaoxi_title)
	EditText xiaoxi_title;
	@AbIocView(id = R.id.xiaoxi_close)
	TextView xiaoxi_close;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		setAbContentView(R.layout.layout_addqunfai);
		context = AddQunfaActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		initUi();
		
	}

	private void initUi() {
		
		//添加
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) AddQunfaActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				
				submitXiaoxi();
			}
		});
		xiaoxi_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) AddQunfaActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		addxiaoxi_full.setClickable(true);
		addxiaoxi_full.setOnClickListener(keyboard_hide);
	}
	
	/**
	 * 群发消息
	 * @author wlj
	 * @date 2015-6-16下午7:21:20
	 */
	private void submitXiaoxi(){
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		
		AbRequestParams params = new AbRequestParams();
		params.put("androidNoticeInfo.title", xiaoxi_title.getText().toString());
		params.put("androidNoticeInfo.content", xiaoxi_info.getText().toString());
		params.put("androidNoticeInfo.push_user_id", loginKey);
		params.put("androidNoticeInfo.notice_type", "0");
		Log.d(TAG, String.format("%s?", URLs.ADDMSG,
				params));
		mAbHttpUtil.post(URLs.ADDMSG ,params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							QunfaInfo gList = QunfaInfo
									.parseJson(content);
							if (gList.code == 200) {
								showToast("消息已经发送！");
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
//	@SuppressLint("NewApi")
//	private void loadPeoples() {
//		try {
//			String content="{\"result\":\"success\",\"msg\":\"操作成功\",\"code\":\"200\",\"info\":[" +
//					"{\"pid\":\"1\",\"pcall\":\"18660019999\",\"pn\":\"赵钱孙\",\"sid\":\"0\"}," +
//					"{\"pid\":\"2\",\"pcall\":\"18660019998\",\"pn\":\"周吴郑\",\"sid\":\"1\"}," +
//					"{\"pid\":\"3\",\"pcall\":\"18660019997\",\"pn\":\"甲乙丙\",\"sid\":\"2\"}]}";
//			
//			PeopleList cList = PeopleList.parseJson(content);
//			if (cList.code == 200) {
//				peoples = cList.getInfo();
//				application.saveObject((Serializable) peoples,"peoples");
//				people_names = new String[peoples.size()];
//				int i = 0;
//				for (People cn : peoples) {
//					people_names[i] = cn.pn;
//					i++;
//				}
//			} else {
//				showToast(cList.msg);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			showToast("数据解析失败");
//		}
//		
////		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
////		String loginKey = application.getProperty("loginKey");
////		if (!application.isNetworkConnected()) {
////			showToast("请检查网络连接");
////			return;
////		}
////		mAbHttpUtil.get(URLs.CHANNELS + "?access_token=" + loginKey,
////				new AbStringHttpResponseListener() {
////					// 获取数据成功会调用这里
////					@Override
////					public void onSuccess(int statusCode, String content) {
////						try {
////							content="{\"result\":\"success\",\"msg\":\"操作成功\",\"code\":\"200\",\"info\":[" +
////									"{\"pid\":\"1\",\"pcall\":\"18660019999\",\"pn\":\"赵钱孙\",\"sid\":\"0\"}," +
////									"{\"pid\":\"2\",\"pcall\":\"18660019998\",\"pn\":\"周吴郑\",\"sid\":\"1\"}," +
////									"{\"pid\":\"3\",\"pcall\":\"18660019997\",\"pn\":\"甲乙丙\",\"sid\":\"2\"}]}";
////							
////							PeopleList cList = PeopleList.parseJson(content);
////							if (cList.code == 200) {
////								peoples = cList.getInfo();
////								application.saveObject((Serializable) peoples,"peoples");
////								people_names = new String[peoples.size()];
////								int i = 0;
////								for (People cn : peoples) {
////									people_names[i] = cn.pn;
////									i++;
////								}
////								initSpinner();
////							} else {
////								showToast(cList.msg);
////							}
////						} catch (Exception e) {
////							e.printStackTrace();
////							showToast("数据解析失败");
////						}
////					};
////
////					// 开始执行前
////					@Override
////					public void onStart() {
////						// 显示进度框
////						showProgressDialog();
////					}
////
////					@Override
////					public void onFailure(int statusCode, String content,
////							Throwable error) {
////						showToast("网络连接失败！");
////					}
////
////					// 完成后调用，失败，成功
////					@Override
////					public void onFinish() {
////						// 移除进度框
////						removeProgressDialog();
////					};
////
////				});
//	}
//
}
