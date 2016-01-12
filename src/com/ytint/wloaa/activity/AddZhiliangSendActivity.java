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
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.FileHelper;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.PeopleList;
import com.ytint.wloaa.bean.Project;
import com.ytint.wloaa.bean.ProjectList;
import com.ytint.wloaa.bean.Qunfa;
import com.ytint.wloaa.bean.QunfaInfo;
import com.ytint.wloaa.bean.URLs;

/**
 * 工程任务
 * @author wlj
 * @date 2016-1-12下午2:41:39
 */
public class AddZhiliangSendActivity extends AbActivity {
	String TAG = "AddZhiliangSendActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	String[] project_names = new String[0];
	private long people = 0;
	private long project = 0;
	private List<People> peoples;
	private List<Project> projects;
	private int from;
	boolean isLongClick = false;
	@AbIocView(id = R.id.select_people)
	Spinner peopleSpinner;
	@AbIocView(id = R.id.select_project)
	Spinner projectSpinner;
//	@AbIocView(id = R.id.select_company_send)
//	Spinner companySpinner;
	@AbIocView(id = R.id.addTaskFile)
	Button addTaskFile;
	@AbIocView(id = R.id.addTaskNoFile)
	Button addTaskNoFile;
	@AbIocView(id = R.id.sendcancel)
	Button sendcancel;
	// @AbIocView(id = R.id.task_info)
	// EditText task_info;
	@AbIocView(id = R.id.task_tell_send)
	EditText task_tell;
	@AbIocView(id = R.id.task_remark)
	EditText task_remark;
	@AbIocView(id = R.id.task_name_send)
	EditText task_name_send;
	// @AbIocView(id = R.id.search_close)
	// TextView search_close;
	@AbIocView(id = R.id.addxiapai_full)
	LinearLayout addxiapai_full;
	@AbIocView(id = R.id.addVoice)
	Button addVoice;
	@AbIocView(id = R.id.addvoicegridview)
	GridView addvoicegridview;
	@AbIocView(id = R.id.horizontalScrollView_addvoice)
	HorizontalScrollView horizontalScrollView_addvoice;
	/** 语音列表适配器 */
	private MyGridAdapter mAdapter;
	/** 语音列表 */
	private ArrayList<String> mVoicesList;
	/** 语音名称列表 */
	private List<String> mVoicesListname;
	/** 录音存储路径 */
	private static final String PATH = "/sdcard/MyVoiceForder/Record/";
	/** 用于语音播放 */
	private MediaPlayer mPlayer = null;
	/** 用于完成录音 */
	private MediaRecorder mRecorder = null;
	/** 语音文件保存路径 */
	private String mFileName = null;
	/** 语音文件显示名称 */
	private String mFileNameShow = null;
	/**
	 * 列宽
	 */
	private int cWidth = 500;
	/**
	 * 水平间距
	 */
	private int hSpacing = 10;
	private String peopleId;
	private String projectId="0";
	private String commitId="0";
	public static ArrayList<String> media=new ArrayList<String>();
	
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
		application= (MyApplication) this.getApplication();
		host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("下派任务");
//		if (from == 1) {
//			mAbTitleBar.setTitleText("质量检查-下派质检任务");
//		} else if (from == 2) {
//			mAbTitleBar.setTitleText("安全检查-下派安检任务");
//		} else {
//			mAbTitleBar.setTitleText("执法管理-下派执法任务");
//		}
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

