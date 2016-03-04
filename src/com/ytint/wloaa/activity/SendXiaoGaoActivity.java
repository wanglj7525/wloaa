package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.jpush.android.api.JPushInterface;

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
import com.ytint.wloaa.bean.QunfaInfo;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.widget.AutolinefeedView;
import com.ytint.wloaa.widget.TitleBar;

public class SendXiaoGaoActivity extends AbActivity {
	String TAG = "AddXiaoxiSendActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	private String departmentId;
	private int push_user_id;
	private String push_user_name;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	private long people = 0;
	private List<People> peoples;
	private int from;
	private int message_id;
	private String message;
	// @AbIocView(id = R.id.select_people_xiaoxi)
	// Spinner peopleSpinner;
	@AbIocView(id = R.id.commitXiaoxi)
	Button commitXiaoxi;
	@AbIocView(id = R.id.xiaoxi_info)
	EditText xiaoxi_info;
	@AbIocView(id = R.id.xiaoxi_title)
	EditText xiaoxi_title;
	@AbIocView(id = R.id.addxiaoxi_full)
	LinearLayout addxiaoxi_full;
	@AbIocView(id = R.id.xiaoxicancel)
	Button xiaoxicancel;
	@AbIocView(id = R.id.showSelectPeople)
	LinearLayout showSelectPeople;
	@AbIocView(id = R.id.showReplayPeople)
	LinearLayout showReplayPeople;
	@AbIocView(id = R.id.showrange)
	LinearLayout showrange;
	@AbIocView(id = R.id.add_people)
	Button add_people;
	 @AbIocView(id=R.id.edit_people)
	 EditText edit_people;
	@AbIocView(id = R.id.autolinefeedView1)
	AutolinefeedView autolinefeedView1;
	private String peopleId = "";
	private int receive_type = 0;
	private int receive_user_type = 1;
	private String userType;
	String host;
	private ArrayList<String> userlist = new ArrayList<String>();

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
		setAbContentView(R.layout.layout_addxiaoxi);
		context = SendXiaoGaoActivity.this;
		loginKey = application.getProperty("loginKey");
		departmentId = application.getProperty("departmentId");

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		final TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setLeftImageResource(R.drawable.back_green);
		titleBar.setLeftText("返回");
		titleBar.setLeftTextColor(Color.WHITE);
		titleBar.setLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if (from == 1) {
			titleBar.setTitle("发送消息");
		} else if (from == 2) {
			titleBar.setTitle("发送公告");
		} else {
			titleBar.setTitle("回复消息");
			message_id = Integer.parseInt(intent.getExtras().get("message_id")
					.toString());
			message = intent.getExtras().get("message").toString();
			push_user_name = intent.getExtras().get("push_user_name").toString();
			push_user_id = Integer.parseInt(intent.getExtras()
					.get("push_user_id").toString());
		}
		titleBar.setTitleColor(Color.WHITE);
		titleBar.setDividerColor(Color.GRAY);

		initUi();
		// 加载联系人下拉框

