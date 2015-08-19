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
import android.widget.RelativeLayout;
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
import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.URLs;

/**
 * @author wlj
 * @date 2014-4-17上午10:40:17
 */
public class PassWordUpdateActivity extends AbActivity {
	private static final String TAG = "PassWordUpdateActivity";
	@AbIocView(id = R.id.new_pass1)
	private EditText new_pass1;
	@AbIocView(id = R.id.new_pass2)
	private EditText new_pass2;
	@AbIocView(id = R.id.old_pass)
	private EditText old_pass;
	@AbIocView(id = R.id.layoutpas)
	private LinearLayout full_screen_layout;
	private MyApplication application;
	Context context = null;
	private String loginKey;
	// 获取Http工具类
	final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (MyApplication) this.getApplication();
		context=PassWordUpdateActivity.this;
		setContentView(R.layout.layout_password);
		loginKey=application.getProperty("loginKey");
		RelativeLayout closeActi = (RelativeLayout) findViewById(R.id.closePass);
		closeActi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button button = (Button) findViewById(R.id.update_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				update();

			}

		});

		new_pass2.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// 隐藏软键盘
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(new_pass1.getWindowToken(),
							0);
					update();
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

	private void update() {
		final String oldpass = old_pass.getText().toString();
		final String password = new_pass1.getText().toString();
		if (old_pass == null || password == null || old_pass.equals("")
				|| password.equals("")) {
			Toast.makeText(PassWordUpdateActivity.this, "密码不能为空！",
					Toast.LENGTH_SHORT).show();
			return;
		}else if (!password.equals(new_pass2.getText().toString())) {
			Toast.makeText(PassWordUpdateActivity.this, "密码不一致！",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 绑定参数
		AbRequestParams params = new AbRequestParams();
		params.put("user_id", loginKey);
		params.put("password", oldpass);
		params.put("new_password", password);

		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(PassWordUpdateActivity.this, "请检查网络连接");
			return;
		}
		
		mAbHttpUtil.post(URLs.PASSWORD, params,
				new AbStringHttpResponseListener() {

					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.e(TAG, content);
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(content);
							int code = Integer.parseInt(jsonObject.getString(
									"code").toString());
							if (code == Constants.SUCCESS) {
								
								application.setProperty("is_login","0");
								// 跳转到登录
								 Intent intent = new Intent(PassWordUpdateActivity.this,
								  LoginActivity.class);
								 PassWordUpdateActivity.this.startActivity(intent);
								 PassWordUpdateActivity.this.finish();

							} else {
								Toast.makeText(PassWordUpdateActivity.this, jsonObject.getString("msg"),
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
						Log.e(TAG, content);
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