		setAbContentView(R.layout.layout_addzhiliangsend);
		context = AddZhiliangSendActivity.this;
		loginKey = application.getProperty("loginKey");
		initData();
		initUi();
		// 加载联系人下拉框
		loadPeoples();
		loadProject();

	}

	/** 初始化数据 */
	private void initData() {
		mVoicesList = new ArrayList<String>();
		mVoicesListname = new ArrayList<String>();
		mPlayer = new MediaPlayer();
	}

	private void initGridView() {
		MyGridAdapter mAdapter = new MyGridAdapter(context);
		addvoicegridview.setAdapter(mAdapter);
		LayoutParams params = new LayoutParams(mAdapter.getCount()
				* (cWidth + hSpacing), LayoutParams.WRAP_CONTENT);
		addvoicegridview.setLayoutParams(params);
		addvoicegridview.setColumnWidth(cWidth);
		addvoicegridview.setHorizontalSpacing(hSpacing);
		addvoicegridview.setStretchMode(GridView.NO_STRETCH);
		addvoicegridview.setNumColumns(mAdapter.getCount());

		addvoicegridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					mPlayer.reset();
					mPlayer.setDataSource(mVoicesList.get(position));
					mPlayer.prepare();
					mPlayer.start();
				} catch (IOException e) {
					Log.e(TAG, "播放失败");
				}
			}
		});
		addvoicegridview
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						mVoicesList.remove(arg2);
						mVoicesListname.remove(arg2);
						initGridView();
						return false;
					}
				});
	}

	private void initUi() {
		horizontalScrollView_addvoice.setHorizontalScrollBarEnabled(true);
		initGridView();
		// 添加
		addTaskNoFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) AddZhiliangSendActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				submitXiaoxi();
			}
		});
		addTaskFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mVoicesList.size()==0) {
					Toast.makeText(getApplicationContext(), "请添加文件", 0).show();
				}else{
					//录音
					new FileHelper().submitUploadFile(mVoicesList, loginKey,commitId,"2",host);
					Toast.makeText(getApplicationContext(), "正在上传文件...", 0).show();
					finish();
				}
			}
		});
		sendcancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		addVoice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (commitId.equals("0")) {
					Toast.makeText(getApplicationContext(), "请先保存上面的信息", 0)
					.show();
				}else{
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (mVoicesList.size() >= 1) {
							Toast.makeText(getApplicationContext(), "只能上传一个录音", 0)
							.show();
						} else {
							startVoice();
						}
						break;
					case MotionEvent.ACTION_UP:
						if (mVoicesList.size() >= 1) {
						} else {
							stopVoice();
						}
						break;
					default:
						break;
					}
				}
				return false;
			}
		});

		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) AddZhiliangSendActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		addxiapai_full.setClickable(true);
		addxiapai_full.setOnClickListener(keyboard_hide);
	}

	private void initProject() {
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(AddZhiliangSendActivity.this,
				R.layout.spinner_item, project_names);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		// 将adapter 添加到spinner中
		projectSpinner.setAdapter(adapter);
		// 设置默认选中
		projectSpinner.setSelection(0);
		// 设置默认值
		// channelSpinner.setVisibility(View.VISIBLE);
		projectSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						project = projects.get(arg2).id;
						projectId = projects.get(arg2).id + "";
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

	@SuppressLint("NewApi")
	private void loadProject() {

		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(host+URLs.PROJECTLIST + "?p=1&ps",
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						try {
							ProjectList cList = ProjectList.parseJson(content);
							if (cList.code == 200) {
								projects = cList.getInfo();
								application.saveObject((Serializable) projects,
										"projects");
								project_names = new String[projects.size()];
								int i = 0;
								for (Project cn : projects) {
									project_names[i] = cn.name;
									i++;
								}

								initProject();
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

	private void initSpinner() {
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(AddZhiliangSendActivity.this,
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
						peopleId = people + "";
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
		if (task_name_send.getText().toString().trim()=="") {
			UIHelper.ToastMessage(context, "请输入内容");
			return;
		}
		AbRequestParams params = new AbRequestParams();
		params.put("taskInfo.name", task_name_send.getText().toString());
		System.out.println(peopleSpinner.getSelectedItem().toString());
		params.put("taskInfo.receive_user_id", peopleId);
		params.put("taskInfo.contact", task_tell.getText().toString());
		params.put("taskInfo.remark", task_remark.getText().toString());
//		String medias = "";
//		for (int i = 0; i < media.size(); i++) {
//			if (i==media.size()-1) {
//				medias+=media.get(i);
//			}else{
//				medias+=media.get(i)+",";
//			}
//			
//		}
//		params.put("taskInfo.media", medias);
		params.put("taskInfo.task_type", "2");
		params.put("taskInfo.create_user_id", loginKey);
		params.put("taskInfo.department_id", from + "");
		params.put("taskInfo.status", "0");
		params.put("taskInfo.project_id", projectId);
		params.put("taskInfo.company_id", "0");
		Log.d(TAG, String.format("%s?", host+URLs.ADDSHENPI, params));
		mAbHttpUtil.post(host+URLs.ADDRS, params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							QunfaInfo gList = QunfaInfo.parseJson(content);
							if (gList.code == 200) {
								showToast("发送成功！");
								Qunfa shenpi=gList.getInfo();
								commitId=shenpi.id.toString();
//								finish();
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
//						finish();
						removeProgressDialog();
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
		mAbHttpUtil.get(host+URLs.USERLIST + "?user_id=" + loginKey
				+ "&department_id=" + from + "&type=2",
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						try {
							PeopleList cList = PeopleList.parseJson(content);
							if (cList.code == 200) {
								peoples = cList.getInfo();
								// peoples.remove(Integer.parseInt(loginKey) -
								// 1);
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

	/** 开始录音 */
	private void startVoice() {
		// 设置录音保存路径
		mFileNameShow = UUID.randomUUID().toString();
		mFileName = PATH + mFileNameShow + ".amr";
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			Log.i(TAG, "SD Card is not mounted,It is  " + state + ".");
		}
		File directory = new File(mFileName).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			Log.i(TAG, "Path to file could not be created");
		}
		Toast.makeText(getApplicationContext(), "开始录音", 0).show();
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
		mRecorder.start();
	}

	/** 停止录音 */
	@SuppressLint("NewApi")
	private void stopVoice() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		mVoicesList.add(mFileName);
		mVoicesListname.add(mFileNameShow);
		mAdapter = new MyGridAdapter(AddZhiliangSendActivity.this);
		addvoicegridview.setAdapter(mAdapter);
		initGridView();
//		new FileHelper().submitUploadFile(mVoicesList, loginKey,commitId,"2");
		Toast.makeText(getApplicationContext(), "保存录音" + mFileName, 0).show();
	}

	class MyGridAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater mInflater;

		public MyGridAdapter(Context c) {
			mContext = c;
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mVoicesList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			contentView = mInflater.inflate(R.layout.item_voicelist, null);
			TextView tv = (TextView) contentView.findViewById(R.id.tv_armName);
			tv.setText(mVoicesListname.get(position) + ".amr");
			return contentView;
		}

	}

}
