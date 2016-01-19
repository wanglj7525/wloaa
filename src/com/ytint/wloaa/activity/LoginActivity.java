package com.ytint.wloaa.activity;

import java.util.Date;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
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
	@AbIocView(id = R.id.network)
	private TextView network;
	@AbIocView(id = R.id.layoutpic2)
	private LinearLayout full_screen_layout;
	private static MyApplication application;
	Context context = null;
	// 获取Http工具类
	final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
	String host;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application= (MyApplication) this.getApplication();
		host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
//		// 添加百度地图SDK
//		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.layout_login);
		context=LoginActivity.this;
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		
		application.setProperty("is_login","0");
		
		String username = application.getProperty(Constants.USER_NAME);
		if (null != username && username.length() > 0) {
			inputUsername.setText(username);
		}

		
		network.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final CustomDialog.Builder builder = new LoginActivity.CustomDialog.Builder(
						LoginActivity.this);
				builder.setTitle("网络配置")
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
//										InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//										imm.hideSoftInputFromWindow(v.getWindowToken(),
//												0);
										dialog.dismiss();
									}
								});
				builder.setPositiveButton("确定",
						getResources().getColor(R.color.global_blue),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								
								String host=builder.networks_ipl.getText().toString().trim();
								String port=builder.networks_portl.getText().toString().trim();
								String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
								String regexyu="^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
								if (!Pattern.matches(regex, host)) {
									if (!Pattern.matches(regexyu, host)) {
										showToast("地址不合法！");
										return;
									}
								}
								if (port.length()==0) {
									showToast("端口不能为空！");
									return;
								}
								application.setProperty("HOST",host);
								application.setProperty("PORT",port);
								showToast("设置成功！");
//								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//								imm.hideSoftInputFromWindow(v.getWindowToken(),
//										0);
								dialog.dismiss();

							}

						});

				builder.create().show();
			}
		});
		
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
		host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
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
								String phone=info.getString("phone");
								application.setProperty("loginKey",id);
								application.setProperty(Constants.USER_NAME,
										username);
								application.setProperty("userName",
										name);
								application.setProperty("userType",
										user_type);
								application.setProperty("department_id",
										department_id);
								application.setProperty("phone",
										phone);
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
	
	
	public static class CustomDialog extends Dialog {

		public CustomDialog(Context context, int theme) {
			super(context, theme);
		}

		public CustomDialog(Context context) {
			super(context);
		}

		/**
		 * Helper class for creating a custom dialog
		 */
		@SuppressLint("NewApi")
		static public class Builder {

			private Context context;
			private String title;
			private String editMessage;
			private String message;
			private String positiveButtonText;
			private int positiveButtonColor;
			private String negativeButtonText;
			private View contentView;
			public AutoCompleteTextView editText;

			public EditText networks_ipl;
			public EditText networks_portl;
			private DialogInterface.OnClickListener positiveButtonClickListener,
					negativeButtonClickListener;

			public Builder(Context context) {
				this.context = context;
			}

			/**
			 * Set the Dialog message from String
			 * 
			 * @param title
			 * @return
			 */
			public Builder setMessage(String message) {
				this.message = message;
				return this;
			}

			/**
			 * Set the Dialog message from resource
			 * 
			 * @param title
			 * @return
			 */
			public Builder setMessage(int message) {
				this.message = (String) context.getText(message);
				return this;
			}

			/**
			 * Set the Dialog title from resource
			 * 
			 * @param title
			 * @return
			 */
			public Builder setTitle(int title) {
				this.title = (String) context.getText(title);
				return this;
			}

			/**
			 * Set the Dialog title from String
			 * 
			 * @param title
			 * @return
			 */
			public Builder setTitle(String title) {
				this.title = title;
				return this;
			}

			/**
			 * Set the Dialog title from resource
			 * 
			 * @param title
			 * @return
			 */
			public Builder setEditMessage(int editMessage) {
				this.editMessage = (String) context.getText(editMessage);
				return this;
			}

			/**
			 * Set the Dialog title from String
			 * 
			 * @param title
			 * @return
			 */
			public Builder setEditMessage(String editMessage) {
				this.editMessage = editMessage;
				return this;
			}

			/**
			 * Set a custom content view for the Dialog. If a message is set,
			 * the contentView is not added to the Dialog...
			 * 
			 * @param v
			 * @return
			 */
			public Builder setContentView(View v) {
				this.contentView = v;
				return this;
			}

			/**
			 * Set the positive button resource and it's listener
			 * 
			 * @param positiveButtonText
			 * @param listener
			 * @return
			 */
			public Builder setPositiveButton(int positiveButtonText,
					DialogInterface.OnClickListener listener) {
				this.positiveButtonText = (String) context
						.getText(positiveButtonText);
				this.positiveButtonClickListener = listener;
				return this;
			}

			/**
			 * Set the positive button text and it's listener
			 * 
			 * @param positiveButtonText
			 * @param listener
			 * @return
			 */
			public Builder setPositiveButton(String positiveButtonText,
					int color, DialogInterface.OnClickListener listener) {
				this.positiveButtonText = positiveButtonText;
				// this.positiveButtonColor=R.color.black;
				this.positiveButtonClickListener = listener;
				return this;
			}

			/**
			 * Set the negative button resource and it's listener
			 * 
			 * @param negativeButtonText
			 * @param listener
			 * @return
			 */
			public Builder setNegativeButton(int negativeButtonText,
					DialogInterface.OnClickListener listener) {
				this.negativeButtonText = (String) context
						.getText(negativeButtonText);
				this.negativeButtonClickListener = listener;
				return this;
			}

			/**
			 * Set the negative button text and it's listener
			 * 
			 * @param negativeButtonText
			 * @param listener
			 * @return
			 */
			public Builder setNegativeButton(String negativeButtonText,
					DialogInterface.OnClickListener listener) {
				this.negativeButtonText = negativeButtonText;
				this.negativeButtonClickListener = listener;
				return this;
			}

			/**
			 * Create the custom dialog
			 */
			public CustomDialog create() {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// instantiate the dialog with the custom Theme
				final CustomDialog dialog = new CustomDialog(context,
						R.style.Dialog);
				View layout = inflater.inflate(R.layout.networkdialog, null);
				dialog.addContentView(layout, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				// set the dialog title
				((TextView) layout.findViewById(R.id.title)).setText(title);
				// set the confirm button
				if (positiveButtonText != null) {
					// ((Button)
					// layout.findViewById(R.id.positiveButton)).setBackgroundColor(positiveButtonColor);
					((Button) layout.findViewById(R.id.positiveButton))
							.setText(positiveButtonText);
					if (positiveButtonClickListener != null) {
						((Button) layout.findViewById(R.id.positiveButton))
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										positiveButtonClickListener
												.onClick(
														dialog,
														DialogInterface.BUTTON_POSITIVE);
									}
								});
					}
				} else {
					// if no confirm button just set the visibility to GONE
					layout.findViewById(R.id.positiveButton).setVisibility(
							View.GONE);
				}
				// set the cancel button
				if (negativeButtonText != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setText(negativeButtonText);
					if (negativeButtonClickListener != null) {
						((Button) layout.findViewById(R.id.negativeButton))
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										negativeButtonClickListener
												.onClick(
														dialog,
														DialogInterface.BUTTON_NEGATIVE);
									}
								});
					}
				} else {
					// if no confirm button just set the visibility to GONE
					layout.findViewById(R.id.negativeButton).setVisibility(
							View.GONE);
				}
				// set the content message
				networks_portl=(EditText) layout.findViewById(R.id.networks_portl);
				networks_ipl=(EditText) layout.findViewById(R.id.networks_ipl);
				networks_ipl.setText(application.getProperty("HOST"));
				networks_portl.setText(application.getProperty("PORT"));
				dialog.setContentView(layout);
				return dialog;
			}
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