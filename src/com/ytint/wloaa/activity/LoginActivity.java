package com.ytint.wloaa.activity;

import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.mapapi.SDKInitializer;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.URLs;

/**
 * @author wlj
 * @date 2014-4-17上午10:40:17
 */
public class LoginActivity extends AbActivity {
	private static final String TAG = "LoginActivity";
	@AbIocView(id = R.id.username_edit)
	private EditText inputUsername;
	@AbIocView(id = R.id.password_edit)
	private EditText inputPassword;
	@AbIocView(id = R.id.layoutpic2)
	private LinearLayout full_screen_layout;
	private MyApplication application;
	Context context = null;
	// 获取Http工具类
	final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
	String host;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application= (MyApplication) this.getApplication();
		host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
		// 添加百度地图SDK
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.layout_login);
		context=LoginActivity.this;
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		
		application.setProperty("is_login","0");
		
		String username = application.getProperty(Constants.USER_NAME);
		if (null != username && username.length() > 0) {
			inputUsername.setText(username);
		}

		Button button = (Button) findViewById(R.id.login_button);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				login();

			}

		});

		inputPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// 隐藏软键盘
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(inputPassword.getWindowToken(),
							0);
					login();
					return true;
				}
				return false;
			}
		});

		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		full_screen_layout.setClickable(true);
		full_screen_layout.setOnClickListener(keyboard_hide);

	}

	private void login() {
		final String username = inputUsername.getText().toString();
		final String password = inputPassword.getText().toString();
		if (username == null || password == null || username.equals("")
				|| password.equals("")) {
			Toast.makeText(LoginActivity.this, "用户名、密码不能为空！",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String jpush_registration_id = "";
		// 从缓存里读jpushid
		jpush_registration_id = application.getProperty("registrationID");
		// 如果缓存中不存在，则设为空值
		if (null==jpush_registration_id) {
			initialLocalConfig();
			 JPushInterface.init(getApplicationContext());
			 jpush_registration_id=JPushInterface.getRegistrationID(context);
			 application.setProperty("registrationID", jpush_registration_id);
		}
		// 绑定参数
		AbRequestParams params = new AbRequestParams();
		params.put("user_name", username);
		params.put("password", password);
		params.put("jpush_registration_id", jpush_registration_id);
//		params.put("user_imei_id", application.getProperty("imei"));

		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(LoginActivity.this, "请检查网络连接");
			return;
		}
		String a = host+URLs.LOGINURL;
		System.out.println(a);
		mAbHttpUtil.post(host+URLs.LOGINURL, params,
				new AbStringHttpResponseListener() {

					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						System.out.println(content);
						Log.d(TAG, "onSuccess");
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(content);
							int code = Integer.parseInt(jsonObject.getString(
									"code").toString());
							if (code == Constants.SUCCESS) {
								
								application.setProperty("is_login","1");
								
								JSONObject info=jsonObject.getJSONObject("info");
								String id=info.getString("id");
								String name=info.getString("name");
								String user_type=info.getString("user_type");
								String department_id=info.getString("department_id");
								application.setProperty("loginKey",id);
								application.setProperty(Constants.USER_NAME,
										username);
								application.setProperty("userName",
										name);
								application.setProperty("userType",
										user_type);
								application.setProperty("departmentId",
										department_id);
								// 跳转到首页
								
								 Intent intent = new Intent(LoginActivity.this,
								  MainActivity.class);
								 LoginActivity.this.startActivity(intent);
								 LoginActivity.this.finish();

							} else {
								Toast.makeText(LoginActivity.this, "用户名或密码错误！",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					};

					// 开始执行前
					@Override
					public void onStart() {
						Log.d(TAG, "onStart");
						// 显示进度框
						showProgressDialog();
					}

					// 失败，调用
					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showToast("连接超时！");
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						Log.d(TAG, "onFinish");
						// 移除进度框
						removeProgressDialog();
					};

				});
	}
	/**
	 * 初始化本地数据
	 */
	private void initialLocalConfig() {
		// 初始化imei信息
		String user_imei_id = application.getProperty("imei");
		if (user_imei_id == null || user_imei_id.equals("")) {
			// 第一次登陆逻辑处理，能获取imei就获取不能获取到换成时间戳
			TelephonyManager mTm = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			String _user_imei_id = mTm.getDeviceId();
			if (_user_imei_id == null || _user_imei_id.length() <= 2) {
				// 如果获取不到imei则获取当前时间戳
				Constants.USER_IMEI_ID = String.valueOf(new Date().getTime());
			} else {
				Constants.USER_IMEI_ID = mTm.getDeviceId();
			}
			// imei写到缓存中，以后直接用缓存的
			application.setProperty("imei", Constants.USER_IMEI_ID);
		} else {
			Constants.USER_IMEI_ID = user_imei_id;
		}

	}
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

}