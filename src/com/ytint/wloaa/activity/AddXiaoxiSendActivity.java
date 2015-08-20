package com.ytint.wloaa.activity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.activity.ShenpiDetailActivity.MAdapter;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Company;
import com.ytint.wloaa.bean.CompanyList;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.PeopleList;
import com.ytint.wloaa.bean.QunfaInfo;
import com.ytint.wloaa.bean.URLs;

public class AddXiaoxiSendActivity extends AbActivity {
	String TAG = "AddXiaoxiSendActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	private String departmentId;
	private int push_user_id;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	private long people = 0;
	private List<People> peoples;
	private int from;
	private int shenpi_id;
	@AbIocView(id = R.id.select_people_xiaoxi)
	Spinner peopleSpinner;
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
	private String peopleId;
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
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		AbTitleBar mAbTitleBar = this.getTitleBar();
		if (from == 1) {
			mAbTitleBar.setTitleText("发送消息");
		} else if (from == 2){
			mAbTitleBar.setTitleText("发送公告");
		}else{
			mAbTitleBar.setTitleText("回复消息");
			shenpi_id = Integer.parseInt(intent.getExtras().get("shenpi_id").toString());
			push_user_id = Integer.parseInt(intent.getExtras().get("push_user_id").toString());
		}
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

		setAbContentView(R.layout.layout_addxiaoxi);
		context = AddXiaoxiSendActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		departmentId = application.getProperty("departmentId");
		initUi();
		// 加载联系人下拉框
		
		if (from==1) {
			loadPeoples();
		}else{
			showSelectPeople.setVisibility(View.GONE);
		}
		

	}

	private void initUi() {
		// 添加
		commitXiaoxi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) AddXiaoxiSendActivity.this
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
				InputMethodManager imm = (InputMethodManager) AddXiaoxiSendActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		addxiaoxi_full.setClickable(true);
		addxiaoxi_full.setOnClickListener(keyboard_hide);
	}
	private void initSpinner() {
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(AddXiaoxiSendActivity.this,
				R.layout.spinner_item, people_names);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		// 将adapter 添加到spinner中
		peopleSpinner.setAdapter(adapter);
		// 设置默认选中
		// channelSpinner.setSelection(0);
		// 设置默认值
		// channelSpinner.setVisibility(View.VISIBLE);
		peopleSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						people = peoples.get(arg2).id;
						peopleId=people+"";
						TextView tv = (TextView) view;
						tv.setTextColor(getResources().getColor(R.color.black)); // 设置颜色
						tv.setGravity(android.view.Gravity.CENTER); // 设置居中

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						arg0.setVisibility(View.VISIBLE);
					}

				});

	}

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
		if (xiaoxi_title.getText().toString().trim()==""||xiaoxi_info.getText().toString().trim()=="") {
			UIHelper.ToastMessage(context, "请输入内容");
			return;
		}
		AbRequestParams params = new AbRequestParams();
		params.put("androidNoticeInfo.title", xiaoxi_title.getText().toString());
		params.put("androidNoticeInfo.content", xiaoxi_info.getText().toString());
		params.put("androidNoticeInfo.receive_user_type", "3");//接收人类型：1：全部成员；2：本科室成员；3：指定人员
		if (from==2) {
			params.put("androidNoticeInfo.notice_type", "0");
			params.put("receive_user_ids","1");
		}else{
			if (from==3) {
				params.put("receive_user_ids",push_user_id+"");
			}else{
				params.put("receive_user_ids",peopleId);
			}
			params.put("androidNoticeInfo.notice_type", "1");
		}
		params.put("androidNoticeInfo.push_user_id", loginKey);
		params.put("androidNoticeInfo.department_id", departmentId);
		if (from==3) {
			params.put("androidNoticeInfo.reply_notice_id", shenpi_id+"");
		}else{
			params.put("androidNoticeInfo.reply_notice_id", "0");
		}
		Log.e(TAG, String.format("%s?%s", URLs.ADDMSG,
				params.toString()));
		mAbHttpUtil.post(URLs.ADDMSG ,params,
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

	@SuppressLint("NewApi")
	private void loadPeoples() {

		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(URLs.USERLIST+"?user_id=0" ,
			 new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					PeopleList cList = PeopleList.parseJson(content);
					if (cList.code == 200) {
						peoples = cList.getInfo();
						for (int i = 0; i < peoples.size(); i++) {
							if (loginKey.equals(peoples.get(i).id+"")) {
								peoples.remove(i);
							}
						}
						peopleId=peoples.get(0).id+"";
						application.saveObject((Serializable) peoples,
								"peoples");
						people_names = new String[peoples.size()];
						int i = 0;
						for (People cn : peoples) {
							people_names[i] = cn.name;
							i++;
						}
						initSpinner();
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


}
