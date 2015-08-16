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
import android.widget.TextView;
import android.widget.Toast;

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
	private int from;
	
	Context context = null;
	private String loginKey;
	@AbIocView(id = R.id.task_people)
	EditText task_people;
	@AbIocView(id = R.id.addshenpi_full)
	LinearLayout addxiapai_full;
//	@AbIocView(id = R.id.select_shenpi_people)
//	Spinner peopleSpinner;
	/**提交上报**/
	@AbIocView(id = R.id.commitShenpi)
	Button add;
	@AbIocView(id = R.id.task_name)
	EditText task_name;
	@AbIocView(id = R.id.task_tell)
	EditText task_tell;
	/**	确定位置*/
	@AbIocView(id = R.id.findlocal)
	Button findlocal;
	/**显示当前位置*/
	@AbIocView(id = R.id.showlocal)
	TextView showlocal;
	/**添加录音*/
	@AbIocView(id = R.id.addVoicereport)
	Button addVoicereport;
	/**显示录音*/
	@AbIocView(id = R.id.addvoicegridviewreport)
	GridView addvoicegridviewreport;
	@AbIocView(id=R.id.horizontalScrollView_addvoicereport)
	HorizontalScrollView horizontalScrollView_addvoicereport;
	/** 语音列表适配器 */
	private MyGridAdapter mAdapter;
	/** 语音列表 */
	private List<String> mVoicesList;
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
	/**列宽*/
	private int cWidth = 500;
	/**水平间距*/
	private int hSpacing = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		AbTitleBar mAbTitleBar = this.getTitleBar();
		if (from==1) {
			mAbTitleBar.setTitleText("质量检查-上报质检任务");
		}else if(from==2){
			mAbTitleBar.setTitleText("安全检查-上报安全隐患");
		}else{
			mAbTitleBar.setTitleText("执法管理-上报执法任务");
		}
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
		initData(); 
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
	/** 初始化数据 */
	private void initData() {
		mVoicesList = new ArrayList<String>();
		mVoicesListname = new ArrayList<String>();
		mPlayer = new MediaPlayer();
	}
	private void initGridView(){
		MyGridAdapter mAdapter = new MyGridAdapter(context);
		addvoicegridviewreport.setAdapter(mAdapter);
		LayoutParams params = new LayoutParams(mAdapter.getCount()
				* (cWidth + hSpacing), LayoutParams.WRAP_CONTENT);
		addvoicegridviewreport.setLayoutParams(params);
		addvoicegridviewreport.setColumnWidth(cWidth);
		addvoicegridviewreport.setHorizontalSpacing(hSpacing);
		addvoicegridviewreport.setStretchMode(GridView.NO_STRETCH);
		addvoicegridviewreport.setNumColumns(mAdapter.getCount());

		addvoicegridviewreport.setOnItemClickListener(new OnItemClickListener() {
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
		addvoicegridviewreport.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mVoicesList.remove(arg2);
				mVoicesListname.remove(arg2);
				initGridView();
				return false;
			}
		});
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
		horizontalScrollView_addvoicereport.setHorizontalScrollBarEnabled(true);
		initGridView();
		//确定当前位置
		findlocal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
		//添加上报
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
		//添加录音
		addVoicereport.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mVoicesList.size()>=1) {
						Toast.makeText(getApplicationContext(), "只能上传一个录音", 0).show();
					}else{
						startVoice();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (mVoicesList.size()>=1) {
					}else{
						stopVoice();
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		
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
	/** 开始录音 */
	private void startVoice() {
		// 设置录音保存路径
		mFileNameShow=UUID.randomUUID().toString();
		mFileName = PATH +mFileNameShow + ".amr";
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
		mAdapter = new MyGridAdapter(AddZhiliangReportActivity.this);
		addvoicegridviewreport.setAdapter(mAdapter);
		initGridView();
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
			tv.setText(mVoicesListname.get(position)+ ".amr");
			return contentView;
		}

	}
}