		if (from == 1) {
			//消息
			showrange.setVisibility(View.GONE);
		} else if (from == 2) {
			//公告
			showSelectPeople.setVisibility(View.GONE);
		} else {
			//回复
			showrange.setVisibility(View.GONE);
			showSelectPeople.setVisibility(View.GONE);
			showReplayPeople.setVisibility(View.VISIBLE);
			edit_people.setText(push_user_name+"");
		}

	}

	private void initUi() {
		if (from == 3) {
			xiaoxi_title.setText("回复：" + message);
		}
		userType = application.getProperty("userType");
		if (userType.equals("0")||userType.equals("1")||userType.equals("2")) {
			//管理员，局长。副局长
			RadioButton radio=(RadioButton)this.findViewById(R.id.radio0);
			radio.setVisibility(View.GONE);
			RadioButton radio1=(RadioButton)this.findViewById(R.id.radio1);
			radio1.setChecked(true);
		}
		add_people.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 选择联系人
				Intent intent = new Intent(SendXiaoGaoActivity.this,
						SelectPeopleActivity.class);
				intent.putExtra("userlist", userlist);
				startActivityForResult(intent, 20);
			}
		});
		// 添加
		commitXiaoxi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) SendXiaoGaoActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				submitXiaoxi();
			}
		});
		xiaoxicancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) SendXiaoGaoActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		addxiaoxi_full.setClickable(true);
		addxiaoxi_full.setOnClickListener(keyboard_hide);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 20:
				userlist = new ArrayList<String>();
				autolinefeedView1.removeAllViews();
				ArrayList<Map<String, Object>> result = (ArrayList<Map<String, Object>>) data
						.getExtras().get("result");// 得到新Activity 关闭后返回的数据
				for (int i = 0; i < result.size(); i++) {
					if(!userlist.contains(result.get(i).get("peopleid").toString())){
						userlist.add(result.get(i).get("peopleid").toString());
						final Button bt = new Button(context);
						bt.setText(result.get(i).get("name").toString());
						bt.setTag(result.get(i).get("peopleid").toString());
						bt.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								autolinefeedView1.removeView(bt);
								userlist.remove(bt.getTag());
							}
						});
						autolinefeedView1.addView(bt);
					}
				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// private void initSpinner() {
	// // 将可选内容与ArrayAdapter连接起来
	// adapter = new ArrayAdapter<String>(SendXiaoGaoActivity.this,
	// R.layout.spinner_item, people_names);
	// // 设置下拉列表的风格
	// adapter.setDropDownViewResource(R.layout.drop_down_item);
	// // 将adapter 添加到spinner中
	// peopleSpinner.setAdapter(adapter);
	// // 设置默认选中
	// // channelSpinner.setSelection(0);
	// // 设置默认值
	// // channelSpinner.setVisibility(View.VISIBLE);
	// peopleSpinner
	// .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
	// @Override
	// public void onItemSelected(AdapterView<?> arg0, View view,
	// int arg2, long arg3) {
	// people = peoples.get(arg2).id;
	// peopleId=people+"";
	// TextView tv = (TextView) view;
	// tv.setTextColor(getResources().getColor(R.color.black)); // 设置颜色
	// tv.setGravity(android.view.Gravity.CENTER); // 设置居中
	//
	// }
	//
	// @Override
	// public void onNothingSelected(AdapterView<?> arg0) {
	// arg0.setVisibility(View.VISIBLE);
	// }
	//
	// });
	//
	// }

	/**
	 * 下发任务
	 * 
	 * @author wlj
	 * @date 2015-6-16下午7:21:20
	 */
	private void submitXiaoxi() {
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		if (xiaoxi_title.getText().toString().trim().length() == 0
				|| xiaoxi_info.getText().toString().trim().length() == 0) {
			UIHelper.ToastMessage(context, "请输入内容");
			return;
		}
		AbRequestParams params = new AbRequestParams();

		params.put("androidNoticeInfo.title", xiaoxi_title.getText().toString());
		params.put("androidNoticeInfo.content", xiaoxi_info.getText()
				.toString());
		// params.put("androidNoticeInfo.receive_user_type", "3");//
		// 接收人类型：1：全部成员；2：本科室成员；3：指定人员
		if (from == 2) {
			// 发送公告
			params.put("androidNoticeInfo.notice_type", "0");
			params.put("receive_user_ids", "1");
			RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup1);
			RadioButton radioButton = (RadioButton) findViewById(group
					.getCheckedRadioButtonId());
			if (radioButton.getText().equals("本科室")) {
				receive_type = 0;
				receive_user_type=2;
			} else {
				receive_type = 1;
				receive_user_type=1;
			}
		} else {
			if (from == 3) {
				// 回复消息
				params.put("receive_user_ids", push_user_id + "");
			} else {
				for (int i = 0; i < userlist.size(); i++) {
					peopleId += userlist.get(i) + ",";
				}
				if (peopleId.length() > 0) {
					peopleId = peopleId.substring(0, peopleId.length() - 1);
				} else {
					UIHelper.ToastMessage(context, "请选择联系人");
					return;
				}
				// 发送消息
				params.put("receive_user_ids", peopleId);
			}
			receive_type = 2;
			receive_user_type=3;
			params.put("androidNoticeInfo.notice_type", "1");
		}
		params.put("androidNoticeInfo.receive_user_type", receive_user_type + "");// 接收人类型：1：全部成员；2：本科室成员；3：指定人员
		params.put("receive_type", receive_type + "");// receive_type
																		// 接受用户类型，主要针对科长发布公告，0：本科室；1：全部；2：指定人（具体接收人在receive_user_ids中指明）
		params.put("androidNoticeInfo.push_user_id", loginKey);
		params.put("androidNoticeInfo.department_id", departmentId);
		if (from == 3) {
			params.put("androidNoticeInfo.reply_notice_id", message_id + "");
		} else {
			params.put("androidNoticeInfo.reply_notice_id", "0");
		}
		Log.e(TAG,
				String.format("%s?%s", host + URLs.ADDMSG, params.toString()));
		mAbHttpUtil.post(host + URLs.ADDMSG, params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.e(TAG, content);
						try {
							QunfaInfo gList = QunfaInfo.parseJson(content);
							if (gList.code == 200) {
								showToast("发送成功！");
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

	// @SuppressLint("NewApi")
	// private void loadPeoples() {
	//
	// final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
	// if (!application.isNetworkConnected()) {
	// showToast("请检查网络连接");
	// return;
	// }
	// String
	// host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
	// mAbHttpUtil.get(host+URLs.USERLIST+"?user_id=0" ,
	// new AbStringHttpResponseListener() {
	// // 获取数据成功会调用这里
	// @Override
	// public void onSuccess(int statusCode, String content) {
	// try {
	// PeopleList cList = PeopleList.parseJson(content);
	// if (cList.code == 200) {
	// peoples = cList.getInfo();
	// for (int i = 0; i < peoples.size(); i++) {
	// if (loginKey.equals(peoples.get(i).id+"")) {
	// peoples.remove(i);
	// }
	// }
	// peopleId=peoples.get(0).id+"";
	// application.saveObject((Serializable) peoples,
	// "peoples");
	// people_names = new String[peoples.size()];
	// int i = 0;
	// for (People cn : peoples) {
	// people_names[i] = cn.name;
	// i++;
	// }
	// // initSpinner();
	// } else {
	// showToast(cList.msg);
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
	// }

}
